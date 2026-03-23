package com.cheqi.sdk.models;

import java.math.BigDecimal;

/**
 * Discount applied to a product line or receipt.
 * Extends the generated base with a builder pattern and factory methods.
 *
 * <pre>
 * Discount discount = Discount.of("5.00", "Loyalty discount");
 * Discount discount = Discount.builder().amount("5.00").label("Loyalty").build();
 * </pre>
 */
public class Discount extends com.cheqi.sdk.models.generated.Discount {

    public Discount() {}

    // ===== FACTORY METHODS =====

    public static Discount of(BigDecimal amount, String label) {
        Discount d = new Discount();
        d.setAmount(amount);
        d.setLabel(label);
        return d;
    }

    public static Discount of(String amount, String label) {
        return of(new BigDecimal(amount), label);
    }

    public static Discount of(BigDecimal amount, Double percentage, String label) {
        Discount d = new Discount();
        d.setAmount(amount);
        d.setPercentage(percentage);
        d.setLabel(label);
        return d;
    }

    public static Discount of(String amount, Double percentage, String label) {
        return of(new BigDecimal(amount), percentage, label);
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BigDecimal amount;
        private Double percentage;
        private String label;

        private Builder() {}

        public Builder from(com.cheqi.sdk.models.generated.Discount other) {
            this.amount = other.getAmount();
            this.percentage = other.getPercentage();
            this.label = other.getLabel();
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

        public Builder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Discount build() {
            Discount d = new Discount();
            d.setAmount(amount);
            d.setPercentage(percentage);
            d.setLabel(label);
            return d;
        }
    }
}
