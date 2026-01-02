package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request DTO for generating credit note templates.
 * 
 * <p>This class provides a simplified API for creating credit notes.
 * The POS system provides all calculated values - no calculations are performed by the SDK.</p>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * CreditNoteTemplateRequest creditNote = CreditNoteTemplateRequest.builder()
 *     // Basic information
 *     .documentNumber("CN-2024-001")
 *     .originatorDocumentReference("INV-2024-001")  // Original receipt
 *     .issueDate(Instant.now())
 *     .currency("EUR")
 *     
 *     // Totals (POS-calculated, negative for refunds)
 *     .invoiceSubtotal(new BigDecimal("-100.00"))
 *     .totalBeforeTax(new BigDecimal("-100.00"))
 *     .totalTaxAmount(new BigDecimal("-21.00"))
 *     .totalAmount(new BigDecimal("-121.00"))
 *     
 *     // Simple product addition (POS provides all amounts)
 *     .addProduct("Nike", "AirMax", "125.00", "-125.00", 21.0, "-26.25", "-151.25", -1.0)
 *     
 *     // Or use full Product builder
 *     .addProduct(Product.builder()
 *         .name("Laptop")
 *         .quantity(-1.0)  // Negative for returns
 *         .unitCode(UnitCode.ONE)
 *         .unitPrice("100.00")
 *         .subtotal("-100.00")
 *         .total("-121.00")
 *         .addTax(21.0, "VAT", "-21.00")
 *         .build())
 *     
 *     // Tax breakdown
 *     .addTax(Tax.builder()
 *         .rate(21.0)
 *         .type("VAT")
 *         .amount("-21.00")
 *         .build())
 *     
 *     .note("Refund for returned item")
 *     .build();
 * </pre>
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>documentNumber</strong>: Unique credit note identifier</li>
 *   <li><strong>originatorDocumentReference</strong>: Reference to original receipt</li>
 *   <li><strong>issueDate</strong>: Credit note issue date</li>
 *   <li><strong>currency</strong>: ISO 4217 currency code</li>
 *   <li><strong>invoiceSubtotal</strong>: Sum of all product line totals</li>
 *   <li><strong>totalBeforeTax</strong>: Total excluding taxes</li>
 *   <li><strong>totalTaxAmount</strong>: Total tax amount</li>
 *   <li><strong>totalAmount</strong>: Final amount</li>
 *   <li><strong>products</strong>: List of products (minimum 1 required)</li>
 *   <li><strong>taxes</strong>: Tax breakdown by category/rate</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ReceiptTemplateRequest.Builder.class)
public final class CreditNoteTemplateRequest {
    @JsonProperty("documentNumber")
    private final String documentNumber;

    @JsonProperty("originatorDocumentReference")
    private final String originatorDocumentReference;

    @JsonProperty("identifiers")
    private final List<Identifier> identifiers;

    @JsonProperty("issueDate")
    private final Instant issueDate;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("invoiceSubtotal")
    private final BigDecimal invoiceSubtotal;

    @JsonProperty("totalBeforeTax")
    private final BigDecimal totalBeforeTax;

    @JsonProperty("totalTaxAmount")
    private final BigDecimal totalTaxAmount;

    @JsonProperty("totalAmount")
    private final BigDecimal totalAmount;

    @JsonProperty("products")
    private final List<Product> products;

    @JsonProperty("discounts")
    private final List<Discount> discounts;

    @JsonProperty("taxes")
    private final List<Tax> taxes;

    @JsonProperty("note")
    private final String note;

    // ===== CONSTRUCTOR =====

    private CreditNoteTemplateRequest(
            String documentNumber,
            String originatorDocumentReference,
            List<Identifier> identifiers,
            Instant issueDate,
            String currency,
            BigDecimal invoiceSubtotal,
            BigDecimal totalBeforeTax,
            BigDecimal totalTaxAmount,
            BigDecimal totalAmount,
            List<Product> products,
            List<Discount> discounts,
            List<Tax> taxes,
            String note) {
        this.documentNumber = documentNumber;
        this.originatorDocumentReference = originatorDocumentReference;
        this.identifiers = identifiers != null ? List.copyOf(identifiers) : List.of();
        this.issueDate = issueDate;
        this.currency = currency;
        this.invoiceSubtotal = invoiceSubtotal;
        this.totalBeforeTax = totalBeforeTax;
        this.totalTaxAmount = totalTaxAmount;
        this.totalAmount = totalAmount;
        this.products = products;
        this.discounts = discounts != null ? List.copyOf(discounts) : List.of();
        this.taxes = taxes != null ? List.copyOf(taxes) : List.of();
        this.note = note;
    }

    // ===== ACCESSORS =====

    @JsonIgnore
    public String getDocumentNumber() {
        return documentNumber;
    }

    @JsonIgnore
    public String getOriginatorDocumentReference() {
        return originatorDocumentReference;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    @JsonIgnore
    public Instant getIssueDate() {
        return issueDate;
    }

    @JsonIgnore
    public String getCurrency() {
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
    public List<Tax> getTaxes() {
        return taxes;
    }

    @JsonIgnore
    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof CreditNoteTemplateRequest && equalTo((CreditNoteTemplateRequest) other);
    }

    private boolean equalTo(CreditNoteTemplateRequest other) {
        return Objects.equals(this.documentNumber, other.documentNumber)
                && Objects.equals(this.originatorDocumentReference, other.originatorDocumentReference)
                && Objects.equals(this.identifiers, other.identifiers)
                && Objects.equals(this.issueDate, other.issueDate)
                && Objects.equals(this.currency, other.currency)
                && Objects.equals(this.invoiceSubtotal, other.invoiceSubtotal)
                && Objects.equals(this.totalBeforeTax, other.totalBeforeTax)
                && Objects.equals(this.totalTaxAmount, other.totalTaxAmount)
                && Objects.equals(this.totalAmount, other.totalAmount)
                && Objects.equals(this.products, other.products)
                && Objects.equals(this.discounts, other.discounts)
                && Objects.equals(this.taxes, other.taxes)
                && Objects.equals(this.note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.documentNumber, this.originatorDocumentReference, this.identifiers,
                this.issueDate, this.currency, this.invoiceSubtotal, this.totalBeforeTax,
                this.totalTaxAmount, this.totalAmount, this.products, this.discounts,
                this.taxes, this.note);
    }

    @Override
    public String toString() {
        return "CreditNoteTemplateRequest{" +
                "documentNumber='" + documentNumber + '\'' +
                ", originatorDocumentReference='" + originatorDocumentReference + '\'' +
                ", identifiers=" + identifiers +
                ", issueDate=" + issueDate +
                ", currency='" + currency + '\'' +
                ", invoiceSubtotal=" + invoiceSubtotal +
                ", totalBeforeTax=" + totalBeforeTax +
                ", totalTaxAmount=" + totalTaxAmount +
                ", totalAmount=" + totalAmount +
                ", products=" + products +
                ", discounts=" + discounts +
                ", taxes=" + taxes +
                ", note='" + note + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String documentNumber;
        private String originatorDocumentReference;
        private List<Identifier> identifiers;
        private Instant issueDate;
        private String currency;
        private BigDecimal invoiceSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalTaxAmount;
        private BigDecimal totalAmount;
        private List<Product> products;
        private List<Discount> discounts;
        private List<Tax> taxes;
        private String note;

        private Builder() {}

        public Builder from(CreditNoteTemplateRequest other) {
            documentNumber(other.getDocumentNumber());
            originatorDocumentReference(other.getOriginatorDocumentReference());
            identifiers(other.getIdentifiers());
            issueDate(other.getIssueDate());
            currency(other.getCurrency());
            invoiceSubtotal(other.getInvoiceSubtotal());
            totalBeforeTax(other.getTotalBeforeTax());
            totalTaxAmount(other.getTotalTaxAmount());
            totalAmount(other.getTotalAmount());
            products(other.getProducts());
            discounts(other.getDiscounts());
            taxes(other.getTaxes());
            note(other.getNote());
            return this;
        }

        @JsonSetter(value = "documentNumber", nulls = Nulls.SKIP)
        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        @JsonSetter(value = "originatorDocumentReference", nulls = Nulls.SKIP)
        public Builder originatorDocumentReference(String originatorDocumentReference) {
            this.originatorDocumentReference = originatorDocumentReference;
            return this;
        }

        @JsonSetter(value = "identifiers", nulls = Nulls.SKIP)
        public Builder identifiers(List<Identifier> identifiers) {
            this.identifiers = identifiers;
            return this;
        }

        @JsonSetter(value = "issueDate", nulls = Nulls.SKIP)
        public Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        @JsonSetter(value = "currency", nulls = Nulls.SKIP)
        public Builder currency(String currency) {
            if (currency != null) {
                try {
                    java.util.Currency.getInstance(currency);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Invalid currency code: '" + currency + "'. Must be a valid ISO 4217 code (e.g., EUR, USD, GBP)", e);
                }
            }
            this.currency = currency;
            return this;
        }

        @JsonSetter(value = "invoiceSubtotal", nulls = Nulls.SKIP)
        public Builder invoiceSubtotal(BigDecimal invoiceSubtotal) {
            this.invoiceSubtotal = invoiceSubtotal;
            return this;
        }

        @JsonSetter(value = "totalBeforeTax", nulls = Nulls.SKIP)
        public Builder totalBeforeTax(BigDecimal totalBeforeTax) {
            this.totalBeforeTax = totalBeforeTax;
            return this;
        }

        public Builder totalBeforeTax(String totalBeforeTax) {
            this.totalBeforeTax = new BigDecimal(totalBeforeTax);
            return this;
        }

        @JsonSetter(value = "totalTaxAmount", nulls = Nulls.SKIP)
        public Builder totalTaxAmount(BigDecimal totalTaxAmount) {
            this.totalTaxAmount = totalTaxAmount;
            return this;
        }

        public Builder totalTaxAmount(String totalTaxAmount) {
            this.totalTaxAmount = new BigDecimal(totalTaxAmount);
            return this;
        }

        @JsonSetter(value = "totalAmount", nulls = Nulls.SKIP)
        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder totalAmount(String totalAmount) {
            this.totalAmount = new BigDecimal(totalAmount);
            return this;
        }

        @JsonSetter(value = "products", nulls = Nulls.SKIP)
        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        /**
         * Convenience method to add a single product to the receipt.
         */
        public CreditNoteTemplateRequest.Builder addProduct(Product product) {
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
         * @param brand Product brand/manufacturer
         * @param name Product name/model
         * @param unitPrice Price per unit
         * @param subtotal Subtotal (unitPrice × quantity, pre-calculated)
         * @param taxRate Tax rate as percentage (e.g., 21.0 for 21%)
         * @param taxAmount Tax amount (pre-calculated)
         * @param total Total amount including tax (pre-calculated)
         * @param quantity Number of units
         * @return This builder
         */
        public CreditNoteTemplateRequest.Builder addProduct(String brand, String name, String unitPrice, String subtotal, Double taxRate, String taxAmount, String total, Double quantity) {
            return addProduct(Product.builder()
                    .brand(brand)
                    .name(name)
                    .quantity(quantity)
                    .baseQuantity(1.0)
                    .unitCode(UnitCode.ONE)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .total(total)
                    .addTax(taxRate, "VAT", taxAmount)
                    .build());
        }

        /**
         * Convenience method with BigDecimal amounts.
         */
        public CreditNoteTemplateRequest.Builder addProduct(String brand, String name, BigDecimal unitPrice, BigDecimal subtotal, Double taxRate, BigDecimal taxAmount, BigDecimal total, Double quantity) {
            return addProduct(brand, name, unitPrice.toString(), subtotal.toString(), taxRate, taxAmount.toString(), total.toString(), quantity);
        }


        @JsonSetter(value = "discounts", nulls = Nulls.SKIP)
        public Builder discounts(List<Discount> discounts) {
            this.discounts = discounts;
            return this;
        }

        public Builder addDiscount(Discount discount) {
            if (this.discounts == null) {
                this.discounts = new ArrayList<>();
            }
            this.discounts.add(discount);
            return this;
        }

        public Builder addDiscount(BigDecimal amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        public Builder addDiscount(String amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        @JsonSetter(value = "taxes", nulls = Nulls.SKIP)
        public Builder taxes(List<Tax> taxes) {
            this.taxes = taxes;
            return this;
        }

        public Builder addTax(Tax tax) {
            if (this.taxes == null) {
                this.taxes = new ArrayList<>();
            }
            this.taxes.add(tax);
            return this;
        }

        public Builder addTax(Double rate, String type, BigDecimal amount) {
            return addTax(Tax.builder()
                    .rate(rate)
                    .type(type)
                    .amount(amount)
                    .build());
        }

        public Builder addTax(Double rate, String type, String amount) {
            return addTax(rate, type, new BigDecimal(amount));
        }

        @JsonSetter(value = "note", nulls = Nulls.SKIP)
        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public CreditNoteTemplateRequest build() {
            return new CreditNoteTemplateRequest(documentNumber, originatorDocumentReference, identifiers,
                    issueDate, currency, invoiceSubtotal, totalBeforeTax, totalTaxAmount, totalAmount,
                    products, discounts, taxes, note);
        }
    }

    // ===== VALIDATION METHODS =====

    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (documentNumber == null || documentNumber.trim().isEmpty()) {
            errors.add("Document number is required");
        }
        if (originatorDocumentReference == null || originatorDocumentReference.trim().isEmpty()) {
            errors.add("Originator document reference is required");
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

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }
}
