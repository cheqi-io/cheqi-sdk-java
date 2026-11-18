package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.PreparedCreditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.RequestOptions;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Issues definitive credit-note values through the separate encrypted credit-note endpoint. */
public class CreditNoteService {
    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;

    public CreditNoteService(
            CheqiApiClient apiClient,
            EncryptionService encryptionService,
            MatchingService matchingService
    ) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(
                encryptionService,
                "encryptionService cannot be null"
        );
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
    }

    /**
     * Serializes and encrypts the supplied definitive credit-note payload without calculations.
     * A typed generated payload can replace {@code Object} once that schema is public.
     */
    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload,
            UUID storeId,
            RequestOptions options
    ) throws CheqiSDKException {
        requireNonNull(identificationDetails, "identificationDetails");
        requireText(parentCheqiReceiptId, "parentCheqiReceiptId");
        requireNonNull(creditNotePayload, "creditNotePayload");

        try {
            RecipientResolutionResponse resolution = matchingService.matchCustomer(identificationDetails, options);
            validateDeviceRoute(resolution);

            String plaintextJson = creditNotePayload instanceof String
                    ? (String) creditNotePayload
                    : objectMapper.writeValueAsString(creditNotePayload);
            List<EncryptedReceiptPayload> deliveries = new ArrayList<>();
            for (MatchedRecipient recipient : resolution.getRecipients()) {
                deliveries.add(
                        encryptionService.encryptCreditNoteForRecipient(plaintextJson, recipient)
                );
            }

            EncryptedCreditNoteEnvelope envelope = new EncryptedCreditNoteEnvelope()
                    .matchId(resolution.getMatchId())
                    .storeId(storeId)
                    .parentCheqiReceiptId(parentCheqiReceiptId)
                    .deviceDeliveries(deliveries);
            ReceiptSubmissionResponse response = apiClient.submitEncryptedCreditNote(envelope, options);
            return CreditNoteResult.accepted(parentCheqiReceiptId, response);
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            throw new CheqiSDKException(
                    "Credit-note submission failed: " + exception.getMessage(),
                    exception
            );
        } catch (Exception exception) {
            throw new CheqiSDKException(
                    "Credit-note processing failed: " + exception.getMessage(),
                    exception
            );
        }
    }

    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload,
            RequestOptions options
    ) throws CheqiSDKException {
        return issueCreditNote(identificationDetails, parentCheqiReceiptId, creditNotePayload, null, options);
    }

    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload,
            UUID storeId
    ) throws CheqiSDKException {
        return issueCreditNote(identificationDetails, parentCheqiReceiptId, creditNotePayload, storeId,
                RequestOptions.defaults());
    }

    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload
    ) throws CheqiSDKException {
        return issueCreditNote(identificationDetails, parentCheqiReceiptId, creditNotePayload, null,
                RequestOptions.defaults());
    }

    /** Resolve the payload's originatorDocumentReference, then encrypt for the original route.
     * Persist the returned object and replay submitPreparedCreditNote after uncertain outcomes.
     */
    public PreparedCreditNote prepareCreditNote(Object payload, UUID storeId, RequestOptions options) throws CheqiSDKException {
        requireNonNull(payload, "creditNotePayload");
        try {
            var node = payload instanceof String ? objectMapper.readTree((String) payload) : objectMapper.valueToTree(payload);
            var body = node.has("creditNoteTemplateRequest") ? node.get("creditNoteTemplateRequest") : node;
            if (body == null || !body.isObject() || !body.path("originatorDocumentReference").isTextual())
                throw validationError("originatorDocumentReference must contain the original documentNumber");
            String hash = com.cheqi.sdk.receipt.DocumentReferences.hash(body.path("originatorDocumentReference").asText());
            var original = apiClient.resolveCreditNoteReference(new com.cheqi.sdk.models.generated.CreditNoteDocumentReference()
                    .documentReferenceHash(hash), options);
            if (original == null) throw validationError("Missing document-reference resolution");
            String parent = original.getParentCheqiReceiptId();
            requireText(parent, "resolved parentCheqiReceiptId");
            if ("DIGITAL".equals(original.getDeliveryRoute())) {
                if (!node.path("taxesApplied").isBoolean())
                    throw validationError("taxesApplied must be supplied as a boolean for device generation");
                var resolution = matchingService.matchCustomer(new IdentificationDetails().cheqiReceiptId(parent), options);
                validateDeviceRoute(resolution);
                if (resolution.getDeliveryRouteType() != RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL)
                    throw validationError("Original owner-device route is unavailable");
                var generation = node.has("creditNoteTemplateRequest") ? node : objectMapper.createObjectNode()
                        .put("taxesApplied", node.path("taxesApplied").asBoolean()).set("creditNoteTemplateRequest", body);
                List<EncryptedReceiptPayload> deliveries = new ArrayList<>();
                for (var recipient : resolution.getRecipients()) deliveries.add(encryptionService.encryptCreditNoteForRecipient(
                        objectMapper.writeValueAsString(generation), recipient));
                return new PreparedCreditNote().parentCheqiReceiptId(parent).deviceSubmission(new EncryptedCreditNoteEnvelope().matchId(resolution.getMatchId())
                        .parentCheqiReceiptId(parent).storeId(storeId == null ? original.getStoreId() : storeId).deviceDeliveries(deliveries));
            }
            if (!"DOWNLOAD".equals(original.getDeliveryRoute())) throw validationError("Unsupported original document route");
            if (storeId != null) throw validationError("Download delivery does not support store-scoped issuance");
            requireText(original.getCreditNotePublicKey(), "creditNotePublicKey");
            var downloads = new com.cheqi.sdk.download.DownloadService();
            // Only the randomly generated ID and key are used; no URL is exposed or requested.
            var child = downloads.generateDownloadLink("https://receipt.cheqi.io");
            var envelope = new com.cheqi.sdk.models.generated.ReceiptEnvelope().envelopeVersion(2)
                    .cheqiReceiptId(child.getDownloadId()).receiptUuid(UUID.randomUUID()).receiptGeneratorVersion("credit-note-payload-v1")
                    .putDocumentsItem("CREDIT_NOTE_PAYLOAD", new com.cheqi.sdk.models.generated.ReceiptEnvelopeDocument()
                            .mediaType(com.cheqi.sdk.models.generated.ReceiptEnvelopeDocument.MediaTypeEnum.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)));
            String link = objectMapper.writeValueAsString(java.util.Map.of("version", 2, "parentCheqiReceiptId", parent,
                    "downloadId", child.getDownloadId(), "contentKey", child.getContentKey()));
            var encryptedLink = encryptionService.encryptForDevice(link, new MatchedRecipient().id(parent).publicKey(original.getCreditNotePublicKey()));
            var request = new com.cheqi.sdk.models.generated.ClientReceiptDownloadRequest().downloadId(child.getDownloadId())
                    .ciphertext(downloads.encryptDownloadEnvelope(envelope, child.getContentKey()))
                    .parentCheqiReceiptId(parent).encryptedParentLink(encryptedLink.getEncryptedContent())
                    .encryptedParentLinkKey(encryptedLink.getEncryptedAesKey());
            return new PreparedCreditNote().parentCheqiReceiptId(parent).downloadSubmission(request);
        } catch (CheqiSDKException e) { throw e; }
        catch (CheqiApiException e) {
            throw new CheqiSDKException("Credit-note reference preparation failed: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
        catch (IllegalArgumentException e) { throw validationError("originatorDocumentReference must contain a nonblank original documentNumber"); }
        catch (Exception e) { throw new CheqiSDKException("Credit-note preparation failed", e); }
    }

    public PreparedCreditNote prepareCreditNote(Object payload) throws CheqiSDKException {
        return prepareCreditNote(payload, null, RequestOptions.defaults());
    }

    /** Submits the exact persisted ciphertext; never re-resolves or regenerates on retry. */
    public CreditNoteResult submitPreparedCreditNote(PreparedCreditNote prepared, RequestOptions options) throws CheqiSDKException {
        requireNonNull(prepared, "preparedCreditNote");
        requireText(prepared.getParentCheqiReceiptId(), "parentCheqiReceiptId");
        if ((prepared.getDeviceSubmission() == null) == (prepared.getDownloadSubmission() == null))
            throw validationError("Exactly one prepared submission is required");
        String parent = prepared.getDeviceSubmission() != null ? prepared.getDeviceSubmission().getParentCheqiReceiptId()
                : prepared.getDownloadSubmission().getParentCheqiReceiptId();
        if (!prepared.getParentCheqiReceiptId().equals(parent)) throw validationError("Credit-note parent mismatch");
        try {
            if (prepared.getDeviceSubmission() != null) {
                var response = apiClient.submitEncryptedCreditNote(prepared.getDeviceSubmission(), options);
                if (response == null || !prepared.getDeviceSubmission().getMatchId().equals(response.getMatchId()))
                    throw validationError("Credit-note acceptance is not confirmed");
                requireText(response.getCheqiReceiptId(), "accepted cheqiReceiptId");
                return CreditNoteResult.accepted(prepared.getParentCheqiReceiptId(), response);
            }
            var response = apiClient.uploadEncryptedDownloadReceipt(prepared.getDownloadSubmission(), options);
            if (response == null) throw validationError("Credit-note acceptance is not confirmed");
            requireText(response.getCheqiReceiptId(), "accepted cheqiReceiptId");
            return CreditNoteResult.accepted(prepared.getParentCheqiReceiptId(), new ReceiptSubmissionResponse()
                    .cheqiReceiptId(response.getCheqiReceiptId()).createdAt(response.getCreatedAt())
                    .status(ReceiptSubmissionResponse.StatusEnum.COMPLETED));
        } catch (CheqiSDKException e) { throw e; }
        catch (CheqiApiException e) {
            throw new CheqiSDKException("Credit-note submission failed; retain the prepared submission: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
        catch (Exception e) { throw new CheqiSDKException("Credit-note submission failed; retain the prepared submission", e); }
    }

    public CreditNoteResult submitPreparedCreditNote(PreparedCreditNote prepared) throws CheqiSDKException {
        return submitPreparedCreditNote(prepared, RequestOptions.defaults());
    }

    private static void validateDeviceRoute(RecipientResolutionResponse resolution)
            throws CheqiSDKException {
        if (resolution == null || !Boolean.TRUE.equals(resolution.getRouteFound())) {
            throw new CheqiSDKException(
                    "No Cheqi owner-device route was found",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND,
                    404,
                    null
            );
        }
        requireText(resolution.getMatchId(), "matchId");
        if (resolution.getRecipients() == null || resolution.getRecipients().isEmpty()) {
            throw validationError("Recipient resolution did not include owner devices");
        }
    }

    private static void requireNonNull(Object value, String name) throws CheqiSDKException {
        if (value == null) {
            throw validationError(name + " cannot be null");
        }
    }

    private static void requireText(String value, String name) throws CheqiSDKException {
        if (value == null || value.trim().isEmpty()) {
            throw validationError(name + " cannot be null or empty");
        }
    }

    private static CheqiSDKException validationError(String message) {
        return new CheqiSDKException(
                message,
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR,
                400,
                null
        );
    }
}
