package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.DTOs.Recipient;
import com.cheqi.commons.DTOs.RecipientResolutionResponse;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.PaymentDetails;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Service for handling receipt operations in the Cheqi SDK.
 * 
 * This service provides comprehensive receipt management functionality including:
 * - Receipt template generation from transaction data
 * - Encrypted receipt creation for matched customers
 * - Integration with backend receipt processing APIs
 * 
 * The service coordinates between the encryption service and API client to provide
 * end-to-end encrypted receipt processing while maintaining data privacy.
 * 
 * Example usage:
 * <pre>
 * // Generate a receipt template
 * ReceiptTemplateRequest request = ReceiptTemplateRequest.builder()
 *     .receiptId("INV-001")
 *     .issueDate(Instant.now())
 *     .documentCurrencyCode("EUR")
 *     .invoiceSubtotal(new BigDecimal("100.00"))
 *     .totalBeforeTax(new BigDecimal("100.00"))
 *     .totalAmount(new BigDecimal("121.00"))
 *     .products(productList)
 *     .taxBreakDown(taxBreakdown)
 *     .build();
 * 
 * PurchaseReceipt template = receiptService.generateReceiptTemplate(request, accessToken);
 * 
 * // Create encrypted receipts for customer devices
 * List<EncryptedReceiptDto> encryptedReceipts = receiptService.createEncryptedReceipts(
 *     template, deviceKeys, cheqiReceiptId, supplierPartyId);
 * </pre>
 */
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final RFC8785Canonicalizer canonicalizer = new RFC8785Canonicalizer();
    private final ObjectMapper objectMapper;

    /**
     * Creates a new ReceiptService instance.
     *
     * @param apiClient API client for backend communication
     * @param encryptionService Service for handling encryption operations
     */
    public ReceiptService(CheqiApiClient apiClient, EncryptionService encryptionService, MatchingService matchingService) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(encryptionService, "encryptionService cannot be null");
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
        logger.info("ReceiptService initialized successfully");
    }

    /**
     * Complete receipt processing workflow:
     * 1. Matches customer using payment details
     * 2. If customer not found, returns early (saves template generation API call)
     * 3. Generates receipt template (only if customer found)
     * 4. Attaches payment means from matching
     * 5. Creates encrypted receipts for all recipients
     * 6. Sends receipts to backend
     *
     * @param paymentDetails Payment information for customer matching
     * @param receiptRequest Receipt template generation request
     * @param accessToken Valid OAuth access token
     * @return ProcessReceiptResult with success status and details
     */
    public ProcessReceiptResult processCompleteReceipt(
            PaymentDetails paymentDetails,
            ReceiptTemplateRequest receiptRequest,
            UUID merchantId,
            String accessToken) throws CheqiSDKException {

        // Input validation
        if (paymentDetails == null) {
            throw new CheqiSDKException("PaymentDetails cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (receiptRequest == null) {
            throw new CheqiSDKException("ReceiptTemplateRequest cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (merchantId == null) {
            throw new CheqiSDKException("Merchant ID cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty", 
                CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        // Validate required fields in receiptRequest
        ValidationResult validation = validateReceiptRequest(receiptRequest);
        if (!validation.isValid()) {
            throw new CheqiSDKException("Receipt request validation failed: " + String.join(", ", validation.getErrors()), 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.debug("Starting complete receipt processing for merchant: {}", merchantId);
        
        try {
            // Step 1: Match customer FIRST (saves template generation API call if customer not found)
            logger.debug("Matching customer using payment details");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(paymentDetails, accessToken);

            // Step 2: If customer not found, return early (no template generation needed)
            if (!matchResponse.isCustomerFound()) {
                logger.info("Customer not found");
                return ProcessReceiptResult.customerNotFound();
            }

            // Step 3: Customer found - generate receipt template
            logger.info("Customer found with {} recipients", matchResponse.getRecipients().size());
            logger.debug("Generating receipt template for receiptId: {}", receiptRequest.getReceiptId());
            String receiptTemplate = generateReceiptTemplate(receiptRequest, accessToken);

            // Step 4: Parse and attach payment means
            PurchaseReceipt receipt = objectMapper.readValue(receiptTemplate, PurchaseReceipt.class);
            receipt.setPaymentMeans(matchResponse.getPaymentMeans());
            String receiptTemplateJson = objectMapper.writeValueAsString(receipt);
            String canonicalReceiptJson = canonicalizer.canonicalize(receiptTemplateJson);
            String templateHash = HashUtils.sha256Hex(canonicalReceiptJson);

            // Step 5: Create encrypted receipts
            logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());
            Set<EncryptedReceiptDto> encryptedReceipts = createEncryptedReceipts(
                    canonicalReceiptJson,
                    matchResponse.getRecipients(),
                    merchantId
            );

            // Step 6: Send receipts
            logger.debug("Sending {} encrypted receipts to backend", encryptedReceipts.size());
            sendEncryptedReceipts(encryptedReceipts, templateHash, matchResponse, accessToken);

            logger.info("Receipt processing completed successfully: {} receipts created for {} recipients", 
                    encryptedReceipts.size(), matchResponse.getRecipients().size());
            return ProcessReceiptResult.success(encryptedReceipts.size(), matchResponse.getRecipients().size());

        } catch (CheqiSDKException e) {
            logger.error("Receipt processing failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during receipt processing: {}", e.getMessage(), e);
            throw new CheqiSDKException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a receipt template from transaction data.
     * 
     * This method validates the request data and calls the backend API to generate
     * a standardized UBL PurchaseReceipt template that can be used for creating
     * encrypted receipts for customers.
     *
     * @param request Receipt template request containing transaction details
     * @param accessToken OAuth2 access token for API authentication
     * @return Generated PurchaseReceipt template
     * @throws CheqiSDKException if validation fails or API call encounters an error
     */
    public String generateReceiptTemplate(
            ReceiptTemplateRequest request, 
            String accessToken) throws CheqiSDKException {
        
        // Validate request data
        if (request == null) {
            throw new CheqiSDKException("Receipt template request cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (!request.isValid()) {
            List<String> errors = request.getValidationErrors();
            throw new CheqiSDKException("Invalid receipt template request: " + String.join(", ", errors), 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token is required for receipt template generation", 
                CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }
        
        logger.debug("Generating receipt template for receiptId: {}", request.getReceiptId());
        
        try {
            String template = apiClient.generateReceiptTemplate(request, accessToken);
            logger.debug("Receipt template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating receipt template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate receipt template: " + e.getMessage(), e);
        }
    }

    /**
     * Creates encrypted receipts for multiple customer devices.
     * 
     * This method takes a receipt template and encrypts it for each of the customer's
     * registered devices using their respective public keys. Each device will receive
     * its own encrypted version that can only be decrypted with its private key.
     *
     * @param purchaseReceipt The receipt template in JSON format
     * @param recipients Map of device IDs to their public key information
     * @param supplierPartyId Identifier of the supplier creating the receipt
     * @return List of encrypted receipts, one per device
     * @throws CheqiSDKException if encryption fails or parameters are invalid
     */
    public Set<EncryptedReceiptDto> createEncryptedReceipts(
            String purchaseReceipt,
            List<Recipient> recipients,
            UUID supplierPartyId) throws CheqiSDKException {
        
        if (purchaseReceipt == null || purchaseReceipt.trim().isEmpty()) {
            throw new CheqiSDKException("Purchase receipt cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (recipients == null || recipients.isEmpty()) {
            throw new CheqiSDKException("Recipients cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (supplierPartyId == null) {
            throw new CheqiSDKException("Supplier party ID cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        logger.debug("Creating encrypted receipts for {} recipients", recipients.size());
        
        try {
            Set<EncryptedReceiptDto> encryptedReceipts = encryptionService.encryptReceiptForRecipients(
                purchaseReceipt,
                recipients,
                supplierPartyId
            );
            logger.debug("Successfully created {} encrypted receipts", encryptedReceipts.size());
            return encryptedReceipts;
        } catch (Exception e) {
            logger.error("Unexpected error creating encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create encrypted receipts: " + e.getMessage(), e);
        }
    }

    /**
     * Sends encrypted receipts to the backend for delivery to customer devices.
     *
     * This is the final step in the complete receipt delivery flow. It takes the
     * encrypted receipts and customer information from the matching process and
     * sends them to the backend for delivery to the customer's devices.
     *
     * @param encryptedReceipts List of encrypted receipts created for customer devices
     * @param recipientResolutionResponse Response from customer matching containing anonymous ID
     * @param accessToken OAuth2 access token for API authentication
     * @return ReceiptDeliveryResponse confirming successful delivery queuing
     * @throws CheqiSDKException if delivery fails or parameters are invalid
     */
    public void sendEncryptedReceipts(
            Set<EncryptedReceiptDto> encryptedReceipts,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse,
            String accessToken) throws CheqiSDKException {

        // Validate input parameters
        if (encryptedReceipts == null || encryptedReceipts.isEmpty()) {
            throw new CheqiSDKException("Encrypted receipts cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (templateHash == null || templateHash.trim().isEmpty()) {
            throw new CheqiSDKException("Template hash cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (recipientResolutionResponse == null) {
            throw new CheqiSDKException("Customer match response cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!recipientResolutionResponse.isCustomerFound()) {
            throw new CheqiSDKException("Cannot send receipts: customer was not found during matching", 
                CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND, 400, null);
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty", 
                CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Sending {} encrypted receipts to backend", encryptedReceipts.size());
        
        try {
            apiClient.sendEncryptedReceipts(
                    encryptedReceipts,
                    templateHash,
                    accessToken
            );
            logger.info("Successfully sent {} encrypted receipts", encryptedReceipts.size());
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted receipts: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted receipts: {}" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a receipt via email when customer doesn't have Cheqi registration.
     * Uses the receipt template from a previous processCompleteReceipt call.
     *
     * This is the fallback mechanism when customer is not found. The POS system
     * should use the receiptTemplateJson from the ProcessReceiptResult to avoid
     * regenerating the template.
     *
     * @param customerEmail Email address to send receipt to
     * @param purchaseReceipt Purchase receipt to send via email
     * @param accessToken OAuth access token
     * @return ProcessReceiptResult indicating email delivery status
     * @throws CheqiSDKException if email delivery fails
     */
    public ProcessReceiptResult sendReceiptViaEmail(
            String customerEmail,
            PurchaseReceipt purchaseReceipt,
            String accessToken) throws CheqiSDKException {

        // Validate inputs
        if (customerEmail == null || customerEmail.trim().isEmpty() || !customerEmail.contains("@")) {
            throw new CheqiSDKException("Valid email address is required",
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token is required",
                CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.info("Sending receipt via email to: {}", maskEmail(customerEmail));
        
        try {
            apiClient.sendReceiptViaEmail(
                customerEmail,
                purchaseReceipt,
                accessToken
            );

            logger.info("Receipt sent successfully via email to: {}", maskEmail(customerEmail));
            return ProcessReceiptResult.emailSent(customerEmail);

        } catch (CheqiApiException e) {
            logger.error("Failed to send receipt via email: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending receipt via email: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
        }
    }

    /**
     * Validates a receipt template request without generating the template.
     * 
     * This method performs comprehensive validation of the receipt data
     * without making any API calls. Useful for pre-validation before
     * attempting template generation.
     *
     * @param request Receipt template request to validate
     * @return ValidationResult containing validation status and any errors
     */
    public ValidationResult validateReceiptRequest(ReceiptTemplateRequest request) {
        if (request == null) {
            return ValidationResult.invalid("Receipt template request cannot be null");
        }
        
        List<String> errors = request.getValidationErrors();
        if (errors.isEmpty()) {
            return ValidationResult.valid();
        } else {
            return ValidationResult.invalid(errors);
        }
    }

    /**
     * Masks an email address for logging purposes to protect privacy.
     * Example: john.doe@example.com becomes j***e@example.com
     * 
     * @param email Email address to mask
     * @return Masked email address
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        
        String[] parts = email.split("@");
        String localPart = parts[0];
        
        if (localPart.length() <= 2) {
            return "***@" + parts[1];
        }
        
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + parts[1];
    }
}
