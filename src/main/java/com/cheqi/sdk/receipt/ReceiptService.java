package com.cheqi.sdk.receipt;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.download.DownloadLink;
import com.cheqi.sdk.download.DownloadService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.ClientReceiptDownloadRequest;
import com.cheqi.sdk.models.generated.ClientReceiptDownloadResponse;
import com.cheqi.sdk.models.generated.EncryptedReceiptEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchStatusResponse;
import com.cheqi.sdk.http.exceptions.SubmissionInProgressException;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.ReceiptPayload;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.ReceiptEnvelopeDocument;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import com.cheqi.sdk.verification.VerificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Issues definitive receipt payloads through Cheqi's zero-knowledge device-processing flow.
 *
 * <p>The SDK serializes the supplied {@link ReceiptPayload} without calculating or enriching
 * receipt values, encrypts the same JSON independently for every matched owner device, and submits
 * only ciphertext to Cheqi.</p>
 */
public class ReceiptService {
    private static final String PAYLOAD_DOCUMENT_VERSION = "receipt-payload-v1";

    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;
    private final DownloadService downloadService;
    private final VerificationService verificationService;
    private final String downloadBaseUrl;

    public ReceiptService(
            CheqiApiClient apiClient,
            EncryptionService encryptionService,
            MatchingService matchingService
    ) {
        this(
                apiClient,
                encryptionService,
                matchingService,
                new DownloadService(),
                DownloadService.PRODUCTION_BASE_URL
        );
    }

    public ReceiptService(
            CheqiApiClient apiClient,
            EncryptionService encryptionService,
            MatchingService matchingService,
            DownloadService downloadService,
            String downloadBaseUrl
    ) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(
                encryptionService,
                "encryptionService cannot be null"
        );
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
        this.downloadService = Objects.requireNonNull(downloadService, "downloadService cannot be null");
        this.verificationService = new VerificationService();
        this.downloadBaseUrl = downloadBaseUrl;
    }

    public ReceiptResult issueReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload,
            UUID storeId,
            String accessToken
    ) throws CheqiSDKException {
        requireNonNull(identificationDetails, "identificationDetails");
        validatePayload(receiptPayload);
        if (accessToken != null && accessToken.trim().isEmpty()) {
            throw validationError("accessToken cannot be empty");
        }
        try {
            RecipientResolutionResponse resolution;
            try {
                resolution = accessToken == null ? matchingService.matchCustomer(identificationDetails)
                    : matchingService.matchCustomer(identificationDetails, accessToken);
            } catch (CheqiApiException exception) {
                if (!isRecoverable(exception)) throw exception;
                return ReceiptResult.downloadPrepared(prepareDownload(receiptPayload, null));
            }
            validateResolvedRoute(resolution);

            RecipientResolutionResponse.DeliveryRouteTypeEnum route = resolveRoute(resolution);
            if (route == RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK) {
                if (identificationDetails.getPaymentType() == null) {
                    return ReceiptResult.downloadEnvelopeRequired(resolution);
                }
                return deliverClientEncryptedDownload(
                        resolution.getMatchId(), identificationDetails, receiptPayload, accessToken);
            }
            if (route == RecipientResolutionResponse.DeliveryRouteTypeEnum.EMAIL_FALLBACK) {
                return ReceiptResult.emailReceiptRequired(resolution);
            }
            validateDeviceRecipients(resolution);
            try {
                return ReceiptResult.accepted(submitPayload(resolution.getMatchId(),
                        resolution.getRecipients(), receiptPayload, storeId, accessToken));
            } catch (CheqiApiException exception) {
                if (exception.getHttpStatusCode() == 410) return ReceiptResult.digitalExpired(resolution.getMatchId());
                if (!isAmbiguousSubmission(exception)) throw exception;
                return ReceiptResult.digitalPending(resolution.getMatchId(),
                        prepareDownload(receiptPayload, null), retryAfter(exception));
            }
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            throw sdkApiError("Receipt issuance failed", exception);
        } catch (Exception exception) {
            throw new CheqiSDKException("Receipt processing failed", exception);
        }
    }

    /** Retrieves the original device snapshot or submission without matching again. */
    public MatchStatusResponse getMatch(String matchId, String accessToken) throws CheqiSDKException {
        validateRetryArguments(matchId, accessToken);
        try {
            return accessToken == null ? apiClient.getMatch(matchId) : apiClient.getMatch(matchId, accessToken);
        } catch (CheqiApiException exception) {
            throw sdkApiError("Match retrieval failed", exception);
        }
    }

    public MatchStatusResponse getMatch(String matchId) throws CheqiSDKException {
        return getMatch(matchId, null);
    }

    /**
     * Makes one recovery attempt using only matchId and the definitive receipt payload.
     * Keep the original prepared download and URL: resuming digital delivery never replaces them.
     */
    public ReceiptResult resumeReceipt(String matchId, ReceiptPayload receiptPayload,
                                       UUID storeId, String accessToken) throws CheqiSDKException {
        validateRetryArguments(matchId, accessToken);
        validatePayload(receiptPayload);
        try {
            MatchStatusResponse match = accessToken == null ? apiClient.getMatch(matchId)
                    : apiClient.getMatch(matchId, accessToken);
            if (match == null || match.getState() == null || !matchId.equals(match.getMatchId())) {
                throw validationError("Invalid match status response");
            }
            switch (match.getState()) {
                case SUBMITTED:
                    ReceiptSubmissionResponse submitted = match.getSubmission();
                    if (submitted == null || submitted.getCheqiReceiptId() == null
                            || submitted.getCheqiReceiptId().trim().isEmpty()
                            || !matchId.equals(submitted.getMatchId())) {
                        throw validationError("Match status did not include the original submission");
                    }
                    return ReceiptResult.accepted(submitted);
                case IN_PROGRESS:
                    return ReceiptResult.digitalPending(matchId, null, match.getRetryAfterSeconds());
                case EXPIRED:
                    return ReceiptResult.digitalExpired(matchId);
                case MATCHED:
                    if (match.getRoute() != MatchStatusResponse.RouteEnum.DIGITAL
                            || match.getRecipients() == null || match.getRecipients().isEmpty()) {
                        throw validationError("Match does not contain a digital owner-device route");
                    }
                    return ReceiptResult.accepted(submitPayload(matchId, match.getRecipients(),
                            receiptPayload, storeId, accessToken));
                default:
                    throw validationError("Unknown match state");
            }
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            if (exception.getHttpStatusCode() == 410) return ReceiptResult.digitalExpired(matchId);
            if (isAmbiguousSubmission(exception)) {
                return ReceiptResult.digitalPending(matchId, null, retryAfter(exception));
            }
            throw sdkApiError("Receipt recovery failed", exception);
        } catch (Exception exception) {
            throw new CheqiSDKException("Receipt recovery failed", exception);
        }
    }

    public ReceiptResult resumeReceipt(String matchId, ReceiptPayload payload, UUID storeId)
            throws CheqiSDKException { return resumeReceipt(matchId, payload, storeId, null); }

    public ReceiptResult resumeReceipt(String matchId, ReceiptPayload payload, String accessToken)
            throws CheqiSDKException { return resumeReceipt(matchId, payload, null, accessToken); }

    public ReceiptResult resumeReceipt(String matchId, ReceiptPayload payload)
            throws CheqiSDKException { return resumeReceipt(matchId, payload, null, null); }

    /** Upload the persisted encrypted fallback from a background worker; no matching is performed. */
    public ClientReceiptDownloadResponse uploadPreparedDownload(PreparedReceiptDownload download, String accessToken)
            throws CheqiSDKException {
        requireNonNull(download, "download");
        validateAccessToken(accessToken);
        try {
            return accessToken == null ? apiClient.uploadEncryptedDownloadReceipt(download.getUploadRequest())
                    : apiClient.uploadEncryptedDownloadReceipt(download.getUploadRequest(), accessToken);
        } catch (CheqiApiException exception) {
            throw sdkApiError("Download upload failed", exception);
        }
    }

    public ClientReceiptDownloadResponse uploadPreparedDownload(PreparedReceiptDownload download)
            throws CheqiSDKException { return uploadPreparedDownload(download, null); }

    private ReceiptSubmissionResponse submitPayload(String matchId, List<MatchedRecipient> recipients,
                                                      ReceiptPayload payload, UUID storeId, String accessToken)
            throws Exception {
        EncryptedReceiptEnvelope envelope = generateEnvelope(matchId, recipients, payload, storeId);
        ReceiptSubmissionResponse response = accessToken == null ? apiClient.submitEncryptedReceipt(envelope)
                : apiClient.submitEncryptedReceipt(envelope, accessToken);
        if (response == null || response.getCheqiReceiptId() == null
                || response.getCheqiReceiptId().trim().isEmpty() || !matchId.equals(response.getMatchId())) {
            throw new CheqiApiException("Invalid receipt submission response", 0,
                    CheqiApiException.ErrorCodes.INVALID_RESPONSE, null);
        }
        return response;
    }

    /**
     * Encrypts locally for the original resolved devices without performing any HTTP requests.
     * The returned generated envelope can be submitted using {@link #submitEncryptedReceipt}.
     */
    public EncryptedReceiptEnvelope generateEncryptedReceiptEnvelope(
            RecipientResolutionResponse match, ReceiptPayload receiptPayload, UUID storeId)
            throws CheqiSDKException {
        validateResolvedRoute(match);
        if (resolveRoute(match) != RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL) {
            throw validationError("Match does not contain a digital owner-device route");
        }
        return generateEnvelope(match.getMatchId(), match.getRecipients(), receiptPayload, storeId);
    }

    /** Encrypts locally using a retrieved, unsubmitted digital match. */
    public EncryptedReceiptEnvelope generateEncryptedReceiptEnvelope(
            MatchStatusResponse match, ReceiptPayload receiptPayload, UUID storeId)
            throws CheqiSDKException {
        requireNonNull(match, "match");
        if (match.getState() != com.cheqi.sdk.models.generated.MatchState.MATCHED
                || match.getRoute() != MatchStatusResponse.RouteEnum.DIGITAL) {
            throw validationError("Match must be MATCHED with a digital owner-device route");
        }
        return generateEnvelope(match.getMatchId(), match.getRecipients(), receiptPayload, storeId);
    }

    private EncryptedReceiptEnvelope generateEnvelope(String matchId, List<MatchedRecipient> recipients,
                                                       ReceiptPayload payload, UUID storeId)
            throws CheqiSDKException {
        validateRetryArguments(matchId, null);
        validatePayload(payload);
        if (recipients == null || recipients.isEmpty()
                || recipients.stream().anyMatch(Objects::isNull)) {
            throw validationError("Match must contain owner devices");
        }
        try {
            return new EncryptedReceiptEnvelope().matchId(matchId).storeId(storeId)
                    .deviceDeliveries(encryptForDevices(objectMapper.writeValueAsString(payload), recipients));
        } catch (Exception exception) {
            throw new CheqiSDKException("Receipt encryption failed", exception);
        }
    }

    private static boolean isAmbiguousSubmission(CheqiApiException exception) {
        return isRecoverable(exception)
                || (CheqiApiException.ErrorCodes.INVALID_RESPONSE.equals(exception.getErrorCode())
                    && (exception.getHttpStatusCode() == 0
                        || (exception.getHttpStatusCode() >= 200 && exception.getHttpStatusCode() < 300)));
    }

    private static Integer retryAfter(CheqiApiException exception) {
        return exception instanceof SubmissionInProgressException
                ? ((SubmissionInProgressException) exception).getRetryAfterSeconds() : null;
    }

    private static boolean isRecoverable(CheqiApiException exception) {
        if (exception.isRetryable()) return true;
        if (exception.getHttpStatusCode() != 0) return false;
        return CheqiApiException.ErrorCodes.NETWORK_ERROR.equals(exception.getErrorCode())
                || CheqiApiException.ErrorCodes.TIMEOUT_ERROR.equals(exception.getErrorCode())
                || (exception.getCause() instanceof java.io.IOException
                    && !(exception.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException));
    }

    private static CheqiSDKException sdkApiError(String message, CheqiApiException exception) {
        return new CheqiSDKException(message, exception, exception.getErrorCode(),
                exception.getHttpStatusCode(), exception.getCorrelationId());
    }

    private static void validateAccessToken(String accessToken) throws CheqiSDKException {
        if (accessToken != null && accessToken.trim().isEmpty()) throw validationError("accessToken cannot be empty");
    }

    private static void validateRetryArguments(String matchId, String accessToken) throws CheqiSDKException {
        if (matchId == null || matchId.trim().isEmpty()) throw validationError("matchId is required");
        validateAccessToken(accessToken);
    }

    /**
     * Creates a client-encrypted download receipt without attempting recipient matching.
     * Use this for an explicit "Customer without Cheqi" flow where only local payment context
     * is available.
     */
    public ReceiptResult issueDownloadReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload,
            String accessToken
    ) throws CheqiSDKException {
        requireNonNull(identificationDetails, "identificationDetails");
        validatePayload(receiptPayload);
        if (identificationDetails.getPaymentType() == null) {
            throw validationError("identificationDetails.paymentType is required");
        }
        if (accessToken != null && accessToken.trim().isEmpty()) {
            throw validationError("accessToken cannot be empty");
        }
        try {
            return deliverClientEncryptedDownload(
                    null,
                    identificationDetails,
                    receiptPayload,
                    accessToken
            );
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            throw new CheqiSDKException(
                    "Encrypted download receipt submission failed: " + exception.getMessage(),
                    exception
            );
        } catch (Exception exception) {
            throw new CheqiSDKException(
                    "Download receipt processing failed: " + exception.getMessage(),
                    exception
            );
        }
    }

    public ReceiptResult issueDownloadReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload
    ) throws CheqiSDKException {
        return issueDownloadReceipt(identificationDetails, receiptPayload, null);
    }

    public ReceiptResult issueReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload,
            String accessToken
    ) throws CheqiSDKException {
        return issueReceipt(identificationDetails, receiptPayload, null, accessToken);
    }

    public ReceiptResult issueReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload,
            UUID storeId
    ) throws CheqiSDKException {
        return issueReceipt(identificationDetails, receiptPayload, storeId, null);
    }

    public ReceiptResult issueReceipt(
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload
    ) throws CheqiSDKException {
        return issueReceipt(identificationDetails, receiptPayload, null, null);
    }

    public ReceiptSubmissionResponse submitEncryptedReceipt(
            EncryptedReceiptEnvelope envelope,
            String accessToken
    ) throws CheqiSDKException {
        requireNonNull(envelope, "envelope");
        try {
            return accessToken == null
                    ? apiClient.submitEncryptedReceipt(envelope)
                    : apiClient.submitEncryptedReceipt(envelope, accessToken);
        } catch (CheqiApiException exception) {
            throw sdkApiError("Encrypted receipt submission failed: " + exception.getMessage(), exception);
        }
    }

    public ReceiptSubmissionResponse submitEncryptedReceipt(EncryptedReceiptEnvelope envelope)
            throws CheqiSDKException {
        return submitEncryptedReceipt(envelope, null);
    }

    /**
     * Completes a download fallback after the caller has generated the final receipt envelope
     * locally. The SDK creates the URL credentials, encrypts the envelope, and uploads only
     * ciphertext. The content key remains in the returned URL fragment.
     *
     * @param fallbackResult result returned by {@link #issueReceipt} for DOWNLOAD_FALLBACK
     * @param receiptEnvelope locally generated final receipt formats
     * @param templateHash optional caller-supplied hash; the SDK does not calculate it
     * @param accessToken optional OAuth access token, or {@code null} for configured API-key auth
     */
    public ReceiptResult completeDownloadFallback(
            ReceiptResult fallbackResult,
            ReceiptEnvelope receiptEnvelope,
            String templateHash,
            String accessToken
    ) throws CheqiSDKException {
        requireNonNull(fallbackResult, "fallbackResult");
        requireNonNull(receiptEnvelope, "receiptEnvelope");
        if (!fallbackResult.isDownloadEnvelopeRequired()
                || fallbackResult.getDeliveryRouteType()
                != RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK) {
            throw validationError("fallbackResult does not require a download receipt envelope");
        }
        if (accessToken != null && accessToken.trim().isEmpty()) {
            throw validationError("accessToken cannot be empty");
        }
        if (downloadBaseUrl == null || downloadBaseUrl.trim().isEmpty()) {
            throw validationError("A receipt download base URL is required to complete download fallback");
        }

        try {
            DownloadLink link = downloadService.generateDownloadLink(downloadBaseUrl);
            String ciphertext = downloadService.encryptDownloadEnvelope(
                    receiptEnvelope,
                    link.getContentKey()
            );
            ClientReceiptDownloadRequest request = new ClientReceiptDownloadRequest()
                    .downloadId(link.getDownloadId())
                    .ciphertext(ciphertext)
                    .templateHash(templateHash);
            ClientReceiptDownloadResponse response = accessToken == null
                    ? apiClient.uploadEncryptedDownloadReceipt(request)
                    : apiClient.uploadEncryptedDownloadReceipt(request, accessToken);
            return ReceiptResult.downloadAccepted(
                    fallbackResult.getMatchId(),
                    response,
                    link.getUrl()
            );
        } catch (CheqiApiException exception) {
            throw new CheqiSDKException(
                    "Encrypted download receipt submission failed: " + exception.getMessage(),
                    exception
            );
        } catch (Exception exception) {
            throw new CheqiSDKException(
                    "Download fallback processing failed: " + exception.getMessage(),
                    exception
            );
        }
    }

    public ReceiptResult completeDownloadFallback(
            ReceiptResult fallbackResult,
            ReceiptEnvelope receiptEnvelope,
            String templateHash
    ) throws CheqiSDKException {
        return completeDownloadFallback(fallbackResult, receiptEnvelope, templateHash, null);
    }

    public ReceiptResult completeDownloadFallback(
            ReceiptResult fallbackResult,
            ReceiptEnvelope receiptEnvelope
    ) throws CheqiSDKException {
        return completeDownloadFallback(fallbackResult, receiptEnvelope, null, null);
    }

    private List<EncryptedReceiptPayload> encryptForDevices(
            String plaintextJson,
            List<MatchedRecipient> recipients
    ) {
        List<EncryptedReceiptPayload> deliveries = new ArrayList<>(recipients.size());
        for (MatchedRecipient recipient : recipients) {
            deliveries.add(encryptionService.encryptReceiptForRecipient(plaintextJson, recipient));
        }
        return deliveries;
    }

    private ReceiptResult deliverClientEncryptedDownload(
            String matchId,
            IdentificationDetails identificationDetails,
            ReceiptPayload receiptPayload,
            String accessToken
    ) throws Exception {
        PreparedReceiptDownload prepared = prepareDownload(receiptPayload, identificationDetails);
        try {
            ClientReceiptDownloadResponse response = accessToken == null
                    ? apiClient.uploadEncryptedDownloadReceipt(prepared.getUploadRequest())
                    : apiClient.uploadEncryptedDownloadReceipt(prepared.getUploadRequest(), accessToken);
            return ReceiptResult.downloadAccepted(matchId, response, prepared.getDownloadUrl());
        } catch (CheqiApiException exception) {
            if (!isRecoverable(exception)) throw exception;
            return ReceiptResult.downloadPrepared(prepared);
        }
    }

    private PreparedReceiptDownload prepareDownload(ReceiptPayload receiptPayload,
                                                     IdentificationDetails identificationDetails)
            throws Exception {
        if (downloadBaseUrl == null || downloadBaseUrl.trim().isEmpty()) {
            throw validationError("A receipt download base URL is required to prepare download fallback");
        }
        DownloadLink link = downloadService.generateDownloadLink(downloadBaseUrl);
        // Recovery fallback contains the definitive payload only; never retain matching identifiers.
        String cheqiDocument = identificationDetails == null ? objectMapper.writeValueAsString(receiptPayload)
                : buildDownloadCheqiDocument(receiptPayload, identificationDetails);
        ReceiptEnvelope envelope = new ReceiptEnvelope()
                .cheqiReceiptId(link.getDownloadId()).envelopeVersion(1)
                .receiptGeneratorVersion(PAYLOAD_DOCUMENT_VERSION).receiptUuid(UUID.randomUUID())
                .putDocumentsItem("CHEQI", new ReceiptEnvelopeDocument()
                        .mediaType(ReceiptEnvelopeDocument.MediaTypeEnum.APPLICATION_JSON).content(cheqiDocument));
        return new PreparedReceiptDownload(link.getUrl(), link.getDownloadId(),
                downloadService.encryptDownloadEnvelope(envelope, link.getContentKey()),
                verificationService.calculateCheqiReceiptHash(cheqiDocument));
    }

    /**
     * Builds the CHEQI JSON used only by the browser download route. The generated receipt payload
     * remains the base contract; the same generated identification details supplied for matching
     * are included unchanged so cash, card, direct-debit, and PAR context survive fallback.
     */
    String buildDownloadCheqiDocument(
            ReceiptPayload receiptPayload,
            IdentificationDetails identificationDetails
    ) throws Exception {
        ObjectNode document = objectMapper.valueToTree(receiptPayload);
        JsonNode identification = objectMapper.valueToTree(identificationDetails);
        document.set("identificationDetails", identification);
        return objectMapper.writeValueAsString(document);
    }

    private static void validateResolvedRoute(RecipientResolutionResponse resolution)
            throws CheqiSDKException {
        if (resolution == null || !Boolean.TRUE.equals(resolution.getRouteFound())) {
            throw new CheqiSDKException(
                    "No Cheqi owner-device route was found",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND,
                    404,
                    null
            );
        }
        if (resolution.getMatchId() == null || resolution.getMatchId().trim().isEmpty()) {
            throw validationError("Recipient resolution did not include matchId");
        }
    }

    private static RecipientResolutionResponse.DeliveryRouteTypeEnum resolveRoute(
            RecipientResolutionResponse resolution
    ) throws CheqiSDKException {
        if (resolution.getDeliveryRouteType() != null) {
            return resolution.getDeliveryRouteType();
        }
        if (resolution.getRecipients() != null && !resolution.getRecipients().isEmpty()) {
            return RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL;
        }
        throw validationError("Recipient resolution did not include deliveryRouteType");
    }

    private static void validateDeviceRecipients(RecipientResolutionResponse resolution)
            throws CheqiSDKException {
        if (resolution.getRecipients() == null || resolution.getRecipients().isEmpty()) {
            throw validationError("Recipient resolution did not include owner devices");
        }
    }

    private static void validatePayload(ReceiptPayload payload) throws CheqiSDKException {
        requireNonNull(payload, "receiptPayload");
        if (payload.getDocumentNumber() == null || payload.getDocumentNumber().trim().isEmpty()
                || payload.getCurrency() == null || payload.getCurrency().trim().isEmpty()
                || payload.getIssueDate() == null || payload.getProducts() == null
                || payload.getProducts().isEmpty() || payload.getProducts().stream().anyMatch(Objects::isNull)
                || payload.getReceiptSubtotal() == null || payload.getTotalBeforeTax() == null
                || payload.getTotalTaxAmount() == null || payload.getTotalAmount() == null
                || payload.getTaxesApplied() == null) {
            throw validationError("receiptPayload must contain the required receipt fields and products");
        }
    }

    private static void requireNonNull(Object value, String name) throws CheqiSDKException {
        if (value == null) {
            throw validationError(name + " cannot be null");
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
