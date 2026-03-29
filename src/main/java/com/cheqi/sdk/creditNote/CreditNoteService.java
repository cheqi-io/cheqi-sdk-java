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
import com.cheqi.sdk.models.generated.*;
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
 * Handles credit note generation and encryption for returns/refunds,
 * decryption of encrypted credit note requests from customers,
 * and bidirectional encrypted communication (merchant ↔ customer).
 *
 * All public methods accept an optional {@code accessToken} parameter.
 * Pass {@code null} to use the API key configured during SDK initialization.
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

    // ===== PUBLIC API =====

    /**
     * Complete credit note processing workflow:
     * 1. Matches customer using identification details (cheqiReceiptId)
     * 2. If customer not found AND no email provided, returns early
     * 3. Generates credit note template
     * 4. If customer found: encrypts and sends credit notes to backend
     *
     * @param identificationDetails Identification details (must include cheqiReceiptId)
     * @param creditNoteTemplateRequest Credit note template request
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return CreditNoteResult with success status
     */
    public CreditNoteResult processCompleteCreditNote(
            IdentificationDetails identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest,
            String accessToken) throws CheqiSDKException {

        requireNonNull(identificationDetails, "Identification details");
        requireNonNull(creditNoteTemplateRequest, "Credit note template request");
        if (accessToken != null) {
            requireNonEmpty(accessToken, "Access token");
        }

        String originalReceiptId = identificationDetails.getCheqiReceiptId();
        requireNonEmpty(originalReceiptId, "Original receipt ID (cheqiReceiptId)");

        try {
            RecipientResolutionResponse matchResponse = matchCustomer(identificationDetails, accessToken);

            if (!matchResponse.getCustomerFound() && isBlank(identificationDetails.getRecipientEmail())) {
                logger.info("Customer not found and no email provided - skipping template generation");
                return CreditNoteResult.customerNotFound();
            }

            List<ReceiptFormat> formats = determineFormats(matchResponse);

            CreditNoteTemplateGenerationRequest templateGenerationRequest = new CreditNoteTemplateGenerationRequest();
            templateGenerationRequest.setCreditNoteTemplateRequest(creditNoteTemplateRequest);
            templateGenerationRequest.setFormats(formats);
            templateGenerationRequest.setBuyerType(matchResponse.getBuyerType());
            templateGenerationRequest.setBuyerCountryCode(matchResponse.getBuyerCountryCode());
            templateGenerationRequest.setTaxesApplied(true);

            CreditNoteTemplateResponse templateResponse = generateCreditNoteTemplate(templateGenerationRequest, accessToken);

            if (!matchResponse.getCustomerFound()) {
                logger.info("Customer not found but email provided - credit note template generated but not sent");
                return CreditNoteResult.customerNotFound();
            }

            return deliverEncryptedCreditNotes(originalReceiptId, matchResponse, templateResponse, accessToken);

        } catch (CheqiSDKException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Credit note processing failed: {}", e.getMessage(), e);
            throw new CheqiSDKException("Credit note processing failed: " + e.getMessage(), e);
        }
    }

    public CreditNoteResult processCompleteCreditNote(
            IdentificationDetails identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest) throws CheqiSDKException {
        return processCompleteCreditNote(identificationDetails, creditNoteTemplateRequest, null);
    }

    /**
     * Decrypts and parses an encrypted credit note request.
     *
     * @param encryptedCreditNote The encrypted credit note from webhook or API
     * @param privateKeyBase64 Base64-encoded private key for decryption
     * @return Parsed CreditNoteInitiationRequest ready for processing
     */
    public CreditNoteInitiationRequest decryptCreditNoteRequest(
            EncryptedCreditNoteInitiationRequest encryptedCreditNote, String privateKeyBase64) {

        logger.debug("Processing credit note request for receipt: {}",
                encryptedCreditNote.getCheqiReceiptId());

        try {
            DecryptedCreditNote decryptedCreditNote = decryptionService.decryptCreditNote(
                    encryptedCreditNote, privateKeyBase64);

            CreditNoteInitiationRequest creditNoteRequest = objectMapper.readValue(
                    decryptedCreditNote.getCreditNoteContentJson(),
                    CreditNoteInitiationRequest.class);

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
     * Generates a credit note template.
     *
     * @param request Credit note template generation request
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return Generated credit note template response
     */
    public CreditNoteTemplateResponse generateCreditNoteTemplate(
            CreditNoteTemplateGenerationRequest request,
            String accessToken) throws CheqiSDKException {

        requireNonNull(request, "Credit note template request");

        logger.debug("Generating credit note template for: {}", request.getCreditNoteTemplateRequest().getDocumentNumber());

        try {
            CreditNoteTemplateResponse template = (accessToken != null)
                    ? apiClient.generateCreditNoteTemplate(request, accessToken)
                    : apiClient.generateCreditNoteTemplate(request);
            logger.debug("Credit note template generated successfully");
            return template;
        } catch (Exception e) {
            logger.error("Failed to generate credit note template: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to generate credit note template: " + e.getMessage(), e);
        }
    }

    public CreditNoteTemplateResponse generateCreditNoteTemplate(
            CreditNoteTemplateGenerationRequest request) throws CheqiSDKException {
        return generateCreditNoteTemplate(request, null);
    }

    /**
     * Sends encrypted credit notes to the backend for delivery.
     *
     * @param parentCheqiReceiptId Original receipt ID this credit note references
     * @param encryptedCreditNotes Encrypted credit notes, one per recipient
     * @param templateHash SHA-256 hash of the template content
     * @param recipientResolutionResponse Customer match response with match ID
     * @param accessToken OAuth access token, or null to use API key from SDK config
     * @return Credit note created response from the backend
     */
    public CreditNoteCreatedResponse sendEncryptedCreditNotes(
            String parentCheqiReceiptId,
            Set<EncryptedCreditNote> encryptedCreditNotes,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse,
            String accessToken) throws CheqiSDKException {

        requireNonNull(encryptedCreditNotes, "Encrypted credit notes");
        if (encryptedCreditNotes.isEmpty()) {
            throw validationError("Encrypted credit notes cannot be empty");
        }
        requireNonEmpty(templateHash, "Template hash");
        requireNonNull(recipientResolutionResponse, "Customer match response");
        if (!recipientResolutionResponse.getCustomerFound()) {
            throw new CheqiSDKException("Cannot send credit notes: customer was not found during matching",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND, 400, null);
        }

        logger.debug("Sending {} encrypted credit notes to backend", encryptedCreditNotes.size());

        try {
            CreditNoteCreatedResponse response = (accessToken != null)
                    ? apiClient.sendEncryptedCreditNotes(recipientResolutionResponse.getMatchId(), parentCheqiReceiptId, encryptedCreditNotes, templateHash, accessToken)
                    : apiClient.sendEncryptedCreditNotes(recipientResolutionResponse.getMatchId(), parentCheqiReceiptId, encryptedCreditNotes, templateHash);
            logger.info("Successfully sent {} encrypted credit notes", encryptedCreditNotes.size());
            return response;
        } catch (CheqiApiException e) {
            logger.error("Failed to send encrypted credit notes: {}", e.getMessage());
            throw new CheqiSDKException("Failed to send encrypted credit notes: " + e.getMessage(), e);
        }
    }

    public CreditNoteCreatedResponse sendEncryptedCreditNotes(
            String parentCheqiReceiptId,
            Set<EncryptedCreditNote> encryptedCreditNotes,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse) throws CheqiSDKException {
        return sendEncryptedCreditNotes(parentCheqiReceiptId, encryptedCreditNotes, templateHash, recipientResolutionResponse, null);
    }

    /**
     * Creates an encrypted credit note for a single recipient.
     *
     * @param creditNoteEnvelope The credit note envelope in JSON format
     * @param recipient Recipient with public key information
     * @return Encrypted credit note
     */
    public EncryptedCreditNote createEncryptedCreditNote(
            String creditNoteEnvelope,
            MatchedRecipient recipient) throws CheqiSDKException {

        requireNonEmpty(creditNoteEnvelope, "Credit note envelope");
        requireNonNull(recipient, "Recipient");

        try {
            return encryptionService.encryptCreditNoteForRecipient(creditNoteEnvelope, recipient);
        } catch (Exception e) {
            logger.error("Failed to create encrypted credit note: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create encrypted credit note: " + e.getMessage(), e);
        }
    }

    // ===== PRIVATE HELPERS =====

    private RecipientResolutionResponse matchCustomer(
            IdentificationDetails identificationDetails, String accessToken) throws CheqiApiException {
        logger.debug("Matching customer with {}", accessToken != null ? "access token" : "API key");
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

    private CreditNoteEnvelope buildEnvelopeForRecipient(
            MatchedRecipient recipient,
            CreditNoteTemplateResponse templateResponse) {

        CreditNoteEnvelope envelope = new CreditNoteEnvelope();
        List<ReceiptFormat> acceptedFormats = recipient.getAcceptedFormats();

        if (acceptedFormats.contains(ReceiptFormat.CHEQI) && templateResponse.getCheqi() != null) {
            envelope.setCheqiCreditNote(templateResponse.getCheqi());
        }
        if (acceptedFormats.contains(ReceiptFormat.UBL_CREDIT_NOTE) && templateResponse.getUbl() != null) {
            envelope.setUblPurchaseReceipt(templateResponse.getUbl());
        }
        return envelope;
    }

    private EncryptedCreditNote buildEncryptedCreditNote(
            MatchedRecipient recipient, String envelopeJson) throws CheqiSDKException {
        try {
            logger.debug("Encrypting credit note envelope for recipient: {}", recipient.getId());
            EncryptedCreditNote encrypted = encryptionService.encryptCreditNoteForRecipient(envelopeJson, recipient);

            EncryptedCreditNote encryptedCreditNote = new EncryptedCreditNote();
            encryptedCreditNote.setRecipientId(recipient.getId());
            encryptedCreditNote.setReceiptFormats(recipient.getAcceptedFormats());
            encryptedCreditNote.setPublicKey(recipient.getPublicKey());
            encryptedCreditNote.setEncryptedCreditNote(encrypted.getEncryptedCreditNote());
            encryptedCreditNote.setEncryptedSymmetricKey(encrypted.getEncryptedSymmetricKey());

            return encryptedCreditNote;
        } catch (Exception e) {
            logger.error("Failed to encrypt credit note for recipient {}: {}", recipient.getId(), e.getMessage());
            throw new CheqiSDKException("Failed to encrypt credit note for recipient: " + recipient.getId(), e);
        }
    }

    private CreditNoteResult deliverEncryptedCreditNotes(
            String originalReceiptId,
            RecipientResolutionResponse matchResponse,
            CreditNoteTemplateResponse templateResponse,
            String accessToken) throws Exception {

        logger.debug("Creating encrypted credit notes for {} recipients", matchResponse.getRecipients().size());

        Set<EncryptedCreditNote> encryptedCreditNotes = new HashSet<>();
        for (MatchedRecipient recipient : matchResponse.getRecipients()) {
            CreditNoteEnvelope envelope = buildEnvelopeForRecipient(recipient, templateResponse);
            logger.debug("Encrypting envelope for recipient: {} with formats: {}", recipient.getId(), recipient.getAcceptedFormats());
            String envelopeJson = objectMapper.writeValueAsString(envelope);
            encryptedCreditNotes.add(buildEncryptedCreditNote(recipient, envelopeJson));
        }

        String templateContent = templateResponse.getCheqi() != null
                ? objectMapper.writeValueAsString(templateResponse.getCheqi())
                : templateResponse.getUbl();
        String templateHash = HashUtils.sha256Hex(templateContent);

        logger.debug("Sending {} encrypted credit notes to backend", encryptedCreditNotes.size());
        CreditNoteCreatedResponse response = sendEncryptedCreditNotes(
                originalReceiptId, encryptedCreditNotes, templateHash, matchResponse, accessToken);

        logger.info("Credit note processing completed: {} credit notes for {} recipients",
                encryptedCreditNotes.size(), matchResponse.getRecipients().size());
        return CreditNoteResult.deliveredToApp(response, templateContent);
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
