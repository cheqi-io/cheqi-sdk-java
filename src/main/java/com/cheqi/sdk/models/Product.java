package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple product line item used in Cheqi receipts.
 * This is intentionally "dumb":
 * - No UBL/PEPPOL types
 * - No Optional
 * - POS/backend just fills in what it already knows
 * The Cheqi backend handles UBL/PEPPOL mapping and validation.
 *
 * <h3>Typical usage:</h3>
 * <pre>
 * Product product = Product.builder()
 *     .name("Laptop 13\"")
 *     .sku("LAP-001")
 *     .description("13\" Ultrabook, 16GB RAM, 512GB SSD")
 *     .quantity(1.0)
 *     .unitCode(UnitCode.ONE)  // Type-safe enum
 *     .unitPrice("1000.00")
 *     .addDiscount("50.00", "Black Friday")
 *     .addCharge("5.00", "Shipping")
 *     .addTax(21.0, "VAT", "199.95")
 *     .subtotal("950.00")      // net line total (before tax)
 *     .total("1149.95")       // gross line total (after tax)
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Product.Builder.class)
public final class Product {

    // ===== BASIC INFO =====

    /**
     * Human-readable product name shown on the receipt.
     */
    @JsonProperty("name")
    private final String name;

    /**
     * SKU or article number (merchant-defined).
     */
    @JsonProperty("sku")
    private final String sku;

    /**
     * Optional longer description.
     */
    @JsonProperty("description")
    private final String description;

    /**
     * Optional brand name.
     */
    @JsonProperty("brand")
    private final String brand;

    // ===== QUANTITY & PRICE =====

    /**
     * Quantity of this item (e.g., 1, 2.5, etc.).
     */
    @JsonProperty("quantity")
    private final Double quantity;

    /**
     * Unit of measure code (mandatory).
     * Uses UN/ECE Recommendation 20 standard codes.
     * Examples: ONE (C62), KILOGRAM (KGM), LITER (LTR), HOUR (HUR)
     * 
     * @see UnitCode
     */
    @JsonProperty("unitCode")
    private final UnitCode unitCode;

    /**
     * Unit price (net, before tax) as seen in the POS.
     */
    @JsonProperty("unitPrice")
    private final BigDecimal unitPrice;

    // ===== DISCOUNTS & CHARGES =====

    /**
     * Discounts applied to this line (POS-calculated).
     */
    @JsonProperty("discounts")
    private final List<Discount> discounts;

    /**
     * Extra charges applied to this line (POS-calculated).
     */
    @JsonProperty("charges")
    private final List<Charge> charges;

    // ===== TAXES =====

    /**
     * Taxes applied to this line.
     * For many countries there will be exactly one tax (e.g. 21% VAT),
     * but multiple entries are allowed for flexibility.
     */
    @JsonProperty("taxes")
    private final List<Tax> taxes;

    // ===== TOTALS (FROM POS) =====

    /**
     * Line subtotal, before tax.
     * Typically: quantity * unitPrice - discounts + charges (net).
     */
    @JsonProperty("subtotal")
    private final BigDecimal subtotal;

    /**
     * Line total, after tax.
     * Typically: subtotal + tax amounts (gross).
     */
    @JsonProperty("total")
    private final BigDecimal total;

    // ===== CONSTRUCTOR =====

    private Product(
            String name,
            String sku,
            String description,
            String brand,
            Double quantity,
            UnitCode unitCode,
            BigDecimal unitPrice,
            List<Discount> discounts,
            List<Charge> charges,
            List<Tax> taxes,
            BigDecimal subtotal,
            BigDecimal total) {

        this.name = name;
        this.sku = sku;
        this.description = description;
        this.brand = brand;
        this.quantity = quantity;
        this.unitCode = unitCode;
        this.unitPrice = unitPrice;
        // defensive copies: keep internal lists immutable
        this.discounts = discounts != null ? List.copyOf(discounts) : List.of();
        this.charges   = charges   != null ? List.copyOf(charges)   : List.of();
        this.taxes     = taxes     != null ? List.copyOf(taxes)     : List.of();
        this.subtotal = subtotal;
        this.total = total;
    }

    // ===== GETTERS =====

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public Double getQuantity() {
        return quantity;
    }

    public UnitCode getUnitCode() {
        return unitCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String name;
        private String sku;
        private String description;
        private String brand;
        private Double quantity;
        private UnitCode unitCode;
        private BigDecimal unitPrice;
        private List<Discount> discounts = new ArrayList<>();
        private List<Charge> charges = new ArrayList<>();
        private List<Tax> taxes = new ArrayList<>();
        private BigDecimal subtotal;
        private BigDecimal total;

        private Builder() {}

        public Builder from(Product other) {
            this.name = other.name;
            this.sku = other.sku;
            this.description = other.description;
            this.brand = other.brand;
            this.quantity = other.quantity;
            this.unitCode = other.unitCode;
            this.unitPrice = other.unitPrice;
            this.discounts = new ArrayList<>(other.discounts);
            this.charges = new ArrayList<>(other.charges);
            this.taxes = new ArrayList<>(other.taxes);
            this.subtotal = other.subtotal;
            this.total = other.total;
            return this;
        }

        @JsonSetter(value = "name", nulls = Nulls.SKIP)
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @JsonSetter(value = "sku", nulls = Nulls.SKIP)
        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        @JsonSetter(value = "description", nulls = Nulls.SKIP)
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @JsonSetter(value = "brand", nulls = Nulls.SKIP)
        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        @JsonSetter(value = "quantity", nulls = Nulls.SKIP)
        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the unit code using the UnitCode enum (recommended).
         * @param unitCode The unit code enum value
         * @return This builder
         */
        public Builder unitCode(UnitCode unitCode) {
            this.unitCode = unitCode;
            return this;
        }

        /**
         * Sets the unit code using a string code.
         * The string will be parsed to a UnitCode enum.
         * Used for JSON deserialization.
         * 
         * @param unitCode The UN/ECE unit code string (e.g., "C62", "KGM")
         * @return This builder
         * @throws IllegalArgumentException if the code is not recognized
         */
        @JsonSetter(value = "unitCode", nulls = Nulls.SKIP)
        public Builder unitCode(String unitCode) {
            this.unitCode = unitCode != null ? UnitCode.fromCode(unitCode) : null;
            return this;
        }

        @JsonSetter(value = "unitPrice", nulls = Nulls.SKIP)
        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder unitPrice(String unitPrice) {
            this.unitPrice = new BigDecimal(unitPrice);
            return this;
        }

        @JsonSetter(value = "discounts", nulls = Nulls.SKIP)
        public Builder discounts(List<Discount> discounts) {
            this.discounts = discounts != null ? discounts : new ArrayList<>();
            return this;
        }

        public Builder addDiscount(Discount discount) {
            this.discounts.add(discount);
            return this;
        }

        public Builder addDiscount(BigDecimal amount, String label) {
            this.discounts.add(
                    Discount.builder()
                            .amount(amount)
                            .label(label)
                            .build()
            );
            return this;
        }

        public Builder addDiscount(String amount, String label) {
            return addDiscount(new BigDecimal(amount), label);
        }

        public Builder addDiscount(BigDecimal amount, Double percentage, String label) {
            this.discounts.add(
                    Discount.builder()
                            .amount(amount)
                            .percentage(percentage)
                            .label(label)
                            .build()
            );
            return this;
        }

        public Builder addDiscount(String amount, Double percentage, String label) {
            return addDiscount(new BigDecimal(amount), percentage, label);
        }

        @JsonSetter(value = "charges", nulls = Nulls.SKIP)
        public Builder charges(List<Charge> charges) {
            this.charges = charges != null ? charges : new ArrayList<>();
            return this;
        }

        public Builder addCharge(Charge charge) {
            this.charges.add(charge);
            return this;
        }

        public Builder addCharge(BigDecimal amount, String label) {
            this.charges.add(
                    Charge.builder()
                            .amount(amount)
                            .label(label)
                            .build()
            );
            return this;
        }

        public Builder addCharge(String amount, String label) {
            return addCharge(new BigDecimal(amount), label);
        }

        public Builder addCharge(BigDecimal amount, Double percentage, String label) {
            this.charges.add(
                    Charge.builder()
                            .amount(amount)
                            .percentage(percentage)
                            .label(label)
                            .build()
            );
            return this;
        }

        public Builder addCharge(String amount, Double percentage, String label) {
            return addCharge(new BigDecimal(amount), percentage, label);
        }

        @JsonSetter(value = "taxes", nulls = Nulls.SKIP)
        public Builder taxes(List<Tax> taxes) {
            this.taxes = taxes != null ? taxes : new ArrayList<>();
            return this;
        }

        public Builder addTax(Tax tax) {
            this.taxes.add(tax);
            return this;
        }

        public Builder addTax(Double rate, String type) {
            this.taxes.add(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .build());
            return this;
        }

        public Builder addTax(Double rate, String type, BigDecimal amount) {
            this.taxes.add(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .amount(amount)
                    .build());
            return this;
        }

        public Builder addTax(Double rate, String type, String amount) {
            this.taxes.add(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .amount(amount)
                    .build());
            return this;
        }

        @JsonSetter(value = "subtotal", nulls = Nulls.SKIP)
        public Builder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Builder subtotal(String subtotal) {
            this.subtotal = new BigDecimal(subtotal);
            return this;
        }

        @JsonSetter(value = "total", nulls = Nulls.SKIP)
        public Builder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Builder total(String total) {
            this.total = new BigDecimal(total);
            return this;
        }

        public Product build() {
            return new Product(
                    name,
                    sku,
                    description,
                    brand,
                    quantity,
                    unitCode,
                    unitPrice,
                    discounts,
                    charges,
                    taxes,
                    subtotal,
                    total
            );
        }
    }

    // ===== SIMPLE VALIDATION (OPTIONAL) =====

    /**
     * @return true if required fields are present and non-negative.
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Very lightweight validation meant as a helper for integrators.
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("name is required");
        }
        if (quantity == null) {
            errors.add("quantity is required");
        }
        if (unitCode == null) {
            errors.add("unitCode is required (e.g., UnitCode.ONE, UnitCode.KILOGRAM)");
        }
        if (unitPrice == null) {
            errors.add("unitPrice is required");
        }
        if (subtotal == null) {
            errors.add("subtotal is required");
        }
        if (total == null) {
            errors.add("total is required");
        }

        if (quantity != null && quantity < 0) {
            errors.add("quantity cannot be negative");
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("unitPrice cannot be negative");
        }
        if (subtotal != null && subtotal.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("subtotal cannot be negative");
        }
        if (total != null && total.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("total cannot be negative");
        }

        return errors;
    }

    // ===== EQUALS, HASHCODE, TOSTRING =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name)
                && Objects.equals(sku, product.sku)
                && Objects.equals(description, product.description)
                && Objects.equals(brand, product.brand)
                && Objects.equals(quantity, product.quantity)
                && Objects.equals(unitCode, product.unitCode)
                && Objects.equals(unitPrice, product.unitPrice)
                && Objects.equals(discounts, product.discounts)
                && Objects.equals(charges, product.charges)
                && Objects.equals(taxes, product.taxes)
                && Objects.equals(subtotal, product.subtotal)
                && Objects.equals(total, product.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                sku,
                description,
                brand,
                quantity,
                unitCode,
                unitPrice,
                discounts,
                charges,
                taxes,
                subtotal,
                total
        );
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", discounts=" + (discounts != null ? discounts.size() : 0) +
                ", charges=" + (charges != null ? charges.size() : 0) +
                ", taxes=" + (taxes != null ? taxes.size() : 0) +
                ", subtotal=" + subtotal +
                ", total=" + total +
                '}';
    }
}