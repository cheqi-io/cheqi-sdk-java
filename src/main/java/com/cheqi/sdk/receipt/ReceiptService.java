package com.cheqi.sdk.receipt;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.*;
import com.cheqi.sdk.models.ubl.PurchaseReceipt;
import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
     * 2. If customer not found AND no email provided, returns early (saves template generation)
     * 3. Generates receipt template (only if deliverable via app or email)
     * 4. If customer found: creates encrypted receipts and sends to backend
     * 5. If customer not found but email provided: sends receipt via email
     * @param identificationDetails Payment information for customer matching
     * @param receiptRequest Receipt template generation request
     * @param accessToken Valid OAuth access token
     * @return ReceiptResult with success status and archival data
     */
    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest,
            String accessToken) throws CheqiSDKException {

        if (identificationDetails == null) {
            throw new CheqiSDKException("PaymentDetails cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (receiptRequest == null) {
            throw new CheqiSDKException("ReceiptTemplateRequest cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        try {
            logger.debug("Matching customer using payment details");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(identificationDetails, accessToken);

            if (!matchResponse.isCustomerFound() && identificationDetails.getRecipientEmail().isEmpty()) {
                logger.info("Customer not found and no email provided - skipping template generation");
                return ReceiptResult.customerNotFound();
            }

            logger.debug("Generating receipt template");
            String receiptTemplate = generateReceiptTemplate(receiptRequest, accessToken);

            PurchaseReceipt receipt = objectMapper.readValue(receiptTemplate, PurchaseReceipt.class);
            receipt.setPaymentMeans(matchResponse.getPaymentMeans());

            if (matchResponse.isCustomerFound()) {
                String receiptTemplateJson = objectMapper.writeValueAsString(receipt);
                String canonicalReceiptJson = canonicalizer.canonicalize(receiptTemplateJson);
                String templateHash = HashUtils.sha256Hex(canonicalReceiptJson);

                logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());
                Set<EncryptedReceiptRequestDto> encryptedReceipts = createEncryptedReceipts(
                        canonicalReceiptJson,
                        matchResponse.getRecipients()
                );

                logger.debug("Sending {} encrypted receipts to backend", encryptedReceipts.size());
                ReceiptCreatedResponse receiptCreatedResponse = sendEncryptedReceipts(encryptedReceipts, templateHash, matchResponse, accessToken);

                logger.info("Receipt processing completed successfully");
                return ReceiptResult.deliveredToApp(receiptCreatedResponse, canonicalReceiptJson);
            }

            logger.info("Customer not found, sending receipt via email");
            return sendReceiptViaEmail(identificationDetails.getRecipientEmail().get(), receipt, accessToken);

        } catch (CheqiSDKException e) {
            logger.error("Receipt processing failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during receipt processing: {}", e.getMessage(), e);
            throw new CheqiSDKException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Complete receipt processing workflow using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     * For companies accessing their own data directly.
     */
    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest) throws CheqiSDKException {

        if (identificationDetails == null) {
            throw new CheqiSDKException("PaymentDetails cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (receiptRequest == null) {
            throw new CheqiSDKException("ReceiptTemplateRequest cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        try {
            logger.debug("Matching customer using payment details with API key");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(identificationDetails);

            if (!matchResponse.isCustomerFound() && identificationDetails.getRecipientEmail().isEmpty()) {
                logger.info("Customer not found and no email provided - skipping template generation");
                return ReceiptResult.customerNotFound();
            }

            logger.debug("Generating receipt template with API key");
            String receiptTemplate = generateReceiptTemplate(receiptRequest);

            PurchaseReceipt receipt = objectMapper.readValue(receiptTemplate, PurchaseReceipt.class);
            receipt.setPaymentMeans(matchResponse.getPaymentMeans());

            if (matchResponse.isCustomerFound()) {
                String receiptTemplateJson = objectMapper.writeValueAsString(receipt);
                String canonicalReceiptJson = canonicalizer.canonicalize(receiptTemplateJson);
                String templateHash = HashUtils.sha256Hex(canonicalReceiptJson);

                logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());
                Set<EncryptedReceiptRequestDto> encryptedReceipts = createEncryptedReceipts(
                        canonicalReceiptJson,
                        matchResponse.getRecipients()
                );

                logger.debug("Sending {} encrypted receipts to backend with API key", encryptedReceipts.size());
                ReceiptCreatedResponse receiptCreatedResponse = sendEncryptedReceipts(encryptedReceipts, templateHash, matchResponse);

                logger.info("Receipt processing completed successfully");
                return ReceiptResult.deliveredToApp(receiptCreatedResponse, canonicalReceiptJson);
            }

            logger.info("Customer not found, sending receipt via email");
            return sendReceiptViaEmail(identificationDetails.getRecipientEmail().get(), receipt);

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

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token is required for receipt template generation",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Generating receipt template for receiptId: {}", request.getDocumentNumber());

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
     * Generates a receipt template using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     * For companies accessing their own data directly.
     */
    public String generateReceiptTemplate(
            ReceiptTemplateRequest request) throws CheqiSDKException {

        if (request == null) {
            throw new CheqiSDKException("Receipt template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.debug("Generating receipt template with API key for receiptId: {}", request.getDocumentNumber());

        try {
            String template = apiClient.generateReceiptTemplate(request);
            logger.debug("Receipt template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating receipt template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate receipt template: " + e.getMessage(), e);
        }
    }


    /**
     * Sends encrypted receipts using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     */
    public ReceiptCreatedResponse sendEncryptedReceipts(
            Set<EncryptedReceiptRequestDto> encryptedReceipts,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse) throws CheqiSDKException {

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

        logger.debug("Sending {} encrypted receipts to backend with API key", encryptedReceipts.size());

        try {
            ReceiptCreatedResponse response = apiClient.sendEncryptedReceipts(
                    recipientResolutionResponse.getMatchId(),
                    encryptedReceipts,
                    templateHash
            );
            logger.info("Successfully sent {} encrypted receipts", encryptedReceipts.size());
            return response;
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted receipts: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
        }
    }

    /**
     * Sends receipt via email using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     */
    public ReceiptResult sendReceiptViaEmail(
            String customerEmail,
            PurchaseReceipt purchaseReceipt) throws CheqiSDKException {

        if (customerEmail == null || customerEmail.trim().isEmpty() || !customerEmail.contains("@")) {
            throw new CheqiSDKException("Valid customer email is required",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (purchaseReceipt == null) {
            throw new CheqiSDKException("Purchase receipt cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.debug("Sending receipt via email to: {}", customerEmail);

        try {
            apiClient.sendReceiptViaEmail(customerEmail, purchaseReceipt);
            logger.info("Successfully sent receipt via email to: {}", customerEmail);
            return ReceiptResult.deliveredViaEmail(customerEmail);
        } catch (CheqiApiException e) {
            logger.error("Failed to send receipt via email: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending receipt via email: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
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
     * @return List of encrypted receipts, one per device
     * @throws CheqiSDKException if encryption fails or parameters are invalid
     */
    public Set<EncryptedReceiptRequestDto> createEncryptedReceipts(
            String purchaseReceipt,
            List<Recipient> recipients ) throws CheqiSDKException {
        
        if (purchaseReceipt == null || purchaseReceipt.trim().isEmpty()) {
            throw new CheqiSDKException("Purchase receipt cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (recipients == null || recipients.isEmpty()) {
            throw new CheqiSDKException("Recipients cannot be null or empty", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        logger.debug("Creating encrypted receipts for {} recipients", recipients.size());
        
        try {
            Set<EncryptedReceiptRequestDto> encryptedReceipts = encryptionService.encryptReceiptForRecipients(
                purchaseReceipt,
                recipients
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
    public ReceiptCreatedResponse sendEncryptedReceipts(
            Set<EncryptedReceiptRequestDto> encryptedReceipts,
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
            ReceiptCreatedResponse response = apiClient.sendEncryptedReceipts(
                    recipientResolutionResponse.getMatchId(),
                    encryptedReceipts,
                    templateHash,
                    accessToken
            );
            logger.info("Successfully sent {} encrypted receipts", encryptedReceipts.size());
            return response;
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
    public ReceiptResult sendReceiptViaEmail(
            String customerEmail,
            PurchaseReceipt purchaseReceipt,
            String accessToken) throws CheqiSDKException {

        if (customerEmail == null || customerEmail.trim().isEmpty() || !customerEmail.contains("@")) {
            throw new CheqiSDKException("Valid customer email is required",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (purchaseReceipt == null) {
            throw new CheqiSDKException("Purchase receipt cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Sending receipt via email to: {}", customerEmail);

        try {
            apiClient.sendReceiptViaEmail(customerEmail, purchaseReceipt, accessToken);
            logger.info("Successfully sent receipt via email to: {}", customerEmail);
            return ReceiptResult.deliveredViaEmail(customerEmail);
        } catch (CheqiApiException e) {
            logger.error("Failed to send receipt via email: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending receipt via email: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
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
