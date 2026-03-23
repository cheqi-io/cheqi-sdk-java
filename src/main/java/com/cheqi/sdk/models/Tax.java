package com.cheqi.sdk.models;

import java.math.BigDecimal;

/**
 * Tax applied to a product line or receipt.
 * Extends the generated base with a builder pattern and convenience overloads.
 *
 * <pre>
 * Tax tax = Tax.builder()
 *     .rate(21.0)
 *     .type("VAT")
 *     .taxableAmount("1000.00")
 *     .amount("210.00")
 *     .label("VAT 21%")
 *     .build();
 * </pre>
 */
public class Tax extends com.cheqi.sdk.models.generated.Tax {

    public Tax() {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Double rate;
        private String type;
        private BigDecimal taxableAmount;
        private BigDecimal amount;
        private String label;

        private Builder() {}

        public Builder from(com.cheqi.sdk.models.generated.Tax other) {
            this.rate = other.getRate();
            this.type = other.getType();
            this.taxableAmount = other.getTaxableAmount();
            this.amount = other.getAmount();
            this.label = other.getLabel();
            return this;
        }

        public Builder rate(Double rate) {
            this.rate = rate;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder taxableAmount(BigDecimal taxableAmount) {
            this.taxableAmount = taxableAmount;
            return this;
        }

        public Builder taxableAmount(String taxableAmount) {
            this.taxableAmount = new BigDecimal(taxableAmount);
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = new BigDecimal(amount);
            return this;
        }

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
            Tax tax = new Tax();
            tax.setRate(rate);
            tax.setType(type);
            tax.setTaxableAmount(taxableAmount);
            tax.setAmount(amount);
            tax.setLabel(label);
            return tax;
        }
    }
}
