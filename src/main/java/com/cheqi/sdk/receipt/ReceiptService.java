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
        requireNonNull(receiptPayload, "receiptPayload");
        if (accessToken != null && accessToken.trim().isEmpty()) {
            throw validationError("accessToken cannot be empty");
        }
        try {
            RecipientResolutionResponse resolution = accessToken == null
                    ? matchingService.matchCustomer(identificationDetails)
                    : matchingService.matchCustomer(identificationDetails, accessToken);
            validateResolvedRoute(resolution);

            RecipientResolutionResponse.DeliveryRouteTypeEnum route = resolveRoute(resolution);
            if (route == RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK) {
                if (identificationDetails.getPaymentType() == null) {
                    return ReceiptResult.downloadEnvelopeRequired(resolution);
                }
                return deliverClientEncryptedDownload(
                        resolution.getMatchId(),
                        identificationDetails,
                        receiptPayload,
                        accessToken
                );
            }
            if (route == RecipientResolutionResponse.DeliveryRouteTypeEnum.EMAIL_FALLBACK) {
                return ReceiptResult.emailReceiptRequired(resolution);
            }
            validateDeviceRecipients(resolution);

            String plaintextJson = objectMapper.writeValueAsString(receiptPayload);
            List<EncryptedReceiptPayload> deliveries = encryptForDevices(
                    plaintextJson,
                    resolution.getRecipients()
            );
            EncryptedReceiptEnvelope envelope = new EncryptedReceiptEnvelope()
                    .matchId(resolution.getMatchId())
                    .storeId(storeId)
                    .deviceDeliveries(deliveries);

            ReceiptSubmissionResponse response = accessToken == null
                    ? apiClient.submitEncryptedReceipt(envelope)
                    : apiClient.submitEncryptedReceipt(envelope, accessToken);
            return ReceiptResult.accepted(response, route);
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            throw new CheqiSDKException(
                    "Receipt submission failed: " + exception.getMessage(),
                    exception
            );
        } catch (Exception exception) {
            throw new CheqiSDKException(
                    "Receipt processing failed: " + exception.getMessage(),
                    exception
            );
        }
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
        requireNonNull(receiptPayload, "receiptPayload");
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
            throw new CheqiSDKException(
                    "Encrypted receipt submission failed: " + exception.getMessage(),
                    exception
            );
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
        if (downloadBaseUrl == null || downloadBaseUrl.trim().isEmpty()) {
            throw validationError("A receipt download base URL is required to complete download fallback");
        }

        DownloadLink link = downloadService.generateDownloadLink(downloadBaseUrl);
        String cheqiDocument = buildDownloadCheqiDocument(receiptPayload, identificationDetails);
        ReceiptEnvelope envelope = new ReceiptEnvelope()
                .cheqiReceiptId(link.getDownloadId())
                .envelopeVersion(1)
                .receiptGeneratorVersion(PAYLOAD_DOCUMENT_VERSION)
                .receiptUuid(UUID.randomUUID())
                .putDocumentsItem(
                        "CHEQI",
                        new ReceiptEnvelopeDocument()
                                .mediaType(ReceiptEnvelopeDocument.MediaTypeEnum.APPLICATION_JSON)
                                .content(cheqiDocument)
                );
        String ciphertext = downloadService.encryptDownloadEnvelope(envelope, link.getContentKey());
        String templateHash = verificationService.calculateCheqiReceiptHash(cheqiDocument);
        ClientReceiptDownloadRequest request = new ClientReceiptDownloadRequest()
                .downloadId(link.getDownloadId())
                .ciphertext(ciphertext)
                .templateHash(templateHash);
        ClientReceiptDownloadResponse response = accessToken == null
                ? apiClient.uploadEncryptedDownloadReceipt(request)
                : apiClient.uploadEncryptedDownloadReceipt(request, accessToken);
        return ReceiptResult.downloadAccepted(matchId, response, link.getUrl());
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
