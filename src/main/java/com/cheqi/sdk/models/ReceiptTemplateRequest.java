package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
 *     .invoiceSubtotal(new BigDecimal("100.00"))
 *     .totalBeforeTax(new BigDecimal("95.00"))  // After receipt-level discount
 *     .totalTaxAmount(new BigDecimal("19.95"))  // Total tax
 *     .totalAmount(new BigDecimal("114.95"))    // Final amount
 *     
 *     // Products with type-safe unit codes
 *     .addProduct(Product.builder()
 *         .name("Laptop")
 *         .quantity(1.0)
 *         .unitCode(UnitCode.ONE)  // Type-safe enum
 *         .unitPrice("100.00")
 *         .subtotal("100.00")
 *         .total("121.00")
 *         .addTax(21.0, "VAT", "21.00")
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
 *   <li><strong>invoiceSubtotal</strong>: Sum of all product line totals</li>
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
 * <h3>Key Differences from invoiceSubtotal and totalBeforeTax:</h3>
 * <ul>
 *   <li><strong>invoiceSubtotal</strong>: Sum of all product line totals (before receipt-level adjustments)</li>
 *   <li><strong>totalBeforeTax</strong>: invoiceSubtotal ± receipt-level discounts/charges</li>
 *   <li>Example: invoiceSubtotal = $100, discount = $5, charge = $3 → totalBeforeTax = $98</li>
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
    @JsonProperty("invoiceSubtotal")
    private final BigDecimal invoiceSubtotal;

    /**
     * The total amount of the invoice excluding taxes, but including any overall discounts or charges.
     * This amount may differ from the invoiceSubtotal if there are invoice-level adjustments.
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

    // ===== CONSTRUCTOR =====

    private ReceiptTemplateRequest(
            String documentNumber,
            List<Identifier> identifiers,
            Instant issueDate,
            String currency,
            BigDecimal invoiceSubtotal,
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
            Map<String, Object> additionalProperties) {
        this.documentNumber = documentNumber;
        this.identifiers = identifiers != null ? List.copyOf(identifiers) : List.of();
        this.issueDate = issueDate;
        this.currency = currency;
        this.invoiceSubtotal = invoiceSubtotal;
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
    }

    // ===== MANDATORY FIELD ACCESSORS =====

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
    public BigDecimal getInvoiceSubtotal() {
        return invoiceSubtotal;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ReceiptTemplateRequest && equalTo((ReceiptTemplateRequest) other);
    }

    private boolean equalTo(ReceiptTemplateRequest other) {
        return Objects.equals(this.documentNumber, other.documentNumber)
                && Objects.equals(this.identifiers, other.identifiers)
                && Objects.equals(this.issueDate, other.issueDate)
                && Objects.equals(this.currency, other.currency)
                && Objects.equals(this.invoiceSubtotal, other.invoiceSubtotal)
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
        return Objects.hash(this.documentNumber, this.identifiers, this.issueDate, this.currency,
                this.invoiceSubtotal, this.totalBeforeTax, this.totalTaxAmount, this.totalAmount,
                this.products, this.discounts, this.charges, this.taxes, this.transactionDate,
                this.purchaseDate, this.period, this.note);
    }

    @Override
    public String toString() {
        return "ReceiptTemplateRequestDto{" +
                "documentNumber='" + documentNumber + '\'' +
                ", identifiers=" + identifiers +
                ", issueDate=" + issueDate +
                ", currency='" + currency + '\'' +
                ", invoiceSubtotal=" + invoiceSubtotal +
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
        private String documentNumber;
        private List<Identifier> identifiers;
        private Instant issueDate;
        private String currency;
        private BigDecimal invoiceSubtotal;
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

        private Builder() {}

        public ReceiptTemplateRequest.Builder from(ReceiptTemplateRequest other) {
            documentNumber(other.getDocumentNumber());
            identifiers(other.getIdentifiers());
            issueDate(other.getIssueDate());
            currency(other.getcurrency());
            invoiceSubtotal(other.getInvoiceSubtotal());
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

        @JsonSetter(value = "currency", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        @JsonSetter(value = "invoiceSubtotal", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder invoiceSubtotal(BigDecimal invoiceSubtotal) {
            this.invoiceSubtotal = invoiceSubtotal;
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
         * Convenience method to add a tax with rate, type, and amount.
         */
        public ReceiptTemplateRequest.Builder addTax(Double rate, String type, BigDecimal amount) {
            return addTax(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .amount(amount)
                    .build());
        }

        /**
         * Convenience method to add a tax with rate, type, and amount (String).
         */
        public ReceiptTemplateRequest.Builder addTax(Double rate, String type, String amount) {
            return addTax(rate, type, new BigDecimal(amount));
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

        public ReceiptTemplateRequest build() {
            return new ReceiptTemplateRequest(documentNumber, identifiers, issueDate, currency,
                    invoiceSubtotal, totalBeforeTax, totalTaxAmount, totalAmount, products, discounts, charges, taxes,
                    transactionDate, purchaseDate, period, note, additionalProperties);
        }
    }

    // ===== VALIDATION METHODS =====

    /**
     * Validates this receipt template request.
     * @return true if all mandatory fields are present and valid
     */
    public boolean isValid() {
        List<String> errors = getValidationErrors();
        return errors.isEmpty();
    }

    /**
     * Gets detailed validation errors for this receipt template request.
     * @return List of validation error messages, empty if valid
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Mandatory field validation
        if (documentNumber == null || documentNumber.trim().isEmpty()) {
            errors.add("Receipt ID is required");
        }
        if (issueDate == null) {
            errors.add("Issue date is required");
        }
        if (currency == null || currency.trim().isEmpty()) {
            errors.add("Currency code is required");
        }
        if (invoiceSubtotal == null) {
            errors.add("Invoice subtotal is required");
        }
        if (totalBeforeTax == null) {
            errors.add("Total before tax is required");
        }
        if (totalTaxAmount == null) {
            errors.add("Total tax amount is required");
        }
        if (totalAmount == null) {
            errors.add("Total amount is required");
        }
        if (products == null || products.isEmpty()) {
            errors.add("At least one product is required");
        }
        if (taxes == null || taxes.isEmpty()) {
            errors.add("At least one tax entry is required");
        }

        // Validate nested objects
        if (products != null) {
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                if (product == null) {
                    errors.add("Product " + (i + 1) + " cannot be null");
                } else {
                    List<String> productErrors = product.getValidationErrors();
                    for (String error : productErrors) {
                        errors.add("Product " + (i + 1) + ": " + error);
                    }
                }
            }
        }

        // Validate taxes
        if (taxes != null) {
            for (int i = 0; i < taxes.size(); i++) {
                Tax tax = taxes.get(i);
                if (tax == null) {
                    errors.add("Tax " + (i + 1) + " cannot be null");
                } else {
                    if (tax.getRate() == null) {
                        errors.add("Tax " + (i + 1) + ": rate is required");
                    }
                    if (tax.getType() == null || tax.getType().trim().isEmpty()) {
                        errors.add("Tax " + (i + 1) + ": type is required");
                    }
                }
            }
        }

        return errors;
    }
}