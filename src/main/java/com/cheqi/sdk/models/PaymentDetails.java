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
 *   <li><strong>customerEmail</strong>: Customer email for identification</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = PaymentDetails.Builder.class)
public final class PaymentDetails {

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

    @JsonProperty("customerEmail")
    private final Optional<String> customerEmail;

    // ===== CONSTRUCTOR =====
    private PaymentDetails(
            PaymentType paymentType,
            Optional<CardDetails> cardDetails,
            Optional<PaymentAccountDetails> paymentAccountDetails,
            Optional<List<String>> paymentIds,
            Optional<String> customerEmail) {
        this.paymentType = paymentType;
        this.cardDetails = cardDetails;
        this.paymentAccountDetails = paymentAccountDetails;
        this.paymentIds = paymentIds;
        this.customerEmail = customerEmail;
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
    public Optional<String> getCustomerEmail() {
        return customerEmail != null ? customerEmail : Optional.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof PaymentDetails && equalTo((PaymentDetails) other);
    }

    private boolean equalTo(PaymentDetails other) {
        return Objects.equals(this.paymentType, other.paymentType)
                && Objects.equals(this.cardDetails, other.cardDetails)
                && Objects.equals(this.paymentAccountDetails, other.paymentAccountDetails)
                && Objects.equals(this.paymentIds, other.paymentIds)
                && Objects.equals(this.customerEmail, other.customerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.paymentType, this.cardDetails, this.paymentAccountDetails, this.paymentIds, this.customerEmail);
    }

    @Override
    public String toString() {
        return "PaymentDetails{" +
                "paymentType=" + paymentType +
                ", cardDetails=" + cardDetails +
                ", paymentAccountDetails=" + paymentAccountDetails +
                ", paymentIds=" + paymentIds +
                ", customerEmail=" + customerEmail +
                '}';
    }

    public static PaymentDetails.Builder builder() {
        return new PaymentDetails.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private PaymentType paymentType;
        private Optional<CardDetails> cardDetails = Optional.empty();
        private Optional<PaymentAccountDetails> paymentAccountDetails = Optional.empty();
        private Optional<List<String>> paymentIds = Optional.empty();
        private Optional<String> customerEmail = Optional.empty();

        private Builder() {
        }

        public PaymentDetails.Builder from(PaymentDetails other) {
            paymentType(other.getPaymentType());
            cardDetails(other.getCardDetails().orElse(null));
            paymentAccountDetails(other.getPaymentAccountDetails().orElse(null));
            paymentIds(other.getPaymentIds().orElse(null));
            customerEmail(other.getCustomerEmail().orElse(null));
            return this;
        }

        @JsonSetter(value = "paymentType", nulls = Nulls.SKIP)
        public PaymentDetails.Builder paymentType(PaymentType paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        @JsonSetter(value = "cardDetails", nulls = Nulls.SKIP)
        public PaymentDetails.Builder cardDetails(CardDetails cardDetails) {
            this.cardDetails = Optional.ofNullable(cardDetails);
            return this;
        }

        @JsonSetter(value = "paymentAccountDetails", nulls = Nulls.SKIP)
        public PaymentDetails.Builder paymentAccountDetails(PaymentAccountDetails paymentAccountDetails) {
            this.paymentAccountDetails = Optional.ofNullable(paymentAccountDetails);
            return this;
        }

        @JsonSetter(value = "paymentIds", nulls = Nulls.SKIP)
        public PaymentDetails.Builder paymentIds(List<String> paymentIds) {
            this.paymentIds = Optional.ofNullable(paymentIds);
            return this;
        }

        @JsonSetter(value = "customerEmail", nulls = Nulls.SKIP)
        public PaymentDetails.Builder customerEmail(String customerEmail) {
            this.customerEmail = Optional.ofNullable(customerEmail);
            return this;
        }

        public PaymentDetails build() {
            return new PaymentDetails(paymentType, cardDetails, paymentAccountDetails, paymentIds, customerEmail);
        }
    }
}