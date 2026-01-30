package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.decryption.DecryptedCreditNote;
import com.cheqi.sdk.decryption.DecryptionException;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.exceptions.CreditNoteProcessingException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.IdentificationDetails;
import com.cheqi.sdk.models.Recipient;
import com.cheqi.sdk.models.RecipientResolutionResponse;
import com.cheqi.sdk.models.ubl.CreditNote;
import com.cheqi.sdk.utils.HashUtils;
import com.cheqi.sdk.utils.RFC8785Canonicalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Service for processing credit note requests.
 * 
 * This service handles:
 * - Credit note generation and encryption for returns/refunds
 * - Decryption of encrypted credit note requests from customers
 * - Customer identification via cheqiReceiptId (links to original receipt)
 * - Bidirectional encrypted communication (merchant ↔ customer)
 * 
 * Note: Credit notes identify customers using the cheqiReceiptId from the original receipt,
 * not payment details like card PAR or IBAN. The cheqiReceiptId automatically resolves
 * to the customer who received the original receipt.
 */
public class CreditNoteService {
    private static final Logger logger = LoggerFactory.getLogger(CreditNoteService.class);
    
    private final DecryptionService decryptionService;
    private final ObjectMapper objectMapper;
    private final MatchingService matchingService;
    private final CheqiApiClient apiClient;
    private final RFC8785Canonicalizer canonicalizer = new RFC8785Canonicalizer();
    private final EncryptionService encryptionService;

    public CreditNoteService(CheqiApiClient apiClient, EncryptionService encryptionService, MatchingService matchingService) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(encryptionService, "encryptionService cannot be null");
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.decryptionService = new DecryptionService();
        this.objectMapper = ObjectMapperConfig.getInstance();
        logger.info("CreditNoteService initialized successfully");
    }

    /**
     * Process complete credit note using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     */
    public CreditNoteResult processCompleteCreditNote(
            IdentificationDetails identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest
    ) throws CheqiSDKException {

        if (identificationDetails == null) {
            throw new CheqiSDKException("Identification details cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (creditNoteTemplateRequest == null) {
            throw new CheqiSDKException("Credit note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        // Validate that cheqiReceiptId is present for credit notes
        String originalReceiptId = identificationDetails.getCheqiReceiptId()
                .orElseThrow(() -> new CheqiSDKException(
                        "Original receipt ID (cheqiReceiptId) is required for credit note processing",
                        CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null));

        try {
            logger.debug("Matching customer with API key");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(identificationDetails);

            if (!matchResponse.isCustomerFound() && identificationDetails.getRecipientEmail().isEmpty()) {
                logger.info("Customer not found and no email provided - skipping template generation as no reciepient is known");
                return CreditNoteResult.customerNotFound();
            }

            // Step 3: Generate credit note template
            logger.debug("Generating credit note template with API key");
            String creditNoteTemplate = generateCreditNoteTemplate(creditNoteTemplateRequest);

            CreditNote creditNote = objectMapper.readValue(creditNoteTemplate, CreditNote.class);

            if (matchResponse.isCustomerFound()) {
                String receiptTemplateJson = objectMapper.writeValueAsString(creditNote);
                String canonicalCreditNoteJson = canonicalizer.canonicalize(receiptTemplateJson);
                String templateHash = HashUtils.sha256Hex(canonicalCreditNoteJson);

                logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());
                Set<EncryptedCreditNote> encryptedCreditNoteDtos = createEncryptedCreditNotes(
                        canonicalCreditNoteJson,
                        matchResponse.getRecipients()
                );

                logger.debug("Sending {} encrypted credit notes to backend with API key", encryptedCreditNoteDtos.size());
                CreditNoteCreatedResponse receiptCreatedResponse = sendEncryptedCreditNotes(originalReceiptId, encryptedCreditNoteDtos, templateHash, matchResponse);

                logger.info("Receipt processing completed successfully: {} receipts created for {} recipients",
                        encryptedCreditNoteDtos.size(), matchResponse.getRecipients().size());
                return CreditNoteResult.deliveredToApp(receiptCreatedResponse, canonicalCreditNoteJson);
            } else {
                logger.info("Customer not found but email provided - credit note template generated but not sent");
                return CreditNoteResult.customerNotFound();
            }
        } catch (CheqiSDKException e) {
            logger.error("Receipt processing failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during receipt processing: {}", e.getMessage(), e);
            throw new CheqiSDKException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Process complete credit note using OAuth2 access token.
     * For third-party integrators accessing merchant data via OAuth2 authorization.
     */
    public CreditNoteResult processCompleteCreditNote(
            IdentificationDetails identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest,
            String accessToken
    ) throws CheqiSDKException {

        if (identificationDetails == null) {
            throw new CheqiSDKException("Identification details cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (creditNoteTemplateRequest == null) {
            throw new CheqiSDKException("Credit note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        // Validate that cheqiReceiptId is present for credit notes
        String originalReceiptId = identificationDetails.getCheqiReceiptId()
                .orElseThrow(() -> new CheqiSDKException(
                        "Original receipt ID (cheqiReceiptId) is required for credit note processing",
                        CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null));

        try {
            logger.debug("Matching customer with access token");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(identificationDetails, accessToken);

            if (!matchResponse.isCustomerFound() && identificationDetails.getRecipientEmail().isEmpty()) {
                logger.info("Customer not found and no email provided - skipping template generation as no recipient is known");
                return CreditNoteResult.customerNotFound();
            }

            logger.debug("Generating credit note template with access token");
            String creditNoteTemplate = generateCreditNoteTemplate(creditNoteTemplateRequest, accessToken);

            CreditNote creditNote = objectMapper.readValue(creditNoteTemplate, CreditNote.class);

            if (matchResponse.isCustomerFound()) {
                String creditNoteTemplateJson = objectMapper.writeValueAsString(creditNote);
                String canonicalCreditNoteJson = canonicalizer.canonicalize(creditNoteTemplateJson);
                String templateHash = HashUtils.sha256Hex(canonicalCreditNoteJson);

                logger.debug("Creating encrypted credit notes for {} recipients", matchResponse.getRecipients().size());
                Set<EncryptedCreditNote> encryptedCreditNoteDtos = createEncryptedCreditNotes(
                        canonicalCreditNoteJson,
                        matchResponse.getRecipients()
                );

                logger.debug("Sending {} encrypted credit notes to backend with access token", encryptedCreditNoteDtos.size());
                CreditNoteCreatedResponse response = sendEncryptedCreditNotes(originalReceiptId, encryptedCreditNoteDtos, templateHash, matchResponse, accessToken);

                logger.info("Credit note processing completed successfully: {} credit notes created for {} recipients",
                        encryptedCreditNoteDtos.size(), matchResponse.getRecipients().size());
                return CreditNoteResult.deliveredToApp(response, canonicalCreditNoteJson);
            } else {
                logger.info("Customer not found but email provided - credit note template generated but not sent");
                return CreditNoteResult.customerNotFound();
            }
        } catch (CheqiSDKException e) {
            logger.error("Credit note processing failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during credit note processing: {}", e.getMessage(), e);
            throw new CheqiSDKException("Credit note processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts and parses an encrypted credit note request.
     *
     * @param encryptedCreditNote The encrypted credit note from webhook or API
     * @return Parsed CreditNoteRequest object ready for processing
     * @throws DecryptionException if decryption fails
     * @throws CreditNoteProcessingException if JSON parsing fails
     */
    public CreditNoteInitiationRequest decryptCreditNoteRequest(
            EncryptedCreditNoteInitiationRequest encryptedCreditNote, String privateKeyBase64) {
        
        logger.debug("Processing credit note request for receipt: {}", 
                encryptedCreditNote.getCheqiReceiptId());
        
        try {
            // Step 1: Decrypt the credit note
            DecryptedCreditNote decryptedCreditNote = decryptionService.decryptCreditNote(
                    encryptedCreditNote,
                    privateKeyBase64
            );
            
            // Step 2: Parse JSON to CreditNoteRequest
            CreditNoteInitiationRequest creditNoteRequest = objectMapper.readValue(
                    decryptedCreditNote.getCreditNoteContentJson(),
                    CreditNoteInitiationRequest.class
            );
            
            logger.info("Successfully processed credit note request for receipt: {}", 
                    creditNoteRequest.getCheqiReceiptId());
            
            return creditNoteRequest;
            
        } catch (DecryptionException e) {
            logger.error("Failed to decrypt credit note: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to parse credit note JSON: {}", e.getMessage());
            throw new CreditNoteProcessingException("Failed to parse credit note request", e);
        }
    }

    /**
     * Generates a receipt template using API credentials (client ID and secret).
     * For companies that want to use API key/secret instead of OAuth access tokens.
     */
    /**
     * Generates a credit note template using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     */
    public String generateCreditNoteTemplate(
            CreditNoteTemplateRequest request) throws CheqiSDKException {

        if (request == null) {
            throw new CheqiSDKException("Credit Note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!request.isValid()) {
            List<String> errors = request.getValidationErrors();
            throw new CheqiSDKException("Invalid Credit Note template request: " + String.join(", ", errors),
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.debug("Generating credit note template with API key for credit note: {}", request.getDocumentNumber());

        try {
            String template = apiClient.generateCreditNoteTemplate(request);
            logger.debug("Credit Note template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating credit note template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate credit note template: " + e.getMessage(), e);
        }
    }

    public String generateCreditNoteTemplate(
            CreditNoteTemplateRequest request,
            String accessToken) throws CheqiSDKException {

        // Validate request data
        if (request == null) {
            throw new CheqiSDKException("Credit Note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!request.isValid()) {
            List<String> errors = request.getValidationErrors();
            throw new CheqiSDKException("Invalid Credit Note template request: " + String.join(", ", errors),
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token is required for Credit Note template generation",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Generating Credit Note template for credit note: {}", request.getDocumentNumber());

        try {
            String template = apiClient.generateCreditNoteTemplate(request, accessToken);
            logger.debug("Credit Note template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating credite template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate credit note template: " + e.getMessage(), e);
        }
    }


    /**
     * Sends encrypted credit notes using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     */
    public CreditNoteCreatedResponse sendEncryptedCreditNotes(
            String parentCheqiReceiptId,
            Set<EncryptedCreditNote> encryptedCreditNotes,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse) throws CheqiSDKException {

        if (encryptedCreditNotes == null || encryptedCreditNotes.isEmpty()) {
            throw new CheqiSDKException("Encrypted credit notes cannot be null or empty",
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
            throw new CheqiSDKException("Cannot send credit notes: customer was not found during matching",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND, 400, null);
        }

        logger.debug("Sending {} encrypted credit notes to backend with API key", encryptedCreditNotes.size());

        try {
            CreditNoteCreatedResponse creditNoteCreatedResponse = apiClient.sendEncryptedCreditNotes(
                    recipientResolutionResponse.getMatchId(),
                    parentCheqiReceiptId,
                    encryptedCreditNotes,
                    templateHash
            );
            logger.info("Successfully sent {} encrypted receipts", encryptedCreditNotes.size());
            return creditNoteCreatedResponse;
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted receipts: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted receipts: {}" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send encrypted receipts: " + e.getMessage(), e);
        }
    }


    /**
     * Sends encrypted credit notes using OAuth2 access token.
     * For third-party integrators accessing merchant data via OAuth2 authorization.
     */
    public CreditNoteCreatedResponse sendEncryptedCreditNotes(
            String parentCheqiReceiptId,
            Set<EncryptedCreditNote> encryptedCreditNotes,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse,
            String accessToken) throws CheqiSDKException {

        if (encryptedCreditNotes == null || encryptedCreditNotes.isEmpty()) {
            throw new CheqiSDKException("Encrypted credit notes cannot be null or empty",
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
            throw new CheqiSDKException("Cannot send credit notes: customer was not found during matching",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Sending {} encrypted credit notes to backend with access token", encryptedCreditNotes.size());

        try {
            CreditNoteCreatedResponse response =apiClient.sendEncryptedCreditNotes(
                    recipientResolutionResponse.getMatchId(),
                    parentCheqiReceiptId,
                    encryptedCreditNotes,
                    templateHash,
                    accessToken
            );
            logger.info("Successfully sent {} encrypted credit notes", encryptedCreditNotes.size());
            return response;
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted credit notes: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted credit notes: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending encrypted credit notes: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to send encrypted credit notes: " + e.getMessage(), e);
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
    public Set<EncryptedCreditNote> createEncryptedCreditNotes(
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
            Set<EncryptedCreditNote> encryptedReceipts = encryptionService.encryptCreditNoteForRecipients(
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
}