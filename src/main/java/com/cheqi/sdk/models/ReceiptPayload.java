package com.cheqi.sdk.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenience builder over the generated receipt contract.
 *
 * <p>The builder only copies caller-supplied values. It performs no monetary, tax, fiscalization,
 * sequence, signature, or barcode calculations.</p>
 */
public class ReceiptPayload extends com.cheqi.sdk.models.generated.ReceiptPayload {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String documentNumber;
        private List<com.cheqi.sdk.models.generated.Identifier> identifiers = new ArrayList<>();
        private OffsetDateTime issueDate;
        private String currency;
        private BigDecimal receiptSubtotal;
        private BigDecimal totalBeforeTax;
        private BigDecimal totalTaxAmount;
        private BigDecimal totalAmount;
        private Boolean taxesApplied;
        private List<com.cheqi.sdk.models.generated.Product> products = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Discount> discounts = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Charge> charges = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Tax> taxes = new ArrayList<>();
        private OffsetDateTime transactionDate;
        private OffsetDateTime purchaseDate;
        private com.cheqi.sdk.models.generated.Period period;
        private String note;
        private List<com.cheqi.sdk.models.generated.Barcode> barcodes = new ArrayList<>();
        private com.cheqi.sdk.models.generated.JurisdictionalData jurisdictionalData;

        private Builder() {
        }

        public Builder documentNumber(String value) { documentNumber = value; return this; }
        public Builder issueDate(OffsetDateTime value) { issueDate = value; return this; }
        public Builder currency(String value) { currency = value; return this; }
        public Builder receiptSubtotal(BigDecimal value) { receiptSubtotal = value; return this; }
        public Builder receiptSubtotal(String value) { return receiptSubtotal(decimal(value)); }
        public Builder totalBeforeTax(BigDecimal value) { totalBeforeTax = value; return this; }
        public Builder totalBeforeTax(String value) { return totalBeforeTax(decimal(value)); }
        public Builder totalTaxAmount(BigDecimal value) { totalTaxAmount = value; return this; }
        public Builder totalTaxAmount(String value) { return totalTaxAmount(decimal(value)); }
        public Builder totalAmount(BigDecimal value) { totalAmount = value; return this; }
        public Builder totalAmount(String value) { return totalAmount(decimal(value)); }
        public Builder taxesApplied(boolean value) { taxesApplied = value; return this; }
        public Builder transactionDate(OffsetDateTime value) { transactionDate = value; return this; }
        public Builder purchaseDate(OffsetDateTime value) { purchaseDate = value; return this; }
        public Builder period(com.cheqi.sdk.models.generated.Period value) { period = value; return this; }
        public Builder note(String value) { note = value; return this; }
        public Builder jurisdictionalData(
                com.cheqi.sdk.models.generated.JurisdictionalData value
        ) {
            jurisdictionalData = value;
            return this;
        }

        public Builder identifiers(List<com.cheqi.sdk.models.generated.Identifier> values) {
            identifiers = copy(values);
            return this;
        }

        public Builder addIdentifier(com.cheqi.sdk.models.generated.Identifier value) {
            identifiers.add(value);
            return this;
        }

        public Builder products(List<com.cheqi.sdk.models.generated.Product> values) {
            products = copy(values);
            return this;
        }

        public Builder addProduct(com.cheqi.sdk.models.generated.Product value) {
            products.add(value);
            return this;
        }

        public Builder discounts(List<com.cheqi.sdk.models.generated.Discount> values) {
            discounts = copy(values);
            return this;
        }

        public Builder addDiscount(com.cheqi.sdk.models.generated.Discount value) {
            discounts.add(value);
            return this;
        }

        public Builder charges(List<com.cheqi.sdk.models.generated.Charge> values) {
            charges = copy(values);
            return this;
        }

        public Builder addCharge(com.cheqi.sdk.models.generated.Charge value) {
            charges.add(value);
            return this;
        }

        public Builder taxes(List<com.cheqi.sdk.models.generated.Tax> values) {
            taxes = copy(values);
            return this;
        }

        public Builder addTax(com.cheqi.sdk.models.generated.Tax value) {
            taxes.add(value);
            return this;
        }

        public Builder barcodes(List<com.cheqi.sdk.models.generated.Barcode> values) {
            barcodes = copy(values);
            return this;
        }

        public Builder addBarcode(com.cheqi.sdk.models.generated.Barcode value) {
            barcodes.add(value);
            return this;
        }

        public Builder addQrCode(String data, String label) {
            barcodes.add(Barcode.qrCode(data, label));
            return this;
        }

        public ReceiptPayload build() {
            require(documentNumber, "documentNumber");
            require(issueDate, "issueDate");
            require(currency, "currency");
            require(receiptSubtotal, "receiptSubtotal");
            require(totalBeforeTax, "totalBeforeTax");
            require(totalTaxAmount, "totalTaxAmount");
            require(totalAmount, "totalAmount");
            require(taxesApplied, "taxesApplied");
            if (products.isEmpty()) {
                throw new IllegalStateException("products must contain at least one item");
            }

            ReceiptPayload payload = new ReceiptPayload();
            payload.setDocumentNumber(documentNumber);
            payload.setIdentifiers(emptyToNull(identifiers));
            payload.setIssueDate(issueDate);
            payload.setCurrency(currency);
            payload.setReceiptSubtotal(receiptSubtotal);
            payload.setTotalBeforeTax(totalBeforeTax);
            payload.setTotalTaxAmount(totalTaxAmount);
            payload.setTotalAmount(totalAmount);
            payload.setTaxesApplied(taxesApplied);
            payload.setProducts(products);
            payload.setDiscounts(emptyToNull(discounts));
            payload.setCharges(emptyToNull(charges));
            payload.setTaxes(emptyToNull(taxes));
            payload.setTransactionDate(transactionDate);
            payload.setPurchaseDate(purchaseDate);
            payload.setPeriod(period);
            payload.setNote(note);
            payload.setBarcodes(emptyToNull(barcodes));
            payload.setJurisdictionalData(jurisdictionalData);
            return payload;
        }

        private static BigDecimal decimal(String value) {
            return value == null ? null : new BigDecimal(value);
        }

        private static <T> List<T> copy(List<T> values) {
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        }

        private static <T> List<T> emptyToNull(List<T> values) {
            return values.isEmpty() ? null : values;
        }

        private static void require(Object value, String name) {
            if (value == null || value instanceof String && ((String) value).trim().isEmpty()) {
                throw new IllegalStateException(name + " is required");
            }
        }
    }
}
