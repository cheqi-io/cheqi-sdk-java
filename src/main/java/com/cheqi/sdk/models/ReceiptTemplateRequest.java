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
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>receiptId</strong>: Unique receipt identifier</li>
 *   <li><strong>issueDate</strong>: Receipt issue date</li>
 *   <li><strong>documentCurrencyCode</strong>: ISO 4217 currency code</li>
 *   <li><strong>invoiceSubtotal</strong>: Subtotal before adjustments</li>
 *   <li><strong>totalBeforeTax</strong>: Total excluding taxes</li>
 *   <li><strong>totalAmount</strong>: Final amount charged to customer</li>
 *   <li><strong>products</strong>: List of products (minimum 1 required)</li>
 *   <li><strong>taxBreakDown</strong>: Tax specifications</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>transactionDate</strong>: When transaction was initiated (if different from issue date)</li>
 *   <li><strong>purchaseDate</strong>: When purchase was made (if different from issue date)</li>
 *   <li><strong>note</strong>: Additional seller notes</li>
 * </ul>
 *
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>totalAmount should equal totalBeforeTax + tax amounts</li>
 *   <li>invoiceSubtotal should equal sum of all product line totals</li>
 *   <li>Currency must be consistent across all monetary fields</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ReceiptTemplateRequest.Builder.class)
public final class ReceiptTemplateRequest {
    /**
     * A unique receipt identifier provided by the seller (for example invoice number).
     */
    @JsonProperty("receiptId")
    private final String receiptId;

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
    private final String documentCurrencyCode;

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
     * The total amount charged to the customer. Should equal totalBeforeTax + taxes.
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
     * The tax specifications for the given transaction.
     */
    @JsonProperty("taxBreakdown")
    private final TaxBreakDown taxBreakDown;

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
     * A note that the seller wants to include on the purchase receipt.
     */
    @JsonProperty("note")
    private final Optional<String> note;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private ReceiptTemplateRequest(
            String receiptId,
            Instant issueDate,
            String documentCurrencyCode,
            BigDecimal invoiceSubtotal,
            BigDecimal totalBeforeTax,
            BigDecimal totalAmount,
            List<Product> products,
            TaxBreakDown taxBreakDown,
            Optional<Instant> transactionDate,
            Optional<Instant> purchaseDate,
            Optional<String> note,
            Map<String, Object> additionalProperties) {
        this.receiptId = receiptId;
        this.issueDate = issueDate;
        this.documentCurrencyCode = documentCurrencyCode;
        this.invoiceSubtotal = invoiceSubtotal;
        this.totalBeforeTax = totalBeforeTax;
        this.totalAmount = totalAmount;
        this.products = products;
        this.taxBreakDown = taxBreakDown;
        this.transactionDate = transactionDate;
        this.purchaseDate = purchaseDate;
        this.note = note;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public String getReceiptId() {
        return receiptId;
    }

    @JsonIgnore
    public Instant getIssueDate() {
        return issueDate;
    }

    @JsonIgnore
    public String getDocumentCurrencyCode() {
        return documentCurrencyCode;
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
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @JsonIgnore
    public List<Product> getProducts() {
        return products;
    }

    @JsonIgnore
    public TaxBreakDown getTaxBreakDown() {
        return taxBreakDown;
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
        return Objects.equals(this.receiptId, other.receiptId)
                && Objects.equals(this.issueDate, other.issueDate)
                && Objects.equals(this.documentCurrencyCode, other.documentCurrencyCode)
                && Objects.equals(this.invoiceSubtotal, other.invoiceSubtotal)
                && Objects.equals(this.totalBeforeTax, other.totalBeforeTax)
                && Objects.equals(this.totalAmount, other.totalAmount)
                && Objects.equals(this.products, other.products)
                && Objects.equals(this.taxBreakDown, other.taxBreakDown)
                && Objects.equals(this.transactionDate, other.transactionDate)
                && Objects.equals(this.purchaseDate, other.purchaseDate)
                && Objects.equals(this.note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.receiptId, this.issueDate, this.documentCurrencyCode,
                this.invoiceSubtotal, this.totalBeforeTax, this.totalAmount,
                this.products, this.taxBreakDown, this.transactionDate,
                this.purchaseDate, this.note);
    }

    @Override
    public String toString() {
        return "ReceiptTemplateRequestDto{" +
                "receiptId='" + receiptId + '\'' +
                ", issueDate=" + issueDate +
                ", documentCurrencyCode='" + documentCurrencyCode + '\'' +
                ", invoiceSubtotal=" + invoiceSubtotal +
                ", totalBeforeTax=" + totalBeforeTax +
                ", totalAmount=" + totalAmount +
                ", products=" + products +
                ", taxBreakDown=" + taxBreakDown +
                ", transactionDate=" + transactionDate +
                ", purchaseDate=" + purchaseDate +
                ", note=" + note +
                '}';
    }

    public static ReceiptTemplateRequest.Builder builder() {
        return new ReceiptTemplateRequest.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String receiptId;
        private Instant issueDate;
        private String documentCurrencyCode;
        private BigDecimal invoiceSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalAmount;
        private List<Product> products;
        private TaxBreakDown taxBreakDown;
        private Optional<Instant> transactionDate = Optional.empty();
        private Optional<Instant> purchaseDate = Optional.empty();
        private Optional<String> note = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public ReceiptTemplateRequest.Builder from(ReceiptTemplateRequest other) {
            receiptId(other.getReceiptId());
            issueDate(other.getIssueDate());
            documentCurrencyCode(other.getDocumentCurrencyCode());
            invoiceSubtotal(other.getInvoiceSubtotal());
            totalBeforeTax(other.getTotalBeforeTax());
            totalAmount(other.getTotalAmount());
            products(other.getProducts());
            taxBreakDown(other.getTaxBreakDown());
            transactionDate(other.getTransactionDate().orElse(null));
            purchaseDate(other.getPurchaseDate().orElse(null));
            note(other.getNote().orElse(null));
            return this;
        }

        @JsonSetter(value = "receiptId", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        @JsonSetter(value = "issueDate", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        @JsonSetter(value = "currency", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder documentCurrencyCode(String documentCurrencyCode) {
            this.documentCurrencyCode = documentCurrencyCode;
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

        @JsonSetter(value = "totalAmount", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        @JsonSetter(value = "products", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        @JsonSetter(value = "taxBreakdown", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder taxBreakDown(TaxBreakDown taxBreakDown) {
            this.taxBreakDown = taxBreakDown;
            return this;
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

        @JsonSetter(value = "note", nulls = Nulls.SKIP)
        public ReceiptTemplateRequest.Builder note(String note) {
            this.note = Optional.ofNullable(note);
            return this;
        }

        public ReceiptTemplateRequest build() {
            return new ReceiptTemplateRequest(receiptId, issueDate, documentCurrencyCode,
                    invoiceSubtotal, totalBeforeTax, totalAmount, products, taxBreakDown,
                    transactionDate, purchaseDate, note, additionalProperties);
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
        if (receiptId == null || receiptId.trim().isEmpty()) {
            errors.add("Receipt ID is required");
        }
        if (issueDate == null) {
            errors.add("Issue date is required");
        }
        if (documentCurrencyCode == null || documentCurrencyCode.trim().isEmpty()) {
            errors.add("Currency code is required");
        }
        if (invoiceSubtotal == null) {
            errors.add("Invoice subtotal is required");
        }
        if (totalBeforeTax == null) {
            errors.add("Total before tax is required");
        }
        if (totalAmount == null) {
            errors.add("Total amount is required");
        }
        if (products == null || products.isEmpty()) {
            errors.add("At least one product is required");
        }
        if (taxBreakDown == null) {
            errors.add("Tax breakdown is required");
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

        if (taxBreakDown != null) {
            List<String> taxErrors = taxBreakDown.getValidationErrors();
            for (String error : taxErrors) {
                errors.add("Tax breakdown: " + error);
            }
        }

        return errors;
    }
}