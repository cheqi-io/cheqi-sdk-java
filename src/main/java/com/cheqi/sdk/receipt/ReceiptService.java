package com.cheqi.sdk.receipt;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.*;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.utils.HashUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for handling receipt operations in the Cheqi SDK.
 *
 * Provides receipt template generation, encrypted receipt creation,
 * and integration with backend receipt processing APIs.
 *
 * All public methods accept an optional {@code accessToken} parameter.
 * Pass {@code null} to use the API key configured during SDK initialization.
 */
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;

    public ReceiptService(CheqiApiClient apiClient, EncryptionService encryptionService, MatchingService matchingService) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(encryptionService, "encryptionService cannot be null");
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
        logger.info("ReceiptService initialized successfully");
    }

    // ===== PUBLIC API =====

    /**
     * Complete receipt processing workflow:
     * 1. Matches customer using payment details
     * 2. If customer not found AND no email provided, returns early
     * 3. Generates receipt template (only if deliverable via app or email)
     * 4. If customer found: creates encrypted receipts and sends to backend
     * 5. If customer not found but email provided: sends receipt via email
     *
     * @param identificationDetails Payment information for customer matching
     * @param receiptRequest Receipt template generation request
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @param notificationDisplayCode Optional barcode for push notification
     * @return ReceiptResult with success status and archival data
     */
    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest,
            String accessToken,
            NotificationDisplayCode notificationDisplayCode) throws CheqiSDKException {

        requireNonNull(identificationDetails, "PaymentDetails");
        requireNonNull(receiptRequest, "ReceiptTemplateRequest");
        if (accessToken != null) {
            requireNonEmpty(accessToken, "Access token");
        }

        try {
            RecipientResolutionResponse matchResponse = matchCustomer(identificationDetails, accessToken);

            if (!matchResponse.getCustomerFound() && isBlank(identificationDetails.getRecipientEmail())) {
                logger.info("Customer not found and no email provided - skipping template generation");
                return ReceiptResult.customerNotFound();
            }

            List<ReceiptFormat> formats = determineFormats(matchResponse);
            ReceiptTemplateRequest enrichedRequest = enrichWithVatContext(receiptRequest, matchResponse);
            ReceiptTemplateResponse templateResponse = generateReceiptTemplate(enrichedRequest, formats, accessToken);

            if (matchResponse.getCustomerFound()) {
                return deliverEncryptedReceipts(matchResponse, templateResponse, notificationDisplayCode, accessToken);
            }

            logger.info("Customer not found, sending receipt via email");
            return sendReceiptViaEmail(identificationDetails.getRecipientEmail(), templateResponse.getCheqi(), accessToken);

        } catch (CheqiSDKException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Receipt processing failed: {}", e.getMessage(), e);
            throw new CheqiSDKException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest,
            String accessToken) throws CheqiSDKException {
        return processCompleteReceipt(identificationDetails, receiptRequest, accessToken, null);
    }

    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest) throws CheqiSDKException {
        return processCompleteReceipt(identificationDetails, receiptRequest, null, null);
    }

    public ReceiptResult processCompleteReceipt(
            IdentificationDetails identificationDetails,
            ReceiptTemplateRequest receiptRequest,
            NotificationDisplayCode notificationDisplayCode) throws CheqiSDKException {
        return processCompleteReceipt(identificationDetails, receiptRequest, null, notificationDisplayCode);
    }

    /**
     * Generates a receipt template from transaction data.
     *
     * @param request Receipt template request containing transaction details
     * @param receiptFormats Formats to generate (CHEQI, UBL_PURCHASE_RECEIPT)
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return Generated receipt template response
     */
    public ReceiptTemplateResponse generateReceiptTemplate(
            ReceiptTemplateRequest request,
            List<ReceiptFormat> receiptFormats,
            String accessToken) throws CheqiSDKException {

        requireNonNull(request, "Receipt template request");

        logger.debug("Generating receipt template for receiptId: {}", request.getDocumentNumber());
        ReceiptTemplateGenerationRequest generationRequest = buildGenerationRequest(request, receiptFormats);

        try {
            ReceiptTemplateResponse template = (accessToken != null)
                    ? apiClient.generateReceiptTemplate(generationRequest, receiptFormats, accessToken)
                    : apiClient.generateReceiptTemplate(generationRequest, receiptFormats);
            logger.debug("Receipt template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Failed to generate receipt template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate receipt template: " + e.getMessage(), e);
        }
    }

    public ReceiptTemplateResponse generateReceiptTemplate(
            ReceiptTemplateRequest request, List<ReceiptFormat> receiptFormats) throws CheqiSDKException {
        return generateReceiptTemplate(request, receiptFormats, null);
    }

    /**
     * Sends encrypted receipts to the backend for delivery.
     *
     * @param encryptedReceipts Encrypted receipts, one per customer device
     * @param templateHash SHA-256 hash of the template content
     * @param recipientResolutionResponse Customer match response with match ID
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return Receipt created response from the backend
     */
    public ReceiptCreatedResponse sendEncryptedReceipts(
            Set<EncryptedReceiptRequest> encryptedReceipts,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse,
            String accessToken) throws CheqiSDKException {

        requireNonNull(encryptedReceipts, "Encrypted receipts");
        if (encryptedReceipts.isEmpty()) {
            throw validationError("Encrypted receipts cannot be empty");
        }
        requireNonEmpty(templateHash, "Template hash");
        requireNonNull(recipientResolutionResponse, "Customer match response");
        if (!recipientResolutionResponse.getCustomerFound()) {
            throw new CheqiSDKException("Cannot send receipts: customer was not found during matching",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND, 400, null);
        }

        logger.debug("Sending {} encrypted receipts to backend", encryptedReceipts.size());

        try {
            ReceiptCreatedResponse response = (accessToken != null)
                    ? apiClient.sendEncryptedReceipts(recipientResolutionResponse.getMatchId(), encryptedReceipts, templateHash, accessToken)
                    : apiClient.sendEncryptedReceipts(recipientResolutionResponse.getMatchId(), encryptedReceipts, templateHash);
            logger.info("Successfully sent {} encrypted receipts", encryptedReceipts.size());
            return response;
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted receipts: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
        }
    }

    public ReceiptCreatedResponse sendEncryptedReceipts(
            Set<EncryptedReceiptRequest> encryptedReceipts,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse) throws CheqiSDKException {
        return sendEncryptedReceipts(encryptedReceipts, templateHash, recipientResolutionResponse, null);
    }

    /**
     * Sends a receipt via email (fallback when customer not found in Cheqi).
     *
     * @param customerEmail Email address to send receipt to
     * @param cheqiReceipt The receipt to send
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return ReceiptResult indicating email delivery status
     */
    public ReceiptResult sendReceiptViaEmail(
            String customerEmail,
            CheqiReceipt cheqiReceipt,
            String accessToken) throws CheqiSDKException {

        requireNonEmpty(customerEmail, "Customer email");
        if (!customerEmail.contains("@")) {
            throw validationError("Valid customer email is required");
        }
        requireNonNull(cheqiReceipt, "Purchase receipt");

        logger.debug("Sending receipt via email to: {}", customerEmail);

        try {
            if (accessToken != null) {
                apiClient.sendReceiptViaEmail(customerEmail, cheqiReceipt, accessToken);
            } else {
                apiClient.sendReceiptViaEmail(customerEmail, cheqiReceipt);
            }
            logger.info("Successfully sent receipt via email to: {}", customerEmail);
            return ReceiptResult.deliveredViaEmail(customerEmail);
        } catch (CheqiApiException e) {
            logger.error("Failed to send receipt via email: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send receipt via email: " + e.getMessage(), e);
        }
    }

    public ReceiptResult sendReceiptViaEmail(
            String customerEmail, CheqiReceipt cheqiReceipt) throws CheqiSDKException {
        return sendReceiptViaEmail(customerEmail, cheqiReceipt, null);
    }

    /**
     * Creates an encrypted receipt for a single recipient.
     *
     * @param purchaseReceipt The receipt template in JSON format
     * @param matchedRecipient MatchedRecipient with public key information
     * @return Encrypted receipt
     */
    public EncryptedReceiptRequest createEncryptedReceipts(
            String purchaseReceipt,
            MatchedRecipient matchedRecipient) throws CheqiSDKException {

        requireNonEmpty(purchaseReceipt, "Purchase receipt");
        requireNonNull(matchedRecipient, "matchedRecipient");

        try {
            return encryptionService.encryptReceiptForRecipients(purchaseReceipt, matchedRecipient);
        } catch (Exception e) {
            logger.error("Failed to create encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create encrypted receipts: " + e.getMessage(), e);
        }
    }

    // ===== PRIVATE HELPERS =====

    private RecipientResolutionResponse matchCustomer(
            IdentificationDetails identificationDetails, String accessToken) throws CheqiApiException {
        logger.debug("Matching customer using payment details with {}", accessToken != null ? "access token" : "API key");
        return (accessToken != null)
                ? matchingService.matchCustomer(identificationDetails, accessToken)
                : matchingService.matchCustomer(identificationDetails);
    }

    private List<ReceiptFormat> determineFormats(RecipientResolutionResponse matchResponse) {
        return matchResponse.getRecipients().stream()
                .flatMap(r -> r.getAcceptedFormats() != null ? r.getAcceptedFormats().stream() : java.util.stream.Stream.empty())
                .distinct()
                .collect(Collectors.toList());
    }

    private ReceiptTemplateRequest enrichWithVatContext(
            ReceiptTemplateRequest request, RecipientResolutionResponse matchResponse) {
        return ReceiptTemplateRequest.builder()
                .from(request)
                .buyerCountryCode(request.getBuyerCountryCode() != null
                        ? request.getBuyerCountryCode()
                        : matchResponse.getBuyerCountryCode())
                .buyerType(request.getBuyerType() != null
                        ? request.getBuyerType()
                        : matchResponse.getBuyerType())
                .taxesApplied(request.getTaxesApplied())
                .build();
    }

    private ReceiptTemplateGenerationRequest buildGenerationRequest(
            ReceiptTemplateRequest request, List<ReceiptFormat> formats) {
        ReceiptTemplateGenerationRequest req = new ReceiptTemplateGenerationRequest();
        req.setReceiptTemplateRequest(request);
        req.setFormats(formats);
        req.setBuyerCountryCode(request.getBuyerCountryCode());
        req.setBuyerType(request.getBuyerType());
        req.setTaxesApplied(request.getTaxesApplied());
        return req;
    }

    private ReceiptResult deliverEncryptedReceipts(
            RecipientResolutionResponse matchResponse,
            ReceiptTemplateResponse templateResponse,
            NotificationDisplayCode notificationDisplayCode,
            String accessToken) throws Exception {

        logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());

        ReceiptEnvelope envelope = new ReceiptEnvelope();
        envelope.setCheqi(templateResponse.getCheqi());
        envelope.setUblPurchaseReceipt(templateResponse.getUblPurchaseReceipt());
        envelope.setUblInvoice(templateResponse.getUblInvoice());
        envelope.setVatMetaData(templateResponse.getVatMetadata());

        String envelopeJson = objectMapper.writeValueAsString(envelope);
        Set<EncryptedReceiptRequest> encryptedReceipts = new HashSet<>();
        for (MatchedRecipient recipient : matchResponse.getRecipients()) {
            logger.debug("Encrypting envelope for recipient: {} with formats: {}", recipient.getId(), recipient.getAcceptedFormats());
            encryptedReceipts.add(encryptionService.encryptReceiptForRecipients(envelopeJson, recipient));
        }

        String templateContent = templateResponse.getCheqi() != null
                ? objectMapper.writeValueAsString(templateResponse.getCheqi())
                : templateResponse.getUblPurchaseReceipt();
        String templateHash = HashUtils.sha256Hex(templateContent);

        logger.debug("Sending {} encrypted receipts to backend", encryptedReceipts.size());
        ReceiptCreatedResponse response = sendEncryptedReceipts(encryptedReceipts, templateHash, matchResponse, accessToken);

        logger.info("Receipt processing completed successfully");
        return ReceiptResult.deliveredToApp(response, templateContent);
    }

    // ===== VALIDATION =====

    private static void requireNonNull(Object value, String name) throws CheqiSDKException {
        if (value == null) {
            throw validationError(name + " cannot be null");
        }
    }

    private static void requireNonEmpty(String value, String name) throws CheqiSDKException {
        if (value == null || value.trim().isEmpty()) {
            throw validationError(name + " cannot be null or empty");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static CheqiSDKException validationError(String message) {
        return new CheqiSDKException(message, CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
    }
}
