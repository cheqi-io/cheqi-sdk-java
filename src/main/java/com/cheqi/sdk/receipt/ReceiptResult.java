package com.cheqi.sdk.receipt;

import com.cheqi.sdk.models.generated.ClientReceiptDownloadResponse;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

/**
 * Outcome of receipt routing.
 *
 * <p>A digital route is submitted immediately. A download fallback with locally supplied
 * payment context is also completed immediately from the generated receipt payload and the
 * original generated identification details. A fallback without local payment context returns
 * {@link #isDownloadEnvelopeRequired()} so the caller can pass a final {@code ReceiptEnvelope} to
 * {@link ReceiptService#completeDownloadFallback(ReceiptResult,
 * com.cheqi.sdk.models.generated.ReceiptEnvelope, String, String)}.</p>
 */
public class ReceiptResult {
    @JsonProperty("cheqiReceiptId")
    private final String cheqiReceiptId;
    @JsonProperty("matchId")
    private final String matchId;
    @JsonProperty("deliveryRouteType")
    private final RecipientResolutionResponse.DeliveryRouteTypeEnum deliveryRouteType;
    @JsonProperty("status")
    private final ReceiptSubmissionResponse.StatusEnum status;
    @JsonProperty("createdAt")
    private final OffsetDateTime createdAt;
    @JsonProperty("expiresAt")
    private final OffsetDateTime expiresAt;
    @JsonProperty("downloadUrl")
    private final String downloadUrl;
    @JsonProperty("downloadEnvelopeRequired")
    private final boolean downloadEnvelopeRequired;
    @JsonProperty("emailReceiptRequired")
    private final boolean emailReceiptRequired;

    private ReceiptResult(
            String cheqiReceiptId,
            String matchId,
            RecipientResolutionResponse.DeliveryRouteTypeEnum deliveryRouteType,
            ReceiptSubmissionResponse.StatusEnum status,
            OffsetDateTime createdAt,
            OffsetDateTime expiresAt,
            String downloadUrl,
            boolean downloadEnvelopeRequired,
            boolean emailReceiptRequired
    ) {
        this.cheqiReceiptId = cheqiReceiptId;
        this.matchId = matchId;
        this.deliveryRouteType = deliveryRouteType;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.downloadUrl = downloadUrl;
        this.downloadEnvelopeRequired = downloadEnvelopeRequired;
        this.emailReceiptRequired = emailReceiptRequired;
    }

    public static ReceiptResult accepted(ReceiptSubmissionResponse response) {
        return accepted(response, RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL);
    }

    static ReceiptResult accepted(
            ReceiptSubmissionResponse response,
            RecipientResolutionResponse.DeliveryRouteTypeEnum deliveryRouteType
    ) {
        if (response == null) {
            throw new IllegalArgumentException("response cannot be null");
        }
        return new ReceiptResult(
                response.getCheqiReceiptId(),
                response.getMatchId(),
                deliveryRouteType,
                response.getStatus(),
                response.getCreatedAt(),
                null,
                null,
                false,
                false
        );
    }

    static ReceiptResult downloadEnvelopeRequired(RecipientResolutionResponse resolution) {
        return new ReceiptResult(
                null,
                resolution.getMatchId(),
                RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK,
                null,
                null,
                resolution.getExpiresAt(),
                null,
                true,
                false
        );
    }

    static ReceiptResult emailReceiptRequired(RecipientResolutionResponse resolution) {
        return new ReceiptResult(
                null,
                resolution.getMatchId(),
                RecipientResolutionResponse.DeliveryRouteTypeEnum.EMAIL_FALLBACK,
                null,
                null,
                resolution.getExpiresAt(),
                null,
                false,
                true
        );
    }

    static ReceiptResult downloadAccepted(
            String matchId,
            ClientReceiptDownloadResponse response,
            String downloadUrl
    ) {
        if (response == null) {
            throw new IllegalArgumentException("response cannot be null");
        }
        return new ReceiptResult(
                response.getCheqiReceiptId(),
                matchId,
                RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK,
                null,
                response.getCreatedAt(),
                response.getExpiresAt(),
                downloadUrl,
                false,
                false
        );
    }

    public boolean isAccepted() {
        return cheqiReceiptId != null && !cheqiReceiptId.trim().isEmpty();
    }

    public boolean isSuccess() {
        return isAccepted();
    }

    public boolean isDownloadEnvelopeRequired() {
        return downloadEnvelopeRequired;
    }

    public boolean isEmailReceiptRequired() {
        return emailReceiptRequired;
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public String getMatchId() {
        return matchId;
    }

    public RecipientResolutionResponse.DeliveryRouteTypeEnum getDeliveryRouteType() {
        return deliveryRouteType;
    }

    public ReceiptSubmissionResponse.StatusEnum getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
