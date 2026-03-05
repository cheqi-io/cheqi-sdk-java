package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.UUID;

/**
 * Request DTO for generating receipt templates.
 * 
 * <p>This class provides a simplified, POS-friendly API for creating digital receipts.
 * The POS system provides all calculated values - no calculations are performed by the SDK.</p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
 *     // Basic information
 *     .documentNumber("INV-2024-001")
 *     .issueDate(Instant.now())
 *     .currency("EUR")
 *     
 *     // Totals (POS-calculated)
 *     .receiptSubtotal(new BigDecimal("250.00"))
 *     .totalBeforeTax(new BigDecimal("245.00"))  // After receipt-level discount
 *     .totalTaxAmount(new BigDecimal("51.45"))   // Total tax
 *     .totalAmount(new BigDecimal("296.45"))     // Final amount
 *     
 *     // Simple product addition (POS provides all amounts)
 *     .addProduct("Nike", "AirMax", "125.00", "250.00", 21.0, "52.50", "302.50", 2.0)
 *     
 *     // Or use full Product builder for complex cases
 *     .addProduct(Product.builder()
 *         .name("Laptop")
 *         .quantity(1.0)
 *         .unitCode(UnitCode.ONE)
 *         .unitPrice("100.00")
 *         .subtotal("100.00")
 *         .total("121.00")
 *         .addTax(21.0, "VAT", "100.00", "21.00")
 *         .build())
 *     
 *     // Receipt-level adjustments
 *     .addDiscount("5.00", "Loyalty discount")
 *     .addCharge("3.00", "Service fee")
 *     
 *     // Tax breakdown
 *     .addTax(Tax.builder()
 *         .rate(21.0)
 *         .type("VAT")
 *         .amount("19.95")
 *         .label("VAT 21%")
 *         .build())
 *     
 *     // Optional fields
 *     .note("Thank you for your purchase!")
 *     .build();
 * </pre>
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>documentNumber</strong>: Unique receipt/invoice identifier</li>
 *   <li><strong>issueDate</strong>: Receipt issue date</li>
 *   <li><strong>currency</strong>: ISO 4217 currency code (e.g., "EUR", "USD")</li>
 *   <li><strong>receiptSubtotal</strong>: Sum of all product line totals</li>
 *   <li><strong>totalBeforeTax</strong>: Total excluding taxes (after receipt-level adjustments)</li>
 *   <li><strong>totalTaxAmount</strong>: Total tax amount (POS-calculated)</li>
 *   <li><strong>totalAmount</strong>: Final amount charged to customer</li>
 *   <li><strong>products</strong>: List of products (minimum 1 required)</li>
 *   <li><strong>taxes</strong>: Tax breakdown by category/rate</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>identifiers</strong>: Additional identifiers (order number, PO number, etc.)</li>
 *   <li><strong>transactionDate</strong>: When transaction was initiated</li>
 *   <li><strong>purchaseDate</strong>: When purchase was made</li>
 *   <li><strong>discounts</strong>: Receipt-level discounts</li>
 *   <li><strong>charges</strong>: Receipt-level charges (delivery, service fees, etc.)</li>
 *   <li><strong>period</strong>: Billing/service period (for subscriptions, rentals)</li>
 *   <li><strong>note</strong>: Additional seller notes</li>
 * </ul>
 *
 * <h3>Key Differences from receiptSubtotal and totalBeforeTax:</h3>
 * <ul>
 *   <li><strong>receiptSubtotal</strong>: Sum of all product line totals (before receipt-level adjustments)</li>
 *   <li><strong>totalBeforeTax</strong>: receiptSubtotal ± receipt-level discounts/charges</li>
 *   <li>Example: receiptSubtotal = $100, discount = $5, charge = $3 → totalBeforeTax = $98</li>
 * </ul>
 *
 * <h3>Validation Rules:</h3>
 * <ul>
 *   <li>All mandatory fields must be present</li>
 *   <li>At least one product is required</li>
 *   <li>Currency must be a valid ISO 4217 code</li>
 *   <li>All monetary amounts must be non-negative</li>
 *   <li>Products must have valid unit codes (see {@link UnitCode})</li>
 * </ul>
 *
 * @see Product
 * @see Tax
 * @see Discount
 * @see Charge
 * @see UnitCode
 * @see Period
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ReceiptTemplateRequest.Builder.class)
public final class ReceiptTemplateRequest {
    /**
     * The UUID of the child company (franchisee location or branch store) issuing this receipt.
     * Required for franchise organizations managing multiple locations.
     * If not provided, the receipt will be issued under the parent company.
     */
    @JsonProperty("childCompanyId")
    private final Optional<UUID> childCompanyId;

    /**
     * A unique document identifier provided by the seller (for example invoice number).
     */
    @JsonProperty("documentNumber")
    private final String documentNumber;

    /**
     * A list of identifiers for the receipt. For example order number, purchase order number, etc.
     */
    @JsonProperty("identifiers")
    private final List<Identifier> identifiers;

    /**
     * The date when the purchase receipt was issued.
     */
    @JsonProperty("issueDate")
    private final Instant issueDate;

    /**
     * The currency in which the purchase receipt is issued.
     * Must be a valid ISO 4217 currency code (e.g., "EUR", "USD").
     */
    @JsonProperty("currency")
    private final String currency;

    /**
     * The subtotal of the invoice, calculated as the sum of all line items before any overall
     * invoice-level adjustments or taxes.
     * This amount represents the total value of goods or services listed on the invoice.
     */
    @JsonProperty("receiptSubtotal")
    private final BigDecimal receiptSubtotal;

    /**
     * The total amount of the invoice excluding taxes, but including any overall discounts or charges.
     * This amount may differ from the receiptSubtotal if there are invoice-level adjustments.
     */
    @JsonProperty("totalBeforeTax")
    private final BigDecimal totalBeforeTax;

    /**
     * The total tax amount for the receipt.
     * This should equal the sum of all tax amounts in the taxes list.
     */
    @JsonProperty("totalTaxAmount")
    private final BigDecimal totalTaxAmount;

    /**
     * The total amount charged to the customer.
     * Should equal totalBeforeTax + totalTaxAmount.
     */
    @JsonProperty("totalAmount")
    private final BigDecimal totalAmount;

    /**
     * The list of products included in the purchase receipt.
     * Must contain at least one product.
     */
    @JsonProperty("products")
    private final List<Product> products;

    /**
     * Overall discounts applied at the receipt level (not product-specific).
     * Examples: "Loyalty discount", "Coupon code", "Volume discount"
     */
    @JsonProperty("discounts")
    private final List<Discount> discounts;

    /**
     * Overall charges applied at the receipt level (not product-specific).
     * Examples: "Delivery fee", "Service charge", "Payment processing fee"
     */
    @JsonProperty("charges")
    private final List<Charge> charges;

    /**
     * Tax breakdown for the receipt.
     * Each Tax entry represents a tax category applied to the receipt (e.g., VAT 21%, VAT 9%).
     * If no taxes are applied, then you need a tax category with 0% rate.
     * The total tax amount is the sum of all tax amounts in this list.
     */
    @JsonProperty("taxes")
    private final List<Tax> taxes;

    // ===== OPTIONAL FIELDS =====
    /**
     * The date when the purchase transaction was initiated.
     * Only needed when different from the issue date.
     */
    @JsonProperty("transactionDate")
    private final Optional<Instant> transactionDate;

    /**
     * The date when the purchase was made.
     * Only needed when different from the issue date.
     */
    @JsonProperty("purchaseDate")
    private final Optional<Instant> purchaseDate;

    /**
     * The billing or service period covered by this receipt.
     * Use this when the purchase covers a specific time range rather than a single point in time.
     * 
     * <p>Common use cases:</p>
     * <ul>
     *   <li>Subscription services (e.g., monthly SaaS subscription from Jan 1 - Jan 31)</li>
     *   <li>Rental periods (e.g., car rental from Dec 1 - Dec 7)</li>
     *   <li>Utility bills (e.g., electricity usage from Nov 1 - Nov 30)</li>
     *   <li>Service contracts (e.g., maintenance contract from Q1 2024)</li>
     * </ul>
     * 
     * <p>Leave empty for one-time purchases or instant transactions.</p>
     */
    @JsonProperty("period")
    private final Optional<Period> period;

    /**
     * A note that the seller wants to include on the purchase receipt.
     */
    @JsonProperty("note")
    private final Optional<String> note;

    private final Map<String, Object> additionalProperties;

    // VAT metadata — not serialized in the inner request JSON.
    // The API client transfers these to ReceiptTemplateGenerationRequest.
    private final String buyerCountryCode;
    private final RecipientEntityType recipientEntityType;
    private final Boolean taxesApplied;

    // ===== CONSTRUCTOR =====

    private ReceiptTemplateRequest(
            Optional<UUID> childCompanyId,
            String documentNumber,
            List<Identifier> identifiers,
            Instant issueDate,
            String currency,
            BigDecimal receiptSubtotal,
            BigDecimal totalBeforeTax,
            BigDecimal totalTaxAmount,
            BigDecimal totalAmount,
            List<Product> products,
            List<Discount> discounts,
            List<Charge> charges,
            List<Tax> taxes,
            Optional<Instant> transactionDate,
            Optional<Instant> purchaseDate,
            Optional<Period> period,
            Optional<String> note,
            Map<String, Object> additionalProperties,
            String buyerCountryCode,
            RecipientEntityType recipientEntityType,
            Boolean taxesApplied) {
        this.childCompanyId = childCompanyId;
        this.documentNumber = documentNumber;
        this.identifiers = identifiers != null ? List.copyOf(identifiers) : List.of();
        this.issueDate = issueDate;
        this.currency = currency;
        this.receiptSubtotal = receiptSubtotal;
        this.totalBeforeTax = totalBeforeTax;
        this.totalTaxAmount = totalTaxAmount;
        this.totalAmount = totalAmount;
        this.products = products;
        this.discounts = discounts != null ? List.copyOf(discounts) : List.of();
        this.charges = charges != null ? List.copyOf(charges) : List.of();
        this.taxes = taxes != null ? List.copyOf(taxes) : List.of();
        this.transactionDate = transactionDate;
        this.purchaseDate = purchaseDate;
        this.period = period;
        this.note = note;
        this.additionalProperties = additionalProperties;
        this.buyerCountryCode = buyerCountryCode;
        this.recipientEntityType = recipientEntityType;
        this.taxesApplied = taxesApplied;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    /**
     * @return The child company ID if provided
     */
    @JsonIgnore
    public Optional<UUID> getChildCompanyId() {
        return childCompanyId != null ? childCompanyId : Optional.empty();
    }

    @JsonIgnore
    public String getDocumentNumber() {
        return documentNumber;
    }

    public List<Identifier> getIdentifiers(){
        return identifiers;
    }

    @JsonIgnore
    public Instant getIssueDate() {
        return issueDate;
    }

    @JsonIgnore
    public String getcurrency() {
        return currency;
    }

    @JsonIgnore
    public BigDecimal getreceiptSubtotal() {
        return receiptSubtotal;
    }

    @JsonIgnore
    public BigDecimal getTotalBeforeTax() {
        return totalBeforeTax;
    }

    @JsonIgnore
    public BigDecimal getTotalTaxAmount() {
        return totalTaxAmount;
    }

    @JsonIgnore
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @JsonIgnore
    public List<Product> getProducts() {
        return products;
    }

    @JsonIgnore
    public List<Discount> getDiscounts() {
        return discounts;
    }

    @JsonIgnore
    public List<Charge> getCharges() {
        return charges;
    }

    @JsonIgnore
    public List<Tax> getTaxes() {
        return taxes;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The transaction date if provided
     */
    @JsonIgnore
    public Optional<Instant> getTransactionDate() {
        return transactionDate != null ? transactionDate : Optional.empty();
    }

    /**
     * @return The purchase date if provided
     */
    @JsonIgnore
    public Optional<Instant> getPurchaseDate() {
        return purchaseDate != null ? purchaseDate : Optional.empty();
    }

    /**
     * @return The billing/service period if provided
     */
    @JsonIgnore
    public Optional<Period> getPeriod() {
        return period != null ? period : Optional.empty();
    }

    /**
     * @return The seller note if provided
     */
    @JsonIgnore
    public Optional<String> getNote() {
        return note != null ? note : Optional.empty();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    // ===== VAT METADATA ACCESSORS (not serialized) =====

    @JsonIgnore
    public String getBuyerCountryCode() {
        return buyerCountryCode;
    }

    @JsonIgnore
    public RecipientEntityType getRecipientEntityType() {
        return recipientEntityType;
    }

    @JsonIgnore
    public Boolean getTaxesApplied() {
        return taxesApplied;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ReceiptTemplateRequest && equalTo((ReceiptTemplateRequest) other);
    }

    private boolean equalTo(ReceiptTemplateRequest other) {
        return Objects.equals(this.childCompanyId, other.childCompanyId)
                && Objects.equals(this.documentNumber, other.documentNumber)
                && Objects.equals(this.identifiers, other.identifiers)
                && Objects.equals(this.issueDate, other.issueDate)
                && Objects.equals(this.currency, other.currency)
                && Objects.equals(this.receiptSubtotal, other.receiptSubtotal)
                && Objects.equals(this.totalBeforeTax, other.totalBeforeTax)
                && Objects.equals(this.totalTaxAmount, other.totalTaxAmount)
                && Objects.equals(this.totalAmount, other.totalAmount)
                && Objects.equals(this.products, other.products)
                && Objects.equals(this.discounts, other.discounts)
                && Objects.equals(this.charges, other.charges)
                && Objects.equals(this.taxes, other.taxes)
                && Objects.equals(this.transactionDate, other.transactionDate)
                && Objects.equals(this.purchaseDate, other.purchaseDate)
                && Objects.equals(this.period, other.period)
                && Objects.equals(this.note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.childCompanyId, this.documentNumber, this.identifiers, this.issueDate, this.currency,
                this.receiptSubtotal, this.totalBeforeTax, this.totalTaxAmount, this.totalAmount,
                this.products, this.discounts, this.charges, this.taxes, this.transactionDate,
                this.purchaseDate, this.period, this.note);
    }

    @Override
    public String toString() {
        return "ReceiptTemplateRequestDto{" +
                "childCompanyId='" + childCompanyId + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", identifiers=" + identifiers +
                ", issueDate=" + issueDate +
                ", currency='" + currency + '\'' +
                ", receiptSubtotal=" + receiptSubtotal +
                ", totalBeforeTax=" + totalBeforeTax +
                ", totalTaxAmount=" + totalTaxAmount +
                ", totalAmount=" + totalAmount +
                ", products=" + products +
                ", discounts=" + discounts +
                ", charges=" + charges +
                ", taxes=" + taxes +
                ", transactionDate=" + transactionDate +
                ", purchaseDate=" + purchaseDate +
                ", period=" + period +
                ", note=" + note +
                '}';
    }

    public static ReceiptTemplateRequest.Builder builder() {
        return new ReceiptTemplateRequest.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Optional<UUID> childCompanyId = Optional.empty();
        private String documentNumber;
        private List<Identifier> identifiers;
        private Instant issueDate;
        private String currency;
        private BigDecimal receiptSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalTaxAmount;
        private BigDecimal totalAmount;
        private List<Product> products;
        private List<Discount> discounts;
        private List<Charge> charges;
        private List<Tax> taxes;
        private Optional<Instant> transactionDate = Optional.empty();
        private Optional<Instant> purchaseDate = Optional.empty();
        private Optional<Period> period = Optional.empty();
        private Optional<String> note = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();
        private String buyerCountryCode;
        private RecipientEntityType recipientEntityType;
        private Boolean taxesApplied;

        private Builder() {}

        public ReceiptTemplateRequest.Builder from(ReceiptTemplateRequest other) {
            childCompanyId(other.getChildCompanyId().orElse(null));
            documentNumber(other.getDocumentNumber());
            identifiers(other.getIdentifiers());
            issueDate(other.getIssueDate());
            currency(other.getcurrency());
            receiptSubtotal(other.getreceiptSubtotal());
            totalBeforeTax(other.getTotalBeforeTax());
            totalTaxAmount(other.getTotalTaxAmount());
            totalAmount(other.getTotalAmount());
            products(other.getProducts());
            discounts(other.getDiscounts());
            charges(other.getCharges());
            taxes(other.getTaxes());
            transactionDate(other.getTransactionDate().orElse(null));
            purchaseDate(other.getPurchaseDate().orElse(null));
            period(other.getPeriod().orElse(null));
            note(other.getNote().orElse(null));
            return this;
        }

        @JsonSetter(value = "documentNumber", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        @JsonSetter(value = "identifiers", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder identifiers(List<Identifier> identifiers) {
            this.identifiers = identifiers;
            return this;
        }

        @JsonSetter(value = "issueDate", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        /**
         * Sets the currency code (ISO 4217).
         * 
         * @param currency ISO 4217 currency code (e.g., "EUR", "USD", "GBP")
         * @return This builder
         * @throws IllegalArgumentException if the currency code is invalid
         */
        @JsonSetter(value = "currency", nulls = Nulls.SKIP)
        public Builder currency(String currency) {
            if (currency != null) {
                try {
                    // Validate using Java's built-in Currency class
                    java.util.Currency.getInstance(currency);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                        "Invalid currency code: '" + currency + "'. Must be a valid ISO 4217 code (e.g., EUR, USD, GBP)", e);
                }
            }
            this.currency = currency;
            return this;
        }

        @JsonSetter(value = "receiptSubtotal", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder receiptSubtotal(BigDecimal receiptSubtotal) {
            this.receiptSubtotal = receiptSubtotal;
            return this;
        }

        @JsonSetter(value = "totalBeforeTax", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder totalBeforeTax(BigDecimal totalBeforeTax) {
            this.totalBeforeTax = totalBeforeTax;
            return this;
        }

        public ReceiptTemplateRequest.Builder totalBeforeTax(String totalBeforeTax) {
            this.totalBeforeTax = new BigDecimal(totalBeforeTax);
            return this;
        }

        @JsonSetter(value = "totalTaxAmount", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder totalTaxAmount(BigDecimal totalTaxAmount) {
            this.totalTaxAmount = totalTaxAmount;
            return this;
        }

        public ReceiptTemplateRequest.Builder totalTaxAmount(String totalTaxAmount) {
            this.totalTaxAmount = new BigDecimal(totalTaxAmount);
            return this;
        }

        @JsonSetter(value = "totalAmount", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public ReceiptTemplateRequest.Builder totalAmount(String totalAmount) {
            this.totalAmount = new BigDecimal(totalAmount);
            return this;
        }

        @JsonSetter(value = "products", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        /**
         * Convenience method to add a single product to the receipt.
         */
        public ReceiptTemplateRequest.Builder addProduct(Product product) {
            if (this.products == null) {
                this.products = new ArrayList<>();
            }
            this.products.add(product);
            return this;
        }

        /**
         * Convenience method to add a simple product with basic details.
         * All amounts must be pre-calculated by the POS system.
         *
         * @param identifier Product identifier (e.g., SKU, EAN)
         * @param brand Product brand/manufacturer
         * @param name Product name/model
         * @param unitPrice Price per unit
         * @param subtotal Subtotal (unitPrice × quantity, pre-calculated)
         * @param taxRate Tax rate as percentage (e.g., 21.0 for 21%)
         * @param taxableAmount The taxable amount this tax rate applies to (pre-calculated)
         * @param taxAmount Tax amount (pre-calculated)
         * @param total Total amount including tax (pre-calculated)
         * @param quantity Number of units
         * @return This builder
         */
        public ReceiptTemplateRequest.Builder addProduct(String identifier, String brand, String name, String unitPrice, String subtotal, Double taxRate, String taxableAmount, String taxAmount, String total, Double quantity) {
            return addProduct(Product.builder()
                    .brandName(brand)
                    .name(name)
                    .identifier(identifier)
                    .quantity(quantity)
                    .baseQuantity(1.0)
                    .unitCode(UnitCode.ONE)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .total(total)
                    .addTax(taxRate, "VAT", taxableAmount, taxAmount)
                    .build());
        }

        /**
         * Convenience method with BigDecimal amounts.
         */
        public ReceiptTemplateRequest.Builder addProduct(String identifier, String brand, String name, BigDecimal unitPrice, BigDecimal subtotal, Double taxRate, BigDecimal taxableAmount, BigDecimal taxAmount, BigDecimal total, Double quantity) {
            return addProduct(identifier, brand, name, unitPrice.toString(), subtotal.toString(), taxRate, taxableAmount.toString(), taxAmount.toString(), total.toString(), quantity);
        }

        @JsonSetter(value = "discounts", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder discounts(List<Discount> discounts) {
            this.discounts = discounts;
            return this;
        }

        /**
         * Convenience method to add a single discount to the receipt.
         */
        public ReceiptTemplateRequest.Builder addDiscount(Discount discount) {
            if (this.discounts == null) {
                this.discounts = new ArrayList<>();
            }
            this.discounts.add(discount);
            return this;
        }

        /**
         * Convenience method to add a discount with amount and label.
         */
        public ReceiptTemplateRequest.Builder addDiscount(BigDecimal amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        /**
         * Convenience method to add a discount with amount (String) and label.
         */
        public ReceiptTemplateRequest.Builder addDiscount(String amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        /**
         * Convenience method to add a discount with amount, percentage, and label.
         */
        public ReceiptTemplateRequest.Builder addDiscount(BigDecimal amount, Double percentage, String label) {
            return addDiscount(Discount.of(amount, percentage, label));
        }

        /**
         * Convenience method to add a discount with amount (String), percentage, and label.
         */
        public ReceiptTemplateRequest.Builder addDiscount(String amount, Double percentage, String label) {
            return addDiscount(Discount.of(amount, percentage, label));
        }

        @JsonSetter(value = "charges", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder charges(List<Charge> charges) {
            this.charges = charges;
            return this;
        }

        /**
         * Convenience method to add a single charge to the receipt.
         */
        public ReceiptTemplateRequest.Builder addCharge(Charge charge) {
            if (this.charges == null) {
                this.charges = new ArrayList<>();
            }
            this.charges.add(charge);
            return this;
        }

        /**
         * Convenience method to add a charge with amount and label.
         */
        public ReceiptTemplateRequest.Builder addCharge(BigDecimal amount, String label) {
            return addCharge(Charge.of(amount, label));
        }

        /**
         * Convenience method to add a charge with amount (String) and label.
         */
        public ReceiptTemplateRequest.Builder addCharge(String amount, String label) {
            return addCharge(Charge.of(amount, label));
        }

        /**
         * Convenience method to add a charge with amount, percentage, and label.
         */
        public ReceiptTemplateRequest.Builder addCharge(BigDecimal amount, Double percentage, String label) {
            return addCharge(Charge.of(amount, percentage, label));
        }

        /**
         * Convenience method to add a charge with amount (String), percentage, and label.
         */
        public ReceiptTemplateRequest.Builder addCharge(String amount, Double percentage, String label) {
            return addCharge(Charge.of(amount, percentage, label));
        }

        @JsonSetter(value = "taxes", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder taxes(List<Tax> taxes) {
            this.taxes = taxes;
            return this;
        }

        /**
         * Convenience method to add a single tax entry.
         */
        public ReceiptTemplateRequest.Builder addTax(Tax tax) {
            if (this.taxes == null) {
                this.taxes = new ArrayList<>();
            }
            this.taxes.add(tax);
            return this;
        }

        /**
         * Convenience method to add a tax with rate, type, taxable amount, and tax amount.
         */
        public ReceiptTemplateRequest.Builder addTax(Double rate, String type, BigDecimal taxableAmount, BigDecimal amount) {
            return addTax(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .taxableAmount(taxableAmount)
                    .amount(amount)
                    .build());
        }

        /**
         * Convenience method to add a tax with rate, type, taxable amount, and tax amount (String).
         */
        public ReceiptTemplateRequest.Builder addTax(Double rate, String type, String taxableAmount, String amount) {
            return addTax(rate, type, new BigDecimal(taxableAmount), new BigDecimal(amount));
        }

        @JsonSetter(value = "transactionDate", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder transactionDate(Instant transactionDate) {
            this.transactionDate = Optional.ofNullable(transactionDate);
            return this;
        }

        @JsonSetter(value = "purchaseDate", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder purchaseDate(Instant purchaseDate) {
            this.purchaseDate = Optional.ofNullable(purchaseDate);
            return this;
        }

        @JsonSetter(value = "period", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder period(Period period) {
            this.period = Optional.ofNullable(period);
            return this;
        }

        @JsonSetter(value = "note", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder note(String note) {
            this.note = Optional.ofNullable(note);
            return this;
        }

        @JsonSetter(value = "childCompanyId", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder childCompanyId(UUID childCompanyId) {
            this.childCompanyId = Optional.ofNullable(childCompanyId);
            return this;
        }

        /**
         * Sets the buyer's ISO country code for VAT metadata resolution.
         * Used to determine cross-border VAT regime (e.g., reverse charge, intra-EU).
         *
         * @param buyerCountryCode ISO 3166-1 alpha-2 country code (e.g., "DE", "FR", "US")
         */
        public ReceiptTemplateRequest.Builder buyerCountryCode(String buyerCountryCode) {
            this.buyerCountryCode = buyerCountryCode;
            return this;
        }

        /**
         * Sets the recipient entity type for VAT metadata resolution.
         *
         * @param recipientEntityType BUSINESS for B2B or CONSUMER for B2C transactions
         */
        public ReceiptTemplateRequest.Builder recipientEntityType(RecipientEntityType recipientEntityType) {
            this.recipientEntityType = recipientEntityType;
            return this;
        }

        /**
         * Indicates whether taxes were applied to this transaction.
         * Set to false when no taxes apply (e.g., US sales tax not charged, non-VAT jurisdiction).
         *
         * @param taxesApplied true if taxes were charged, false if not
         */
        public ReceiptTemplateRequest.Builder taxesApplied(Boolean taxesApplied) {
            this.taxesApplied = taxesApplied;
            return this;
        }

        public ReceiptTemplateRequest build() {
            return new ReceiptTemplateRequest(childCompanyId, documentNumber, identifiers, issueDate, currency,
                    receiptSubtotal, totalBeforeTax, totalTaxAmount, totalAmount, products, discounts, charges, taxes,
                    transactionDate, purchaseDate, period, note, additionalProperties,
                    buyerCountryCode, recipientEntityType, taxesApplied);
        }
    }
}