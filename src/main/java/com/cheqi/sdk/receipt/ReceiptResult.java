package com.cheqi.sdk.receipt;

import com.cheqi.sdk.models.generated.ReceiptCreatedResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

/**
 * Result of processing a receipt through the SDK.
 */
public class ReceiptResult {
    @JsonProperty("success")
    private final boolean success;
    @JsonProperty("deliveryStatus")
    private final DeliveryStatus deliveryStatus;
    @JsonProperty("cheqiReceiptId")
    private final String cheqiReceiptId;
    @JsonProperty("createdAt")
    private final OffsetDateTime createdAt;
    @JsonProperty("templateHash")
    private final String templateHash;
    @JsonProperty("canonicalJson")
    private final String canonicalJson;
    @JsonProperty("emailAddress")
    private final String emailAddress;
    @JsonProperty("downloadUrl")
    private final String downloadUrl;
    @JsonProperty("message")
    private final String message;

    private ReceiptResult(
            boolean success,
            DeliveryStatus deliveryStatus,
            String cheqiReceiptId,
            OffsetDateTime createdAt,
            String templateHash,
            String canonicalJson,
            String emailAddress,
            String downloadUrl,
            String message) {
        this.success = success;
        this.deliveryStatus = deliveryStatus;
        this.cheqiReceiptId = cheqiReceiptId;
        this.createdAt = createdAt;
        this.templateHash = templateHash;
        this.canonicalJson = canonicalJson;
        this.emailAddress = emailAddress;
        this.downloadUrl = downloadUrl;
        this.message = message;
    }

    public static ReceiptResult deliveredToApp(ReceiptCreatedResponse response, String canonicalJson) {
        return new ReceiptResult(
                true,
                DeliveryStatus.DELIVERED_DIGITAL,
                response.getCheqiReceiptId(),
                response.getCreatedAt(),
                response.getTemplateHash(),
                canonicalJson,
                null,
                null,
                "Receipt delivered to customer's Cheqi app"
        );
    }

    /**
     * QR-code self-service download fallback: the receipt was created and Cheqi returned a public
     * download URL. The merchant terminal renders {@code downloadUrl} as a QR code for the customer.
     */
    public static ReceiptResult deliveredViaDownload(ReceiptCreatedResponse response, String canonicalJson) {
        return new ReceiptResult(
                true,
                DeliveryStatus.DELIVERED_DOWNLOAD,
                response.getCheqiReceiptId(),
                response.getCreatedAt(),
                response.getTemplateHash(),
                canonicalJson,
                null,
                response.getDownloadUrl(),
                "Receipt available for self-service download"
        );
    }

    public static ReceiptResult deliveredViaEmail(String emailAddress) {
        return new ReceiptResult(
                true,
                DeliveryStatus.DELIVERED_EMAIL,
                null,
                OffsetDateTime.now(),
                null,
                null,
                emailAddress,
                null,
                "Receipt sent via email to " + emailAddress
        );
    }

    public static ReceiptResult customerNotFound() {
        return new ReceiptResult(
                false,
                DeliveryStatus.CUSTOMER_NOT_FOUND,
                null,
                null,
                null,
                null,
                null,
                null,
                "Customer not found with provided payment details"
        );
    }

    public static ReceiptResult failed(String message) {
        return new ReceiptResult(
                false,
                DeliveryStatus.FAILED,
                null,
                null,
                null,
                null,
                null,
                null,
                message
        );
    }

    // Core status checks
    public boolean isSuccess() { return success; }
    public boolean isCustomerNotFound() { return deliveryStatus == DeliveryStatus.CUSTOMER_NOT_FOUND; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public String getMessage() { return message; }

    // Archival data
    public String getCheqiReceiptId() { return cheqiReceiptId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getTemplateHash() { return templateHash; }
    public String getCanonicalJson() { return canonicalJson; }

    // Email flow
    public String getEmailAddress() { return emailAddress; }

    // Download fallback flow
    public String getDownloadUrl() { return downloadUrl; }

    @Override
    public String toString() {
        return "ReceiptResult{success=" + success + ", deliveryStatus=" + deliveryStatus +
                ", cheqiReceiptId='" + cheqiReceiptId + "'}";
    }
}