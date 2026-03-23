package com.cheqi.sdk.models;

import java.math.BigDecimal;

/**
 * Extra charge (fee) applied to a product line or receipt.
 * Extends the generated base with a builder pattern and factory methods.
 *
 * <pre>
 * Charge charge = Charge.of("3.00", "Service fee");
 * Charge charge = Charge.builder().amount("3.00").label("Shipping").build();
 * </pre>
 */
public class Charge extends com.cheqi.sdk.models.generated.Charge {

    public Charge() {}

    // ===== FACTORY METHODS =====

    public static Charge of(BigDecimal amount, String label) {
        Charge c = new Charge();
        c.setAmount(amount);
        c.setLabel(label);
        return c;
    }

    public static Charge of(String amount, String label) {
        return of(new BigDecimal(amount), label);
    }

    public static Charge of(BigDecimal amount, Double percentage, String label) {
        Charge c = new Charge();
        c.setAmount(amount);
        c.setPercentage(percentage);
        c.setLabel(label);
        return c;
    }

    public static Charge of(String amount, Double percentage, String label) {
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

        public Builder from(com.cheqi.sdk.models.generated.Charge other) {
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

        public Charge build() {
            Charge c = new Charge();
            c.setAmount(amount);
            c.setPercentage(percentage);
            c.setLabel(label);
            return c;
        }
    }
}
