package com.cheqi.sdk.models;

import com.cheqi.commons.enums.CardProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Card details DTO representing payment card information.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>cardProvider</strong>: The card provider (VISA, MASTERCARD, etc.)</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>paymentAccountNumber</strong>: Primary Account Number (PAN)</li>
 *   <li><strong>paymentAccountReference</strong>: Payment Account Reference (PAR)</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = CardDetails.Builder.class)
public final class CardDetails {

    // ===== MANDATORY FIELDS =====
    /**
     * The card provider (VISA, MASTERCARD, etc.).
     */
    @JsonProperty("cardProvider")
    private final CardProvider cardProvider;

    // ===== OPTIONAL FIELDS =====
    /**
     * Primary Account Number (PAN) of the card.
     */
    private final Optional<String> paymentAccountNumber;

    /**
     * Payment Account Reference (PAR) - a unique identifier for the card.
     */
    private final Optional<String> paymentAccountReference;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====
    private CardDetails(
            CardProvider cardProvider,
            Optional<String> paymentAccountNumber,
            Optional<String> paymentAccountReference,
            Map<String, Object> additionalProperties) {
        this.cardProvider = cardProvider;
        this.paymentAccountNumber = paymentAccountNumber;
        this.paymentAccountReference = paymentAccountReference;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    /**
     * @return The card provider
     */
    @JsonIgnore
    public CardProvider getCardProvider() {
        return cardProvider;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The payment account number (PAN) if provided
     */
    @JsonIgnore
    public Optional<String> getPaymentAccountNumber() {
        if (paymentAccountNumber == null) {
            return Optional.empty();
        }
        return paymentAccountNumber;
    }

    /**
     * @return The payment account reference (PAR) if provided
     */
    @JsonIgnore
    public Optional<String> getPaymentAccountReference() {
        if (paymentAccountReference == null) {
            return Optional.empty();
        }
        return paymentAccountReference;
    }

    /**
     * @return Additional properties map
     */
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof CardDetails && equalTo((CardDetails) other);
    }

    private boolean equalTo(CardDetails other) {
        return Objects.equals(this.cardProvider, other.cardProvider)
                && Objects.equals(this.paymentAccountNumber, other.paymentAccountNumber)
                && Objects.equals(this.paymentAccountReference, other.paymentAccountReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.cardProvider,
                this.paymentAccountNumber,
                this.paymentAccountReference
        );
    }

    @Override
    public String toString() {
        return "CardDetails{" +
                "cardProvider=" + cardProvider +
                ", paymentAccountNumber=" + paymentAccountNumber +
                ", paymentAccountReference=" + paymentAccountReference +
                '}';
    }

    public static CardDetails.Builder builder() {
        return new CardDetails.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private CardProvider cardProvider;
        private Optional<String> paymentAccountNumber = Optional.empty();
        private Optional<String> paymentAccountReference = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public CardDetails.Builder from(CardDetails other) {
            cardProvider(other.getCardProvider());
            paymentAccountNumber(other.getPaymentAccountNumber());
            paymentAccountReference(other.getPaymentAccountReference());
            return this;
        }

        @JsonSetter(value = "cardProvider", nulls = Nulls.SKIP)
        public CardDetails.Builder cardProvider(CardProvider cardProvider) {
            this.cardProvider = cardProvider;
            return this;
        }

        @JsonSetter(value = "paymentAccountNumber", nulls = Nulls.SKIP)
        public CardDetails.Builder paymentAccountNumber(Optional<String> paymentAccountNumber) {
            this.paymentAccountNumber = paymentAccountNumber;
            return this;
        }

        public CardDetails.Builder paymentAccountNumber(String paymentAccountNumber) {
            this.paymentAccountNumber = Optional.ofNullable(paymentAccountNumber);
            return this;
        }

        @JsonSetter(value = "paymentAccountReference", nulls = Nulls.SKIP)
        public CardDetails.Builder paymentAccountReference(Optional<String> paymentAccountReference) {
            this.paymentAccountReference = paymentAccountReference;
            return this;
        }

        public CardDetails.Builder paymentAccountReference(String paymentAccountReference) {
            this.paymentAccountReference = Optional.ofNullable(paymentAccountReference);
            return this;
        }

        public CardDetails build() {
            return new CardDetails(
                    cardProvider,
                    paymentAccountNumber,
                    paymentAccountReference,
                    additionalProperties
            );
        }
    }

    // ===== PRIVATE JSON SERIALIZATION METHODS =====

    @JsonProperty("paymentAccountNumber")
    private Optional<String> _getPaymentAccountNumber() {
        return paymentAccountNumber;
    }

    @JsonProperty("paymentAccountReference")
    private Optional<String> _getPaymentAccountReference() {
        return paymentAccountReference;
    }
}