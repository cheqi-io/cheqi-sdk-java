package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Tax applied to a product line or receipt.
 * 
 * <p>Typical examples:</p>
 * <ul>
 *   <li>VAT 21%</li>
 *   <li>GST 10%</li>
 *   <li>Sales Tax 8.25%</li>
 * </ul>
 * 
 * <h3>Example:</h3>
 * <pre>
 * Tax tax = Tax.builder()
 *     .rate(19.0)                    // 19% tax rate
 *     .type("VAT")
 *     .taxableAmount("8500.00")      // €8500 taxable amount
 *     .amount("1615.00")             // €1615 tax (8500 × 0.19)
 *     .label("VAT 19%")
 *     .build();
 * </pre>
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
     * The taxable amount (base amount before tax).
     * This is the amount that the tax rate is applied to.
     * 
     * <p>Example: €8500 taxable amount × 19% = €1615 tax</p>
     */
    @JsonProperty("taxableAmount")
    private final BigDecimal taxableAmount;

    /**
     * The calculated tax amount.
     * Should equal: taxableAmount × (rate / 100)
     * 
     * <p>Example: €8500 × 0.19 = €1615</p>
     */
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Optional label, e.g. "VAT 21%" or "Reduced Rate".
     */
    @JsonProperty("label")
    private final String label;

    private Tax(Double rate, String type, BigDecimal taxableAmount, BigDecimal amount, String label) {
        this.rate = rate;
        this.type = type;
        this.taxableAmount = taxableAmount;
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

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
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
        private BigDecimal taxableAmount;
        private BigDecimal amount;
        private String label;

        private Builder() {}

        public Builder from(Tax other) {
            this.rate = other.rate;
            this.type = other.type;
            this.taxableAmount = other.taxableAmount;
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

        /**
         * Sets the taxable amount (base amount before tax).
         * @param taxableAmount The amount that tax is calculated on
         * @return This builder
         */
        @JsonSetter(value = "taxableAmount", nulls = Nulls.SKIP)
        public Builder taxableAmount(BigDecimal taxableAmount) {
            this.taxableAmount = taxableAmount;
            return this;
        }

        /**
         * Sets the taxable amount (base amount before tax).
         * @param taxableAmount The amount that tax is calculated on
         * @return This builder
         */
        public Builder taxableAmount(String taxableAmount) {
            this.taxableAmount = new BigDecimal(taxableAmount);
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
            if (taxableAmount == null) {
                throw new IllegalStateException("Tax taxableAmount is required");
            }
            return new Tax(rate, type, taxableAmount, amount, label);
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
                && Objects.equals(taxableAmount, that.taxableAmount)
                && Objects.equals(amount, that.amount)
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate, type, taxableAmount, amount, label);
    }

    @Override
    public String toString() {
        return "Tax{" +
                "rate=" + rate +
                ", type='" + type + '\'' +
                ", taxableAmount=" + taxableAmount +
                ", amount=" + amount +
                ", label='" + label + '\'' +
                '}';
    }
}