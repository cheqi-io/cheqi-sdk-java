package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.receipt.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CreditNoteResult {
    @JsonProperty("success")
    private final boolean success;
    @JsonProperty("deliveryStatus")
    private final DeliveryStatus deliveryStatus;
    @JsonProperty("cheqiReceiptId")
    private final String cheqiReceiptId;
    @JsonProperty("cheqiCreditNoteId")
    private final String parentCheqiReceiptId;
    @JsonProperty("createdAt")
    private final Instant createdAt;
    @JsonProperty("templateHash")
    private final String templateHash;
    @JsonProperty("canonicalJson")
    private final String canonicalJson;
    @JsonProperty("emailAddress")
    private final String emailAddress;
    @JsonProperty("message")
    private final String message;

    private CreditNoteResult(
            boolean success,
            DeliveryStatus deliveryStatus,
            String cheqiReceiptId,
            String parentCheqiReceiptId,
            Instant createdAt,
            String templateHash,
            String canonicalJson,
            String emailAddress,
            String message) {
        this.success = success;
        this.deliveryStatus = deliveryStatus;
        this.cheqiReceiptId = cheqiReceiptId;
        this.parentCheqiReceiptId = parentCheqiReceiptId;
        this.createdAt = createdAt;
        this.templateHash = templateHash;
        this.canonicalJson = canonicalJson;
        this.emailAddress = emailAddress;
        this.message = message;
    }

    public static CreditNoteResult deliveredToApp(CreditNoteCreatedResponse response, String canonicalJson) {
        return new CreditNoteResult(
                true,
                DeliveryStatus.DELIVERED_DIGITAL,
                response.getCheqiReceiptId(),
                response.getParentCheqiReceiptId(),
                response.getCreatedAt(),
                response.getTemplateHash(),
                canonicalJson,
                null,
                "Receipt delivered to customer's Cheqi app"
        );
    }

    public static CreditNoteResult deliveredViaEmail(String cheqiReceiptId, String parentCheqiReceiptId, String emailAddress) {
        return new CreditNoteResult(
                true,
                DeliveryStatus.DELIVERED_EMAIL,
                cheqiReceiptId,
                parentCheqiReceiptId,
                Instant.now(),
                null,
                null,
                emailAddress,
                "Receipt sent via email to " + emailAddress
        );
    }

    public static CreditNoteResult customerNotFound() {
        return new CreditNoteResult(
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

    public static CreditNoteResult failed(String message) {
        return new CreditNoteResult(
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
    public Instant getCreatedAt() { return createdAt; }
    public String getTemplateHash() { return templateHash; }
    public String getCanonicalJson() { return canonicalJson; }

    public String getParentCheqiReceiptId() { return parentCheqiReceiptId; }

    // Email flow
    public String getEmailAddress() { return emailAddress; }

    @Override
    public String toString() {
        return "CreditNoteResult{success=" + success + ", deliveryStatus=" + deliveryStatus +
                ", cheqiReceiptId='" + cheqiReceiptId + "'}";
    }
}
