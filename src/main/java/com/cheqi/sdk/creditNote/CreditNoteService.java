package com.cheqi.sdk.creditNote;

import com.cheqi.commons.DTOs.EncryptedCreditNoteDto;
import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.DTOs.Recipient;
import com.cheqi.commons.DTOs.RecipientResolutionResponse;
import com.cheqi.commons.UBL.CreditNote;
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
import com.cheqi.sdk.receipt.ProcessReceiptResult;
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
 * - Decryption of encrypted credit note requests
 * - JSON parsing to CreditNoteRequest objects
 */
public class CreditNoteService {
    private static final Logger logger = LoggerFactory.getLogger(CreditNoteService.class);
    
    private final DecryptionService decryptionService;
    private final ObjectMapper objectMapper;
    private final String privateKeyBase64;
    private final MatchingService matchingService;
    private final CheqiApiClient apiClient;
    private final RFC8785Canonicalizer canonicalizer = new RFC8785Canonicalizer();
    private final EncryptionService encryptionService;

    public CreditNoteService(String privateKeyBase64, MatchingService matchingService, CheqiApiClient apiClient, EncryptionService encryptionService) {
        this.privateKeyBase64 = privateKeyBase64;
        this.matchingService = matchingService;
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = encryptionService;
        this.decryptionService = new DecryptionService();
        this.objectMapper = new ObjectMapper(); 
        logger.info("CreditNoteService initialized");
    }

    /**
     * Decrypts and parses an encrypted credit note request.
     *
     * @param encryptedCreditNote The encrypted credit note from webhook or API
     * @return Parsed CreditNoteRequest object ready for processing
     * @throws DecryptionException if decryption fails
     * @throws CreditNoteProcessingException if JSON parsing fails
     */
    public CreditNoteRequest decryptCreditNoteRequest(
            EncryptedCreditNoteRequest encryptedCreditNote) {
        
        logger.debug("Processing credit note request for receipt: {}", 
                encryptedCreditNote.getCheqiReceiptId());
        
        try {
            // Step 1: Decrypt the credit note
            DecryptedCreditNote decryptedCreditNote = decryptionService.decryptCreditNote(
                    encryptedCreditNote,
                    this.privateKeyBase64
            );
            
            // Step 2: Parse JSON to CreditNoteRequest
            CreditNoteRequest creditNoteRequest = objectMapper.readValue(
                    decryptedCreditNote.getCreditNoteContentJson(),
                    CreditNoteRequest.class
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
    public String generateCreditNoteTemplate(
            CreditNoteTemplateRequest request,
            String clientId,
            String clientSecret) throws CheqiSDKException {

        if (request == null) {
            throw new CheqiSDKException("Credit Note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!request.isValid()) {
            List<String> errors = request.getValidationErrors();
            throw new CheqiSDKException("Invalid Credit Note template request: " + String.join(", ", errors),
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (clientId == null || clientId.trim().isEmpty() || clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new CheqiSDKException("Client ID and secret are required for credit note template generation",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Generating credit note template with API credentials for credit note: {}", request.getDocumentNumber());

        try {
            String template = apiClient.generateCreditNoteTemplate(request, clientId, clientSecret);
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

    public ProcessReceiptResult processCompleteCreditNote(
            IdentificationDetails  identificationDetails,
            CreditNoteTemplateRequest creditNoteTemplateRequest,
            String clientId,
            String clientSecret
    ) throws CheqiSDKException {

        if(identificationDetails == null) {
            throw new CheqiSDKException("Identification details cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if(creditNoteTemplateRequest == null){
            throw new CheqiSDKException("Credit note template request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if(clientId == null || clientId.trim().isEmpty() || clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new CheqiSDKException("Client ID and secret are required for credit note processing",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        try {
            logger.debug("Matching customer using cheqiReceiptId");
            RecipientResolutionResponse matchResponse = matchingService.matchCustomer(identificationDetails, clientId, clientSecret);

            if (!matchResponse.isCustomerFound() && identificationDetails.getRecipientEmail().isEmpty()) {
                logger.info("Customer not found and no email provided - skipping template generation as no reciepient is known");
                return ProcessReceiptResult.customerNotFound();
            }

            // Step 3: Generate receipt template
            logger.debug("Generating receipt template with API credentials");
            String creditNoteTemplate = generateCreditNoteTemplate(creditNoteTemplateRequest, clientId, clientSecret);

            CreditNote creditNote = objectMapper.readValue(creditNoteTemplate, CreditNote.class);

            if(matchResponse.isCustomerFound()){
                String receiptTemplateJson = objectMapper.writeValueAsString(creditNote);
                String canonicalReceiptJson = canonicalizer.canonicalize(receiptTemplateJson);
                String templateHash = HashUtils.sha256Hex(canonicalReceiptJson);

                logger.debug("Creating encrypted receipts for {} recipients", matchResponse.getRecipients().size());
                Set<EncryptedCreditNoteDto> encryptedCreditNoteDtos = createEncryptedCreditNotes(
                        canonicalReceiptJson,
                        matchResponse.getRecipients()
                );

                logger.debug("Sending {} encrypted receipts to backend with API credentials", encryptedCreditNoteDtos.size());
                sendEncryptedCreditNotes(encryptedCreditNoteDtos, templateHash, matchResponse, clientId, clientSecret);

                logger.info("Receipt processing completed successfully: {} receipts created for {} recipients",
                        encryptedCreditNoteDtos.size(), matchResponse.getRecipients().size());
                return ProcessReceiptResult.success(encryptedCreditNoteDtos.size(), matchResponse.getRecipients().size());
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
     * Sends encrypted receipts using API credentials.
     */
    public void sendEncryptedCreditNotes(
            Set<EncryptedCreditNoteDto> encryptedCreditNotes,
            String templateHash,
            RecipientResolutionResponse recipientResolutionResponse,
            String clientId,
            String clientSecret) throws CheqiSDKException {

        if (encryptedCreditNotes == null || encryptedCreditNotes.isEmpty()) {
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

        if (clientId == null || clientId.trim().isEmpty() || clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new CheqiSDKException("Client ID and secret cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.debug("Sending {} encrypted receipts to backend with API credentials", encryptedCreditNotes.size());

        try {
            apiClient.sendEncryptedCreditNotes(
                    encryptedCreditNotes,
                    templateHash,
                    clientId,
                    clientSecret
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
    public Set<EncryptedCreditNoteDto> createEncryptedCreditNotes(
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
            Set<EncryptedCreditNoteDto> encryptedReceipts = encryptionService.encryptCreditNoteForRecipients(
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