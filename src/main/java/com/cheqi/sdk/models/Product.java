package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
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
 * Cheqi backend handles UBL mapping and validation.
 *
 * <h3>Typical usage:</h3>
 * <pre>
 * // Simple product (1 laptop)
 * Product laptop = Product.builder()
 *     .name("Laptop 13\"")
 *     .identifier("LAP-001")
 *     .quantity(1.0)
 *     .baseQuantity(1.0)        // Always required (1.0 for simple items)
 *     .unitCode(UnitCode.ONE)
 *     .unitPrice("1000.00")
 *     .subtotal("1000.00")
 *     .total("1210.00")
 *     .addTax(21.0, "VAT", "1000.00", "210.00")
 *     .build();
 *
 * // Pre-packaged product (10 packages × 500g cheese)
 * Product cheese = Product.builder()
 *     .name("Gouda Cheese")
 *     .identifier("CHEESE-500G")
 *     .quantity(10.0)           // 10 packages
 *     .baseQuantity(500.0)      // 500g per package (total: 5000g)
 *     .unitCode(UnitCode.GRAM)
 *     .unitPrice("4.99")        // Price per package
 *     .subtotal("49.90")
 *     .total("60.38")
 *     .addTax(21.0, "VAT", "49.90", "10.48")
 *     .build();
 *
 * // Weight-based product (2.5kg apples)
 * Product apples = Product.builder()
 *     .name("Organic Apples")
 *     .identifier("APPLE-ORG")
 *     .quantity(2.5)
 *     .baseQuantity(1.0)
 *     .unitCode(UnitCode.KILOGRAM)
 *     .unitPrice("3.99")        // Price per kg
 *     .subtotal("9.98")
 *     .total("12.08")
 *     .addTax(21.0, "VAT", "9.98", "2.10")
 *     .build();
 *
 * // Time-based product (monthly subscription) - Easy with LocalDate!
 * Product subscription = Product.builder()
 *     .name("Premium Subscription")
 *     .identifier("SUB-PREMIUM-M")
 *     .quantity(1.0)
 *     .baseQuantity(1.0)
 *     .unitCode(UnitCode.MONTH)
 *     .unitPrice("29.99")
 *     .subtotal("29.99")
 *     .total("36.29")
 *     .addTax(21.0, "VAT", "29.99", "6.30")
 *     .period(Period.builder()
 *         .startDate(LocalDate.of(2024, 1, 1))      // Easy!
 *         .endDate(LocalDate.of(2024, 1, 31))       // Easy!
 *         .description("January 2024")
 *         .build())
 *     .build();
 *
 * // Hourly rental with precise times - Easy with LocalDateTime!
 * Product carRental = Product.builder()
 *     .name("Tesla Model 3 Rental")
 *     .identifier("RENTAL-TESLA-M3")
 *     .quantity(3.0)
 *     .baseQuantity(1.0)
 *     .unitCode(UnitCode.HOUR)
 *     .unitPrice("25.00")
 *     .subtotal("75.00")
 *     .total("90.75")
 *     .addTax(21.0, "VAT", "75.00", "15.75")
 *     .period(Period.builder()
 *         .startDate(LocalDateTime.of(2024, 12, 1, 14, 0))  // 2:00 PM - Easy!
 *         .endDate(LocalDateTime.of(2024, 12, 1, 17, 0))    // 5:00 PM - Easy!
 *         .description("3-hour rental")
 *         .build())
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Product.Builder.class)
public final class Product {

    // ===== BASIC INFO =====

    /**
     * Human-readable product name shown on the receipt.
     */
    @JsonProperty("name")
    private final String name;

    /**
     * Article identifier, for example article number (merchant-defined).
     */
    @JsonProperty("identifier")
    private final String identifier;

    /**
     * Optional longer description.
     */
    @JsonProperty("description")
    private final String description;

    /**
     * Optional brandName name.
     */
    @JsonProperty("brandName")
    private final String brandName;

    // ===== QUANTITY & PRICE =====

    /**
     * Quantity of this item (e.g., 1, 2.5, etc.).
     */
    @JsonProperty("quantity")
    private final Double quantity;

    /**
     * Base quantity per unit (mandatory).
     * 
     * <p>For pre-packaged products sold in fixed quantities:</p>
     * <ul>
     *   <li>10 packages × 500g cheese → quantity=10, baseQuantity=500, unitCode=GRAM</li>
     *   <li>6 bottles × 0.33L water → quantity=6, baseQuantity=0.33, unitCode=LITER</li>
     *   <li>3 boxes × 12 eggs → quantity=3, baseQuantity=12, unitCode=ONE</li>
     * </ul>
     * 
     * <p>For simple items, set baseQuantity to 1.0:</p>
     * <ul>
     *   <li>2.5 kg apples → quantity=2.5, baseQuantity=1.0, unitCode=KILOGRAM</li>
     *   <li>1 laptop → quantity=1, baseQuantity=1.0, unitCode=ONE</li>
     * </ul>
     * 
     * <p><strong>Total quantity = quantity × baseQuantity</strong></p>
     */
    @JsonProperty("baseQuantity")
    private final Double baseQuantity;

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

    // ===== OPTIONAL FIELDS =====

    /**
     * Optional period for time-based products.
     * 
     * <p>Use this for products that represent services over a time range:</p>
     * <ul>
     *   <li>Subscriptions (e.g., monthly SaaS from Jan 1 - Jan 31)</li>
     *   <li>Rentals (e.g., car rental from Dec 1 - Dec 7)</li>
     *   <li>Utilities (e.g., electricity usage from Nov 1 - Nov 30)</li>
     *   <li>Service contracts (e.g., maintenance from Q1 2024)</li>
     * </ul>
     * 
     * <p>Leave empty for one-time purchases or instant transactions.</p>
     * 
     * @see Period
     */
    @JsonProperty("period")
    private final Period period;

    // ===== CONSTRUCTOR =====

    private Product(
            String name,
            String identifier,
            String description,
            String brandName,
            Double quantity,
            Double baseQuantity,
            UnitCode unitCode,
            BigDecimal unitPrice,
            List<Discount> discounts,
            List<Charge> charges,
            List<Tax> taxes,
            BigDecimal subtotal,
            BigDecimal total,
            Period period) {

        this.name = name;
        this.identifier = identifier;
        this.description = description;
        this.brandName = brandName;
        this.quantity = quantity;
        this.baseQuantity = baseQuantity;
        this.unitCode = unitCode;
        this.unitPrice = unitPrice;
        // Store null for empty lists so NON_EMPTY excludes them from JSON
        this.discounts = discounts != null && !discounts.isEmpty() ? List.copyOf(discounts) : null;
        this.charges   = charges   != null && !charges.isEmpty()   ? List.copyOf(charges)   : null;
        this.taxes     = taxes     != null && !taxes.isEmpty()     ? List.copyOf(taxes)     : null;
        this.subtotal = subtotal;
        this.total = total;
        this.period = period;
    }

    // ===== GETTERS =====

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public String getBrandName() {
        return brandName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getBaseQuantity() {
        return baseQuantity;
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

    /**
     * @return The period if this product represents a time-based service, or null
     */
    public Period getPeriod() {
        return period;
    }

    // ===== BUILDER =====

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String name;
        private String identifier;
        private String description;
        private String brandName;
        private Double quantity;
        private Double baseQuantity;
        private UnitCode unitCode;
        private BigDecimal unitPrice;
        private List<Discount> discounts = new ArrayList<>();
        private List<Charge> charges = new ArrayList<>();
        private List<Tax> taxes = new ArrayList<>();
        private BigDecimal subtotal;
        private BigDecimal total;
        private Period period;

        private Builder() {}

        public Builder from(Product other) {
            this.name = other.name;
            this.identifier = other.identifier;
            this.description = other.description;
            this.brandName = other.brandName;
            this.quantity = other.quantity;
            this.baseQuantity = other.baseQuantity;
            this.unitCode = other.unitCode;
            this.unitPrice = other.unitPrice;
            this.discounts = other.discounts != null ? new ArrayList<>(other.discounts) : new ArrayList<>();
            this.charges = other.charges != null ? new ArrayList<>(other.charges) : new ArrayList<>();
            this.taxes = other.taxes != null ? new ArrayList<>(other.taxes) : new ArrayList<>();
            this.subtotal = other.subtotal;
            this.total = other.total;
            this.period = other.getPeriod();
            return this;
        }

        @JsonSetter(value = "name", nulls = Nulls.SKIP)
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @JsonSetter(value = "identifier", nulls = Nulls.SKIP)
        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        @JsonSetter(value = "description", nulls = Nulls.SKIP)
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @JsonSetter(value = "brandName", nulls = Nulls.SKIP)
        public Builder brandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        @JsonSetter(value = "quantity", nulls = Nulls.SKIP)
        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the base quantity per unit (mandatory).
         * Use 1.0 for simple items, or the package size for pre-packaged items.
         * 
         * @param baseQuantity Base quantity (e.g., 1.0 for simple items, 500 for 500g packages)
         * @return This builder
         */
        @JsonSetter(value = "baseQuantity", nulls = Nulls.SKIP)
        public Builder baseQuantity(Double baseQuantity) {
            this.baseQuantity = baseQuantity;
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

        public Builder addTax(Double rate, String type, BigDecimal taxableAmount, BigDecimal amount) {
            this.taxes.add(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .taxableAmount(taxableAmount)
                    .amount(amount)
                    .build());
            return this;
        }

        public Builder addTax(Double rate, String type, String taxableAmount, String amount) {
            this.taxes.add(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .taxableAmount(taxableAmount)
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

        /**
         * Sets the period for time-based products (subscriptions, rentals, etc.).
         * 
         * @param period The period this product covers
         * @return This builder
         */
        @JsonSetter(value = "period", nulls = Nulls.SKIP)
        public Builder period(Period period) {
            this.period = period;
            return this;
        }

        public Product build() {
            return new Product(
                    name,
                    identifier,
                    description,
                    brandName,
                    quantity,
                    baseQuantity,
                    unitCode,
                    unitPrice,
                    discounts,
                    charges,
                    taxes,
                    subtotal,
                    total,
                    period
            );
        }
    }

    // ===== EQUALS, HASHCODE, TOSTRING =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name)
                && Objects.equals(identifier, product.identifier)
                && Objects.equals(description, product.description)
                && Objects.equals(brandName, product.brandName)
                && Objects.equals(quantity, product.quantity)
                && Objects.equals(baseQuantity, product.baseQuantity)
                && Objects.equals(unitCode, product.unitCode)
                && Objects.equals(unitPrice, product.unitPrice)
                && Objects.equals(discounts, product.discounts)
                && Objects.equals(charges, product.charges)
                && Objects.equals(taxes, product.taxes)
                && Objects.equals(subtotal, product.subtotal)
                && Objects.equals(total, product.total)
                && Objects.equals(period, product.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                identifier,
                description,
                brandName,
                quantity,
                baseQuantity,
                unitCode,
                unitPrice,
                discounts,
                charges,
                taxes,
                subtotal,
                total,
                period
        );
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
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