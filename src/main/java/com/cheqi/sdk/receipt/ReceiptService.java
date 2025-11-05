package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.*;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
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
 * ReceiptTemplateRequestDto request = ReceiptTemplateRequestDto.builder()
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

    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final RFC8785Canonicalizer canonicalizer = new RFC8785Canonicalizer();

    /**
     * Creates a new ReceiptService instance.
     *
     * @param apiClient API client for backend communication
     * @param encryptionService Service for handling encryption operations
     */
    public ReceiptService(CheqiApiClient apiClient, EncryptionService encryptionService, MatchingService matchingService) {
        this.apiClient = apiClient;
        this.encryptionService = encryptionService;
        this.matchingService = matchingService;
    }

    /**
     * Complete receipt processing workflow:
     * 1. Matches customer using payment details
     * 2. Generates receipt template
     * 3. Attaches payment means from matching
     * 4. Creates encrypted receipts for all recipients
     * 5. Sends receipts to backend
     *
     * @param paymentDetails Payment information for customer matching
     * @param receiptRequest Receipt template generation request
     * @param accessToken Valid OAuth access token
     * @return ProcessReceiptResult with success status and details
     */
    public ProcessReceiptResult processCompleteReceipt(
            PaymentDetails paymentDetails,
            ReceiptTemplateRequestDto receiptRequest,
            UUID merchantId,
            String accessToken) throws CheqiSDKException {

        // Input validation
        if (paymentDetails == null) {
            throw new CheqiSDKException("PaymentDetails cannot be null", 
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        
        if (receiptRequest == null) {
            throw new CheqiSDKException("ReceiptTemplateRequestDto cannot be null", 
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

        try {
            // Step 1: Match customer
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(paymentDetails, accessToken);
            if (!matchResponse.isCustomerFound()) {
                return ProcessReceiptResult.customerNotFound();
            }

            // Step 2: Generate receipt template
            String receiptTemplate = generateReceiptTemplate(receiptRequest, accessToken);

            // Step 3: Parse and attach payment means
            ObjectMapper objectMapper = ObjectMapperConfig.getInstance();
            PurchaseReceipt receipt = objectMapper.readValue(receiptTemplate, PurchaseReceipt.class);
            receipt.setPaymentMeans(matchResponse.getPaymentMeans());

            String receiptTemplateJson = objectMapper.writeValueAsString(receipt);
            String canonicalReceiptJson = canonicalizer.canonicalize(receiptTemplateJson);
            String templateHash = HashUtils.sha256Hex(canonicalReceiptJson);

            // Step 4: Create encrypted receipts
            Set<EncryptedReceiptDto> encryptedReceipts = createEncryptedReceipts(
                    canonicalReceiptJson,
                    matchResponse.getRecipients(),
                    merchantId
            );

            // Step 5: Send receipts
            sendEncryptedReceipts(encryptedReceipts, templateHash, matchResponse, accessToken);

            return ProcessReceiptResult.success(encryptedReceipts.size(), matchResponse.getRecipients().size());

        } catch (Exception e) {
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
            ReceiptTemplateRequestDto request, 
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
        
        try {
            // Call backend API to generate receipt template
            return apiClient.generateReceiptTemplate(request, accessToken);
        } catch (Exception e) {
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
        
        try {
            // Use encryption service to create encrypted receipts for all devices
            return encryptionService.encryptReceiptForRecipients(
                purchaseReceipt,
                recipients,
                supplierPartyId
            );
        } catch (Exception e) {
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

        try {
            // Call backend API to send encrypted receipts
            apiClient.sendEncryptedReceipts(
                    encryptedReceipts,
                    templateHash,
                    accessToken
            );
        } catch (Exception e) {
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
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
    public ValidationResult validateReceiptRequest(ReceiptTemplateRequestDto request) {
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
     * Result of receipt request validation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, List.of());
        }

        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, List.of(error));
        }

        public static ValidationResult invalid(List<String> errors) {
            return new ValidationResult(false, errors);
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
