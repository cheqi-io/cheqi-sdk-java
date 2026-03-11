package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Extra charge (fee) applied to a product line.
 * Examples: "Shipping", "Service fee", "Bottle deposit".
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Charge.Builder.class)
public final class Charge {

    /**
     * Fixed charge amount for this line (e.g. 5.00 means 5 currency units extra).
     */
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Percentage charge (e.g. 3.0 means 3% fee).
     */
    @JsonProperty("percentage")
    private final Double percentage;

    /**
     * Human-readable label, e.g. "Shipping", "Service fee".
     */
    @JsonProperty("label")
    private final String label;

    private Charge(BigDecimal amount, Double percentage, String label) {
        this.amount = amount;
        this.percentage = percentage;
        this.label = label;
    }

    // ===== GETTERS =====

    public BigDecimal getAmount() {
        return amount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public String getLabel() {
        return label;
    }

    // ===== FACTORY METHODS =====

    /**
     * Creates a charge with a fixed amount.
     * @param amount The charge amount
     * @param label The charge label/description
     * @return A new Charge instance
     */
    public static Charge of(BigDecimal amount, String label) {
        return new Charge(amount, null, label);
    }

    /**
     * Creates a charge with a fixed amount (String).
     * @param amount The charge amount as a string
     * @param label The charge label/description
     * @return A new Charge instance
     */
    public static Charge of(String amount, String label) {
        return new Charge(new BigDecimal(amount), null, label);
    }

    /**
     * Creates a charge with both amount and percentage.
     * @param amount The charge amount
     * @param percentage The charge percentage
     * @param label The charge label/description
     * @return A new Charge instance
     */
    public static Charge of(BigDecimal amount, Double percentage, String label) {
        return new Charge(amount, percentage, label);
    }

    /**
     * Creates a charge with both amount and percentage (String amount).
     * @param amount The charge amount as a string
     * @param percentage The charge percentage
     * @param label The charge label/description
     * @return A new Charge instance
     */
    public static Charge of(String amount, Double percentage, String label) {
        return new Charge(new BigDecimal(amount), percentage, label);
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private BigDecimal amount;
        private Double percentage;
        private String label;

        private Builder() {}

        public Builder from(Charge other) {
            this.amount = other.amount;
            this.percentage = other.percentage;
            this.label = other.label;
            return this;
        }

        @JsonSetter(value = "amount", nulls = Nulls.SKIP)
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = new BigDecimal(amount);
            return this;
        }

        @JsonSetter(value = "percentage", nulls = Nulls.SKIP)
        public Builder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        @JsonSetter(value = "label", nulls = Nulls.SKIP)
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Charge build() {
            // Same rule as Discount, if you want consistency:
            if (amount != null && percentage != null) {
                throw new IllegalStateException(
                        "Charge: only one of amount or percentage should be set");
            }
            if (amount == null && percentage == null) {
                throw new IllegalStateException(
                        "Charge: one of amount or percentage must be set");
            }
            return new Charge(amount, percentage, label);
        }
    }

    // ===== EQUALITY =====

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Charge)) return false;
        Charge that = (Charge) other;
        return Objects.equals(amount, that.amount)
                && Objects.equals(percentage, that.percentage)
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, percentage, label);
    }

    @Override
    public String toString() {
        return "Charge{" +
                "amount=" + amount +
                ", percentage=" + percentage +
                ", label='" + label + '\'' +
                '}';
    }
}
