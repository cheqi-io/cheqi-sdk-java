package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.UnitCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for generating credit note templates.
 * Extends the generated base with a builder pattern and convenience methods.
 *
 * <p>Reuses the same {@link Product}, {@link Tax}, {@link Discount}, and {@link Charge}
 * helpers as receipts — there is no separate credit-note product type.</p>
 *
 * <pre>
 * CreditNoteTemplateRequest creditNote = CreditNoteTemplateRequest.builder()
 *     .documentNumber("CN-2024-001")
 *     .originatorDocumentReference("INV-2024-001") // the original receipt
 *     .issueDate(Instant.now())
 *     .currency("EUR")
 *     .creditNoteSubtotal(new BigDecimal("-100.00"))
 *     .totalBeforeTax(new BigDecimal("-100.00"))
 *     .totalTaxAmount(new BigDecimal("-21.00"))
 *     .totalAmount(new BigDecimal("-121.00"))
 *     // identifier must match the original receipt line so the credit note maps back to it
 *     .addProduct("SKU-LAP-001", "Acme", "Laptop", "100.00", "-100.00", 21.0, "-100.00", "-21.00", "-121.00", -1.0)
 *     .addTax(21.0, "VAT", "-100.00", "-21.00")
 *     .build();
 * </pre>
 */
public class CreditNoteTemplateRequest extends com.cheqi.sdk.models.generated.CreditNoteTemplateRequest {

    public CreditNoteTemplateRequest() {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID childCompanyId;
        private String documentNumber;
        private String originatorDocumentReference;
        private List<com.cheqi.sdk.models.generated.Identifier> identifiers = new ArrayList<>();
        private OffsetDateTime issueDate;
        private String currency;
        private BigDecimal creditNoteSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalTaxAmount;
        private BigDecimal totalAmount;
        private List<com.cheqi.sdk.models.generated.Product> products = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Discount> discounts = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Charge> charges = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Tax> taxes = new ArrayList<>();
        private com.cheqi.sdk.models.generated.Period period;
        private String note;

        private Builder() {}

        public Builder from(CreditNoteTemplateRequest other) {
            this.childCompanyId = other.getChildCompanyId();
            this.documentNumber = other.getDocumentNumber();
            this.originatorDocumentReference = other.getOriginatorDocumentReference();
            this.identifiers = other.getIdentifiers() != null ? new ArrayList<>(other.getIdentifiers()) : new ArrayList<>();
            this.issueDate = other.getIssueDate();
            this.currency = other.getCurrency();
            this.creditNoteSubtotal = other.getCreditNoteSubtotal();
            this.totalBeforeTax = other.getTotalBeforeTax();
            this.totalTaxAmount = other.getTotalTaxAmount();
            this.totalAmount = other.getTotalAmount();
            this.products = other.getProducts() != null ? new ArrayList<>(other.getProducts()) : new ArrayList<>();
            this.discounts = other.getDiscounts() != null ? new ArrayList<>(other.getDiscounts()) : new ArrayList<>();
            this.charges = other.getCharges() != null ? new ArrayList<>(other.getCharges()) : new ArrayList<>();
            this.taxes = other.getTaxes() != null ? new ArrayList<>(other.getTaxes()) : new ArrayList<>();
            this.period = other.getPeriod();
            this.note = other.getNote();
            return this;
        }

        public Builder childCompanyId(UUID childCompanyId) {
            this.childCompanyId = childCompanyId;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder originatorDocumentReference(String originatorDocumentReference) {
            this.originatorDocumentReference = originatorDocumentReference;
            return this;
        }

        public Builder identifiers(List<com.cheqi.sdk.models.generated.Identifier> identifiers) {
            this.identifiers = identifiers;
            return this;
        }

        public Builder issueDate(OffsetDateTime issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate.atOffset(ZoneOffset.UTC);
            return this;
        }

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

        public Builder creditNoteSubtotal(BigDecimal creditNoteSubtotal) {
            this.creditNoteSubtotal = creditNoteSubtotal;
            return this;
        }

        public Builder creditNoteSubtotal(String creditNoteSubtotal) {
            this.creditNoteSubtotal = new BigDecimal(creditNoteSubtotal);
            return this;
        }

        public Builder totalBeforeTax(BigDecimal totalBeforeTax) {
            this.totalBeforeTax = totalBeforeTax;
            return this;
        }

        public Builder totalBeforeTax(String totalBeforeTax) {
            this.totalBeforeTax = new BigDecimal(totalBeforeTax);
            return this;
        }

        public Builder totalTaxAmount(BigDecimal totalTaxAmount) {
            this.totalTaxAmount = totalTaxAmount;
            return this;
        }

        public Builder totalTaxAmount(String totalTaxAmount) {
            this.totalTaxAmount = new BigDecimal(totalTaxAmount);
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder totalAmount(String totalAmount) {
            this.totalAmount = new BigDecimal(totalAmount);
            return this;
        }

        // ===== PRODUCTS =====

        public Builder products(List<com.cheqi.sdk.models.generated.Product> products) {
            this.products = products;
            return this;
        }

        public Builder addProduct(com.cheqi.sdk.models.generated.Product product) {
            this.products.add(product);
            return this;
        }

        /**
         * Convenience method to add a credit-note line. {@code identifier} must match the
         * identifier of the original receipt line being credited (the returned {@code lineItemId})
         * so the credit note can be mapped back to it.
         */
        public Builder addProduct(String identifier, String brand, String name, String unitPrice,
                                  String subtotal, Double taxRate, String taxableAmount,
                                  String taxAmount, String total, Double quantity) {
            return addProduct(Product.builder()
                    .brandName(brand)
                    .name(name)
                    .identifier(identifier)
                    .quantity(quantity)
                    .baseQuantity(1.0)
                    .unitCode(UnitCode.C62)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .total(total)
                    .addTax(taxRate, "VAT", taxableAmount, taxAmount)
                    .build());
        }

        // ===== DISCOUNTS =====

        public Builder discounts(List<com.cheqi.sdk.models.generated.Discount> discounts) {
            this.discounts = discounts;
            return this;
        }

        public Builder addDiscount(com.cheqi.sdk.models.generated.Discount discount) {
            this.discounts.add(discount);
            return this;
        }

        public Builder addDiscount(BigDecimal amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        public Builder addDiscount(String amount, String label) {
            return addDiscount(Discount.of(amount, label));
        }

        // ===== CHARGES =====

        public Builder charges(List<com.cheqi.sdk.models.generated.Charge> charges) {
            this.charges = charges;
            return this;
        }

        public Builder addCharge(com.cheqi.sdk.models.generated.Charge charge) {
            this.charges.add(charge);
            return this;
        }

        public Builder addCharge(BigDecimal amount, String label) {
            return addCharge(Charge.of(amount, label));
        }

        public Builder addCharge(String amount, String label) {
            return addCharge(Charge.of(amount, label));
        }

        // ===== TAXES =====

        public Builder taxes(List<com.cheqi.sdk.models.generated.Tax> taxes) {
            this.taxes = taxes;
            return this;
        }

        public Builder addTax(com.cheqi.sdk.models.generated.Tax tax) {
            this.taxes.add(tax);
            return this;
        }

        public Builder addTax(Double rate, String type, BigDecimal taxableAmount, BigDecimal amount) {
            return addTax(Tax.builder().rate(rate).type(type).taxableAmount(taxableAmount).amount(amount).build());
        }

        public Builder addTax(Double rate, String type, String taxableAmount, String amount) {
            return addTax(rate, type, new BigDecimal(taxableAmount), new BigDecimal(amount));
        }

        // ===== OPTIONAL FIELDS =====

        public Builder period(com.cheqi.sdk.models.generated.Period period) {
            this.period = period;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public CreditNoteTemplateRequest build() {
            CreditNoteTemplateRequest r = new CreditNoteTemplateRequest();
            r.setChildCompanyId(childCompanyId);
            r.setDocumentNumber(documentNumber);
            r.setOriginatorDocumentReference(originatorDocumentReference);
            r.setIdentifiers(identifiers);
            r.setIssueDate(issueDate);
            r.setCurrency(currency);
            r.setCreditNoteSubtotal(creditNoteSubtotal);
            r.setTotalBeforeTax(totalBeforeTax);
            r.setTotalTaxAmount(totalTaxAmount);
            r.setTotalAmount(totalAmount);
            r.setProducts(products);
            r.setDiscounts(discounts);
            r.setCharges(charges);
            r.setTaxes(taxes);
            r.setPeriod(period);
            r.setNote(note);
            return r;
        }
    }
}
