package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Objects;

/**
 * Payment details DTO containing complete payment information for UBL PaymentMeans generation.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>paymentType</strong>: The type of payment being processed</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>cardDetails</strong>: Card details for card-based payments</li>
 *   <li><strong>paymentAccountDetails</strong>: Payment account details for direct debit payments</li>
 *   <li><strong>paymentIds</strong>: List of payment identifiers</li>
 *   <li><strong>recipientEmail</strong>: Customer email for identification</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = IdentificationDetails.Builder.class)
public final class IdentificationDetails {

    // ===== MANDATORY FIELDS =====
    @JsonProperty("paymentType")
    private final PaymentType paymentType;

    // ===== OPTIONAL FIELDS =====
    @JsonProperty("cardDetails")
    private final CardDetails cardDetails;

    @JsonProperty("paymentAccountDetails")
    private final PaymentAccountDetails paymentAccountDetails;

    @JsonProperty("paymentIds")
    private final List<String> paymentIds;

    @JsonProperty("recipientEmail")
    private final String recipientEmail;

    @JsonProperty("cheqiReceiptId")
    private final String cheqiReceiptId;

    @JsonProperty("pairingCode")
    private final String pairingCode;

    // ===== CONSTRUCTOR =====
    private IdentificationDetails(
            PaymentType paymentType,
            CardDetails cardDetails,
            PaymentAccountDetails paymentAccountDetails,
            List<String> paymentIds,
            String recipientEmail,
            String cheqiReceiptId,
            String pairingCode
    ) {
        this.paymentType = paymentType;
        this.cardDetails = cardDetails;
        this.paymentAccountDetails = paymentAccountDetails;
        this.paymentIds = paymentIds;
        this.recipientEmail = recipientEmail;
        this.cheqiReceiptId = cheqiReceiptId;
        this.pairingCode = pairingCode;
    }

    // ===== MANDATORY FIELD ACCESSORS =====
    public PaymentType getPaymentType() {
        return paymentType;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public PaymentAccountDetails getPaymentAccountDetails() {
        return paymentAccountDetails;
    }

    public List<String> getPaymentIds() {
        return paymentIds;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof IdentificationDetails && equalTo((IdentificationDetails) other);
    }

    private boolean equalTo(IdentificationDetails other) {
        return Objects.equals(this.paymentType, other.paymentType)
                && Objects.equals(this.cardDetails, other.cardDetails)
                && Objects.equals(this.paymentAccountDetails, other.paymentAccountDetails)
                && Objects.equals(this.paymentIds, other.paymentIds)
                && Objects.equals(this.recipientEmail, other.recipientEmail)
                && Objects.equals(this.cheqiReceiptId, other.cheqiReceiptId)
                && Objects.equals(this.pairingCode, other.pairingCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.paymentType, this.cardDetails, this.paymentAccountDetails, this.paymentIds, this.recipientEmail, this.cheqiReceiptId, this.pairingCode);
    }

    @Override
    public String toString() {
        return "IdentificationDetails{" +
                "paymentType=" + paymentType +
                ", cardDetails=" + cardDetails +
                ", paymentAccountDetails=" + paymentAccountDetails +
                ", paymentIds=" + paymentIds +
                ", recipientEmail=" + recipientEmail +
                ", cheqiReceiptId=" + cheqiReceiptId +
                ", pairingCode=" + pairingCode +
                '}';
    }

    public static IdentificationDetails.Builder builder() {
        return new IdentificationDetails.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private PaymentType paymentType;
        private CardDetails cardDetails;
        private PaymentAccountDetails paymentAccountDetails;
        private List<String> paymentIds;
        private String recipientEmail;
        private String cheqiReceiptId;
        private String pairingCode;

        private Builder() {
        }

        public IdentificationDetails.Builder from(IdentificationDetails other) {
            paymentType(other.getPaymentType());
            cardDetails(other.getCardDetails());
            paymentAccountDetails(other.getPaymentAccountDetails());
            paymentIds(other.getPaymentIds());
            recipientEmail(other.getRecipientEmail());
            cheqiReceiptId(other.getCheqiReceiptId());
            pairingCode(other.getPairingCode());
            return this;
        }

        @JsonSetter(value = "paymentType", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        @JsonSetter(value = "cardDetails", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder cardDetails(CardDetails cardDetails) {
            this.cardDetails = cardDetails;
            return this;
        }

        @JsonSetter(value = "paymentAccountDetails", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentAccountDetails(PaymentAccountDetails paymentAccountDetails) {
            this.paymentAccountDetails = paymentAccountDetails;
            return this;
        }

        @JsonSetter(value = "paymentIds", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentIds(List<String> paymentIds) {
            this.paymentIds = paymentIds;
            return this;
        }

        @JsonSetter(value = "recipientEmail", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder recipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        @JsonSetter(value = "cheqiReceiptId", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder cheqiReceiptId(String cheqiReceiptId) {
            this.cheqiReceiptId = cheqiReceiptId;
            return this;
        }

        @JsonSetter(value = "pairingCode", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder pairingCode(String pairingCode) {
            this.pairingCode = pairingCode;
            return this;
        }

        public IdentificationDetails build() {
            return new IdentificationDetails(paymentType, cardDetails, paymentAccountDetails, paymentIds, recipientEmail, cheqiReceiptId, pairingCode);
        }
    }
}
