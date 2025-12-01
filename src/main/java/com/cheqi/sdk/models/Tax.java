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
 * Tax applied to a product line.
 * Typical examples:
 * - VAT 21%
 * - GST 10%
 * - Sales Tax 8.25%
 * POS/back-end may either calculate the amount, or simply provide the rate.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Tax.Builder.class)
public final class Tax {

    /**
     * The tax rate as a percentage (e.g. 21.0 for 21%).
     */
    @JsonProperty("rate")
    private final Double rate;

    /**
     * The type of tax, e.g. "VAT", "GST", "Sales Tax".
     */
    @JsonProperty("type")
    private final String type;

    /**
     * Optional: The calculated tax amount for this line.
     * If not provided, Cheqi can compute this server-side.
     */
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Optional label, e.g. "VAT 21%" or "Reduced Rate".
     */
    @JsonProperty("label")
    private final String label;

    private Tax(Double rate, String type, BigDecimal amount, String label) {
        this.rate = rate;
        this.type = type;
        this.amount = amount;
        this.label = label;
    }

    // ===== GETTERS =====

    public Double getRate() {
        return rate;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getLabel() {
        return label;
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Double rate;
        private String type;
        private BigDecimal amount;
        private String label;

        private Builder() {}

        public Builder from(Tax other) {
            this.rate = other.rate;
            this.type = other.type;
            this.amount = other.amount;
            this.label = other.label;
            return this;
        }

        @JsonSetter(value = "rate", nulls = Nulls.SKIP)
        public Builder rate(Double rate) {
            this.rate = rate;
            return this;
        }

        @JsonSetter(value = "type", nulls = Nulls.SKIP)
        public Builder type(String type) {
            this.type = type;
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

        @JsonSetter(value = "label", nulls = Nulls.SKIP)
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Tax build() {
            if (rate == null) {
                throw new IllegalStateException("Tax rate is required");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalStateException("Tax type is required");
            }
            return new Tax(rate, type, amount, label);
        }
    }

    // ===== EQUALITY =====

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Tax)) return false;
        Tax that = (Tax) other;
        return Objects.equals(rate, that.rate)
                && Objects.equals(type, that.type)
                && Objects.equals(amount, that.amount)
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate, type, amount, label);
    }

    @Override
    public String toString() {
        return "Tax{" +
                "rate=" + rate +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", label='" + label + '\'' +
                '}';
    }
}