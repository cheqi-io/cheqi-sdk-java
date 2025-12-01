package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Discount applied to a product line.
 * You can specify either:
 * - a fixed amount (e.g. 5.00), or
 * - a percentage (e.g. 10.0 for 10%)
 * The POS/backend is responsible for the actual calculation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Discount.Builder.class)
public final class Discount {

    /**
     * Fixed discount amount for this line (e.g. 5.00 means 5 currency units off).
     */
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Percentage discount (e.g. 10.0 means 10% off).
     */
    @JsonProperty("percentage")
    private final Double percentage;

    /**
     * Human-readable label, e.g. "Black Friday", "Coupon", "Loyalty".
     */
    @JsonProperty("label")
    private final String label;

    private Discount(BigDecimal amount, Double percentage, String label) {
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
     * Creates a discount with a fixed amount.
     * @param amount The discount amount
     * @param label The discount label/description
     * @return A new Discount instance
     */
    public static Discount of(BigDecimal amount, String label) {
        return new Discount(amount, null, label);
    }

    /**
     * Creates a discount with a fixed amount (String).
     * @param amount The discount amount as a string
     * @param label The discount label/description
     * @return A new Discount instance
     */
    public static Discount of(String amount, String label) {
        return new Discount(new BigDecimal(amount), null, label);
    }

    /**
     * Creates a discount with both amount and percentage.
     * @param amount The discount amount
     * @param percentage The discount percentage
     * @param label The discount label/description
     * @return A new Discount instance
     */
    public static Discount of(BigDecimal amount, Double percentage, String label) {
        return new Discount(amount, percentage, label);
    }

    /**
     * Creates a discount with both amount and percentage (String amount).
     * @param amount The discount amount as a string
     * @param percentage The discount percentage
     * @param label The discount label/description
     * @return A new Discount instance
     */
    public static Discount of(String amount, Double percentage, String label) {
        return new Discount(new BigDecimal(amount), percentage, label);
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

        public Builder from(Discount other) {
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

        public Discount build() {
            // Optional: enforce business rule "one of amount or percentage"
            if (amount != null && percentage != null) {
                throw new IllegalStateException(
                        "Discount: only one of amount or percentage should be set");
            }
            if (amount == null && percentage == null) {
                throw new IllegalStateException(
                        "Discount: one of amount or percentage must be set");
            }
            return new Discount(amount, percentage, label);
        }
    }

    // ===== EQUALITY =====

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Discount)) return false;
        Discount that = (Discount) other;
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
        return "Discount{" +
                "amount=" + amount +
                ", percentage=" + percentage +
                ", label='" + label + '\'' +
                '}';
    }
}
