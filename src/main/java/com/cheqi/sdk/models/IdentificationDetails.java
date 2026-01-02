package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private final Optional<CardDetails> cardDetails;

    @JsonProperty("paymentAccountDetails")
    private final Optional<PaymentAccountDetails> paymentAccountDetails;

    @JsonProperty("paymentIds")
    private final Optional<List<String>> paymentIds;

    @JsonProperty("recipientEmail")
    private final Optional<String> recipientEmail;

    @JsonProperty("cheqiReceiptId")
    private final Optional<String> cheqiReceiptId;

    // ===== CONSTRUCTOR =====
    private IdentificationDetails(
            PaymentType paymentType,
            Optional<CardDetails> cardDetails,
            Optional<PaymentAccountDetails> paymentAccountDetails,
            Optional<List<String>> paymentIds,
            Optional<String> recipientEmail,
            Optional<String> cheqiReceiptId
    ) {
        this.paymentType = paymentType;
        this.cardDetails = cardDetails;
        this.paymentAccountDetails = paymentAccountDetails;
        this.paymentIds = paymentIds;
        this.recipientEmail = recipientEmail;
        this.cheqiReceiptId = cheqiReceiptId;
    }

    // ===== MANDATORY FIELD ACCESSORS =====
    public PaymentType getPaymentType() {
        return paymentType;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====
    @JsonIgnore
    public Optional<CardDetails> getCardDetails() {
        return cardDetails != null ? cardDetails : Optional.empty();
    }

    @JsonIgnore
    public Optional<PaymentAccountDetails> getPaymentAccountDetails() {
        return paymentAccountDetails != null ? paymentAccountDetails : Optional.empty();
    }

    @JsonIgnore
    public Optional<List<String>> getPaymentIds() {
        return paymentIds != null ? paymentIds : Optional.empty();
    }

    @JsonIgnore
    public Optional<String> getRecipientEmail() {
        return recipientEmail != null ? recipientEmail : Optional.empty();
    }

    @JsonIgnore
    public Optional<String> getCheqiReceiptId() {
        return cheqiReceiptId != null ? cheqiReceiptId : Optional.empty();
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
                && Objects.equals(this.cheqiReceiptId, other.cheqiReceiptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.paymentType, this.cardDetails, this.paymentAccountDetails, this.paymentIds, this.recipientEmail, this.cheqiReceiptId);
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
                '}';
    }

    public static IdentificationDetails.Builder builder() {
        return new IdentificationDetails.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private PaymentType paymentType;
        private Optional<CardDetails> cardDetails = Optional.empty();
        private Optional<PaymentAccountDetails> paymentAccountDetails = Optional.empty();
        private Optional<List<String>> paymentIds = Optional.empty();
        private Optional<String> recipientEmail = Optional.empty();
        private Optional<String> cheqiReceiptId = Optional.empty();

        private Builder() {
        }

        public IdentificationDetails.Builder from(IdentificationDetails other) {
            paymentType(other.getPaymentType());
            cardDetails(other.getCardDetails().orElse(null));
            paymentAccountDetails(other.getPaymentAccountDetails().orElse(null));
            paymentIds(other.getPaymentIds().orElse(null));
            recipientEmail(other.getRecipientEmail().orElse(null));
            cheqiReceiptId(other.getCheqiReceiptId().orElse(null));
            return this;
        }

        @JsonSetter(value = "paymentType", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        @JsonSetter(value = "cardDetails", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder cardDetails(CardDetails cardDetails) {
            this.cardDetails = Optional.ofNullable(cardDetails);
            return this;
        }

        @JsonSetter(value = "paymentAccountDetails", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentAccountDetails(PaymentAccountDetails paymentAccountDetails) {
            this.paymentAccountDetails = Optional.ofNullable(paymentAccountDetails);
            return this;
        }

        @JsonSetter(value = "paymentIds", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder paymentIds(List<String> paymentIds) {
            this.paymentIds = Optional.ofNullable(paymentIds);
            return this;
        }

        @JsonSetter(value = "recipientEmail", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder recipientEmail(String recipientEmail) {
            this.recipientEmail = Optional.ofNullable(recipientEmail);
            return this;
        }

        @JsonSetter(value = "cheqiReceiptId", nulls = Nulls.SKIP)
        public IdentificationDetails.Builder cheqiReceiptId(String cheqiReceiptId) {
            this.cheqiReceiptId = Optional.ofNullable(cheqiReceiptId);
            return this;
        }

        public IdentificationDetails build() {
            return new IdentificationDetails(paymentType, cardDetails, paymentAccountDetails, paymentIds, recipientEmail, cheqiReceiptId);
        }
    }
}
