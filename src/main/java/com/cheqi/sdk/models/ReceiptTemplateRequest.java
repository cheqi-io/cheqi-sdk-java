package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.BarcodeType;
import com.cheqi.sdk.models.generated.BuyerType;
import com.cheqi.sdk.models.generated.UnitCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for generating receipt templates.
 * Extends the generated base with a builder pattern and convenience methods.
 *
 * <pre>
 * ReceiptTemplateRequest receipt = ReceiptTemplateRequest.builder()
 *     .documentNumber("INV-2024-001")
 *     .issueDate(Instant.now())
 *     .currency("EUR")
 *     .receiptSubtotal(new BigDecimal("250.00"))
 *     .totalBeforeTax(new BigDecimal("245.00"))
 *     .totalTaxAmount(new BigDecimal("51.45"))
 *     .totalAmount(new BigDecimal("296.45"))
 *     .addProduct(Product.builder()
 *         .name("Laptop").identifier("LAP-001")
 *         .quantity(1.0).baseQuantity(1.0).unitCode(UnitCode.ONE)
 *         .unitPrice("100.00").subtotal("100.00").total("121.00")
 *         .addTax(21.0, "VAT", "100.00", "21.00")
 *         .build())
 *     .addTax(Tax.builder().rate(21.0).type("VAT").taxableAmount("100.00").amount("21.00").build())
 *     .build();
 * </pre>
 */
public class ReceiptTemplateRequest extends com.cheqi.sdk.models.generated.ReceiptTemplateRequest {

    // SDK-specific fields not in the API spec — used for VAT metadata resolution
    @JsonIgnore
    private String buyerCountryCode;
    @JsonIgnore
    private BuyerType buyerType;
    @JsonIgnore
    private Boolean taxesApplied;

    public ReceiptTemplateRequest() {}

    @JsonIgnore
    public String getBuyerCountryCode() { return buyerCountryCode; }
    public void setBuyerCountryCode(String buyerCountryCode) { this.buyerCountryCode = buyerCountryCode; }

    @JsonIgnore
    public BuyerType getBuyerType() { return buyerType; }
    public void setBuyerType(BuyerType buyerType) { this.buyerType = buyerType; }

    @JsonIgnore
    public Boolean getTaxesApplied() { return taxesApplied; }
    public void setTaxesApplied(Boolean taxesApplied) { this.taxesApplied = taxesApplied; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID childCompanyId;
        private String documentNumber;
        private List<com.cheqi.sdk.models.generated.Identifier> identifiers = new ArrayList<>();
        private OffsetDateTime issueDate;
        private String currency;
        private BigDecimal receiptSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalTaxAmount;
        private BigDecimal totalAmount;
        private List<com.cheqi.sdk.models.generated.Product> products = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Discount> discounts = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Charge> charges = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Tax> taxes = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Barcode> barcodes = new ArrayList<>();
        private OffsetDateTime transactionDate;
        private OffsetDateTime purchaseDate;
        private com.cheqi.sdk.models.generated.Period period;
        private String note;
        // SDK-specific
        private String buyerCountryCode;
        private BuyerType buyerType;
        private Boolean taxesApplied;

        private Builder() {}

        public Builder from(ReceiptTemplateRequest other) {
            this.childCompanyId = other.getChildCompanyId();
            this.documentNumber = other.getDocumentNumber();
            this.identifiers = other.getIdentifiers() != null ? new ArrayList<>(other.getIdentifiers()) : new ArrayList<>();
            this.issueDate = other.getIssueDate();
            this.currency = other.getCurrency();
            this.receiptSubtotal = other.getReceiptSubtotal();
            this.totalBeforeTax = other.getTotalBeforeTax();
            this.totalTaxAmount = other.getTotalTaxAmount();
            this.totalAmount = other.getTotalAmount();
            this.products = other.getProducts() != null ? new ArrayList<>(other.getProducts()) : new ArrayList<>();
            this.discounts = other.getDiscounts() != null ? new ArrayList<>(other.getDiscounts()) : new ArrayList<>();
            this.charges = other.getCharges() != null ? new ArrayList<>(other.getCharges()) : new ArrayList<>();
            this.taxes = other.getTaxes() != null ? new ArrayList<>(other.getTaxes()) : new ArrayList<>();
            this.barcodes = other.getBarcodes() != null ? new ArrayList<>(other.getBarcodes()) : new ArrayList<>();
            this.transactionDate = other.getTransactionDate();
            this.purchaseDate = other.getPurchaseDate();
            this.period = other.getPeriod();
            this.note = other.getNote();
            this.buyerCountryCode = other.getBuyerCountryCode();
            this.buyerType = other.getBuyerType();
            this.taxesApplied = other.getTaxesApplied();
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

        public Builder receiptSubtotal(BigDecimal receiptSubtotal) {
            this.receiptSubtotal = receiptSubtotal;
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

        public Builder addDiscount(BigDecimal amount, Double percentage, String label) {
            return addDiscount(Discount.of(amount, percentage, label));
        }

        public Builder addDiscount(String amount, Double percentage, String label) {
            return addDiscount(Discount.of(amount, percentage, label));
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

        public Builder addCharge(BigDecimal amount, Double percentage, String label) {
            return addCharge(Charge.of(amount, percentage, label));
        }

        public Builder addCharge(String amount, Double percentage, String label) {
            return addCharge(Charge.of(amount, percentage, label));
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

        // ===== BARCODES =====

        public Builder barcodes(List<com.cheqi.sdk.models.generated.Barcode> barcodes) {
            this.barcodes = barcodes;
            return this;
        }

        public Builder addBarcode(com.cheqi.sdk.models.generated.Barcode barcode) {
            this.barcodes.add(barcode);
            return this;
        }

        public Builder addBarcode(BarcodeType type, String data, String label) {
            return addBarcode(Barcode.of(type, data, label));
        }

        public Builder addQrCode(String data, String label) {
            return addBarcode(Barcode.qrCode(data, label));
        }

        // ===== OPTIONAL FIELDS =====

        public Builder transactionDate(OffsetDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder transactionDate(Instant transactionDate) {
            this.transactionDate = transactionDate != null ? transactionDate.atOffset(ZoneOffset.UTC) : null;
            return this;
        }

        public Builder purchaseDate(OffsetDateTime purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder purchaseDate(Instant purchaseDate) {
            this.purchaseDate = purchaseDate != null ? purchaseDate.atOffset(ZoneOffset.UTC) : null;
            return this;
        }

        public Builder period(com.cheqi.sdk.models.generated.Period period) {
            this.period = period;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        // ===== VAT METADATA (SDK-specific) =====

        public Builder buyerCountryCode(String buyerCountryCode) {
            this.buyerCountryCode = buyerCountryCode;
            return this;
        }

        public Builder buyerType(BuyerType buyerType) {
            this.buyerType = buyerType;
            return this;
        }

        public Builder taxesApplied(Boolean taxesApplied) {
            this.taxesApplied = taxesApplied;
            return this;
        }

        public ReceiptTemplateRequest build() {
            ReceiptTemplateRequest r = new ReceiptTemplateRequest();
            r.setChildCompanyId(childCompanyId);
            r.setDocumentNumber(documentNumber);
            r.setIdentifiers(identifiers);
            r.setIssueDate(issueDate);
            r.setCurrency(currency);
            r.setReceiptSubtotal(receiptSubtotal);
            r.setTotalBeforeTax(totalBeforeTax);
            r.setTotalTaxAmount(totalTaxAmount);
            r.setTotalAmount(totalAmount);
            r.setProducts(products);
            r.setDiscounts(discounts);
            r.setCharges(charges);
            r.setTaxes(taxes);
            r.setBarcodes(barcodes);
            r.setTransactionDate(transactionDate);
            r.setPurchaseDate(purchaseDate);
            r.setPeriod(period);
            r.setNote(note);
            // SDK-specific fields
            r.setBuyerCountryCode(buyerCountryCode);
            r.setBuyerType(buyerType);
            r.setTaxesApplied(taxesApplied);
            return r;
        }
    }
}
