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
import com.cheqi.sdk.models.ReceiptFormat;
import com.cheqi.sdk.models.Recipient;
import com.cheqi.sdk.models.RecipientResolutionResponse;
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
        validateProcessCompleteCreditNoteInputs(identificationDetails, creditNoteTemplateRequest);
        return processCompleteCreditNoteInternal(identificationDetails, creditNoteTemplateRequest, null);
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
        validateProcessCompleteCreditNoteInputs(identificationDetails, creditNoteTemplateRequest);
        validateAccessToken(accessToken);
        return processCompleteCreditNoteInternal(identificationDetails, creditNoteTemplateRequest, accessToken);
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
    public CreditNoteTemplateResponse generateCreditNoteTemplate(
            CreditNoteTemplateGenerationRequest request) throws CheqiSDKException {

        if (request == null) {
            throw new CheqiSDKException("Credit Note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.debug("Generating credit note template with API key for credit note: {}", request.getCreditNoteTemplateRequest().getDocumentNumber());

        try {
            CreditNoteTemplateResponse template = apiClient.generateCreditNoteTemplate(request);
            logger.debug("Credit Note template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating credit note template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate credit note template: " + e.getMessage(), e);
        }
    }

    public CreditNoteTemplateResponse generateCreditNoteTemplate(
            CreditNoteTemplateGenerationRequest request,
            String accessToken) throws CheqiSDKException {

        // Validate request data
        if (request == null) {
            throw new CheqiSDKException("Credit Note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token is required for Credit Note template generation",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Generating Credit Note template for credit note: {}", request.getCreditNoteTemplateRequest().getDocumentNumber());

        try {
            CreditNoteTemplateResponse template = apiClient.generateCreditNoteTemplate(request, accessToken);
            logger.debug("Credit Note template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Unexpected error generating credit note template: {}", e.getMessage(), e);
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
            throw new CheqiSDKException("Failed to send encrypted credit notes: " + e.getMessage(), e);
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
     * This method takes a receipt template and encrypts it for each of the customer's
     * registered devices using their respective public keys. Each device will receive
     * its own encrypted version that can only be decrypted with its private key.
     *
     * @param creditNoteEnvelope The receipt template in JSON format
     * @param recipient Map of device IDs to their public key information
     * @return List of encrypted receipts, one per device
     * @throws CheqiSDKException if encryption fails or parameters are invalid
     */
    public EncryptedCreditNote createEncryptedCreditNote(
            String creditNoteEnvelope,
            Recipient recipient ) throws CheqiSDKException {

        if (creditNoteEnvelope == null || creditNoteEnvelope.trim().isEmpty()) {
            throw new CheqiSDKException("Purchase receipt cannot be null or empty",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (recipient == null) {
            throw new CheqiSDKException("Recipients cannot be null or empty",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
        try {
            EncryptedCreditNote encryptedReceipts = encryptionService.encryptCreditNoteForRecipient(
                    creditNoteEnvelope,
                    recipient
            );
            return encryptedReceipts;
        } catch (Exception e) {
            logger.error("Unexpected error creating encrypted receipts: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create encrypted receipts: " + e.getMessage(), e);
        }
    }

    /**
     * Builds a ReceiptEnvelope containing only the formats accepted by the given recipient.
     */
    private CreditNoteEnvelope buildEnvelopeForRecipient(
            Recipient recipient,
            CreditNoteTemplateResponse creditNoteTemplateResponse) throws Exception {

        CreditNoteEnvelope envelope = new CreditNoteEnvelope();
        List<ReceiptFormat> acceptedFormats = recipient.getAcceptedFormats();

        if (acceptedFormats.contains(ReceiptFormat.CHEQI) && creditNoteTemplateResponse.getCheqi() != null) {
            envelope.setCheqiCreditNote(creditNoteTemplateResponse.getCheqi());
        }

        if (acceptedFormats.contains(ReceiptFormat.UBL_XML) && creditNoteTemplateResponse.getUbl() != null) {
            envelope.setUblXml(creditNoteTemplateResponse.getUbl());
        }

        return envelope;
    }

    private EncryptedCreditNote buildEncryptedCreditNote(
            Recipient recipient,
            String envelopeJson) throws CheqiSDKException {

        try {
            logger.debug("Encrypting creditNote envelope for recipient: {}", recipient.getId());

            // Use encryption service to encrypt the envelope for this recipient
            EncryptedCreditNote encryptedCreditNote = encryptionService.encryptCreditNoteForRecipient(envelopeJson, recipient);

            return EncryptedCreditNote.builder()
                    .recipientId(encryptedCreditNote.getRecipientId())
                    .receiverType(encryptedCreditNote.getReceiverType())
                    .receiptFormats(recipient.getAcceptedFormats())
                    .encryptedCreditNote(encryptedCreditNote.getEncryptedCreditNote())
                    .encryptedSymmetricKey(encryptedCreditNote.getEncryptedSymmetricKey())
                    .publicKey(encryptedCreditNote.getPublicKey())
                    .build();

        } catch (Exception e) {
            logger.error("Failed to encrypt receipt for recipient {}: {}", recipient.getId(), e.getMessage());
            throw new CheqiSDKException("Failed to encrypt receipt for recipient: " + recipient.getId(), e);
        }
    }

    private CreditNoteResult processCompleteCreditNoteInternal(
            IdentificationDetails identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest,
            String accessToken
    ) throws CheqiSDKException {
        String originalReceiptId = identificationDetails.getCheqiReceiptId();
        if (originalReceiptId == null || originalReceiptId.isEmpty()) {
            throw new CheqiSDKException(
                    "Original receipt ID (cheqiReceiptId) is required for credit note processing",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        boolean withAccessToken = accessToken != null;
        String authMode = withAccessToken ? "access token" : "API key";

        try {
            logger.debug("Matching customer with {}", authMode);
            RecipientResolutionResponse matchResponse = withAccessToken
                    ? matchingService.matchCustomer(identificationDetails, accessToken)
                    : matchingService.matchCustomer(identificationDetails);

            if (!matchResponse.isCustomerFound()
                    && (identificationDetails.getRecipientEmail() == null || identificationDetails.getRecipientEmail().isEmpty())) {
                logger.info("Customer not found and no email provided - skipping template generation as no recipient is known");
                return CreditNoteResult.customerNotFound();
            }

            List<ReceiptFormat> determinedFormats = matchResponse.getRecipients().stream()
                    .flatMap(r -> r.getAcceptedFormats().stream())
                    .distinct()
                    .collect(Collectors.toList());

            CreditNoteTemplateGenerationRequest generationRequest =
                    new CreditNoteTemplateGenerationRequest(creditNoteTemplateRequest, determinedFormats);

            logger.debug("Generating credit note template with {}", authMode);
            CreditNoteTemplateResponse templateResponse = withAccessToken
                    ? generateCreditNoteTemplate(generationRequest, accessToken)
                    : generateCreditNoteTemplate(generationRequest);

            if (!matchResponse.isCustomerFound()) {
                logger.info("Customer not found but email provided - credit note template generated but not sent");
                return CreditNoteResult.customerNotFound();
            }

            Set<EncryptedCreditNote> encryptedCreditNotes = buildEncryptedCreditNotes(matchResponse, templateResponse);
            logger.debug("Sending {} encrypted credit notes to backend with {}", encryptedCreditNotes.size(), authMode);

            String templateContent = templateResponse.getCheqi() != null
                    ? objectMapper.writeValueAsString(templateResponse.getCheqi())
                    : templateResponse.getUbl();
            String templateHash = HashUtils.sha256Hex(templateContent);

            CreditNoteCreatedResponse createdResponse = withAccessToken
                    ? sendEncryptedCreditNotes(originalReceiptId, encryptedCreditNotes, templateHash, matchResponse, accessToken)
                    : sendEncryptedCreditNotes(originalReceiptId, encryptedCreditNotes, templateHash, matchResponse);

            logger.info("Credit note processing completed successfully: {} credit notes created for {} recipients",
                    encryptedCreditNotes.size(), matchResponse.getRecipients().size());
            return CreditNoteResult.deliveredToApp(createdResponse, templateContent);
        } catch (CheqiSDKException e) {
            logger.error("Credit note processing failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during credit note processing: {}", e.getMessage(), e);
            throw new CheqiSDKException("Credit note processing failed: " + e.getMessage(), e);
        }
    }

    private Set<EncryptedCreditNote> buildEncryptedCreditNotes(
            RecipientResolutionResponse matchResponse,
            CreditNoteTemplateResponse templateResponse
    ) throws Exception {
        Set<EncryptedCreditNote> encryptedCreditNotes = new HashSet<>();
        for (Recipient recipient : matchResponse.getRecipients()) {
            CreditNoteEnvelope envelope = buildEnvelopeForRecipient(recipient, templateResponse);
            logger.debug("Encrypting envelope for recipient: {} with formats: {}", recipient.getId(), recipient.getAcceptedFormats());
            String envelopeJson = objectMapper.writeValueAsString(envelope);
            encryptedCreditNotes.add(buildEncryptedCreditNote(recipient, envelopeJson));
        }
        return encryptedCreditNotes;
    }

    private void validateProcessCompleteCreditNoteInputs(
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
    }

    private void validateAccessToken(String accessToken) throws CheqiSDKException {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }
    }
}