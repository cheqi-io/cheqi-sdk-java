package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.BarcodeType;
import com.cheqi.sdk.models.generated.UnitCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Product line item for Cheqi receipts.
 * Extends the generated base with a builder pattern and convenience methods.
 *
 * <pre>
 * Product laptop = Product.builder()
 *     .name("Laptop 13\"")
 *     .identifier("LAP-001")
 *     .quantity(1.0)
 *     .baseQuantity(1.0)
 *     .unitCode(UnitCode.C62)
 *     .unitPrice("1000.00")
 *     .subtotal("1000.00")
 *     .total("1210.00")
 *     .addTax(21.0, "VAT", "1000.00", "210.00")
 *     .build();
 * </pre>
 */
public class Product extends com.cheqi.sdk.models.generated.Product {

    public Product() {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String identifier;
        private String description;
        private String brandName;
        private Double quantity;
        private Double baseQuantity;
        private UnitCode unitCode;
        private BigDecimal unitPrice;
        private List<com.cheqi.sdk.models.generated.Discount> discounts = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Charge> charges = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Tax> taxes = new ArrayList<>();
        private List<com.cheqi.sdk.models.generated.Barcode> barcodes = new ArrayList<>();
        private BigDecimal subtotal;
        private BigDecimal total;
        private com.cheqi.sdk.models.generated.Period period;

        private Builder() {}

        public Builder from(com.cheqi.sdk.models.generated.Product other) {
            this.name = other.getName();
            this.identifier = other.getIdentifier();
            this.description = other.getDescription();
            this.brandName = other.getBrandName();
            this.quantity = other.getQuantity();
            this.baseQuantity = other.getBaseQuantity();
            this.unitCode = other.getUnitCode();
            this.unitPrice = other.getUnitPrice();
            this.discounts = other.getDiscounts() != null ? new ArrayList<>(other.getDiscounts()) : new ArrayList<>();
            this.charges = other.getCharges() != null ? new ArrayList<>(other.getCharges()) : new ArrayList<>();
            this.taxes = other.getTaxes() != null ? new ArrayList<>(other.getTaxes()) : new ArrayList<>();
            this.barcodes = other.getBarcodes() != null ? new ArrayList<>(other.getBarcodes()) : new ArrayList<>();
            this.subtotal = other.getSubtotal();
            this.total = other.getTotal();
            this.period = other.getPeriod();
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder brandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder baseQuantity(Double baseQuantity) {
            this.baseQuantity = baseQuantity;
            return this;
        }

        public Builder unitCode(UnitCode unitCode) {
            this.unitCode = unitCode;
            return this;
        }

        public Builder unitCode(String unitCode) {
            this.unitCode = UnitCode.fromValue(unitCode);
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder unitPrice(String unitPrice) {
            this.unitPrice = new BigDecimal(unitPrice);
            return this;
        }

        // ===== DISCOUNTS =====

        public Builder discounts(List<com.cheqi.sdk.models.generated.Discount> discounts) {
            this.discounts = discounts != null ? discounts : new ArrayList<>();
            return this;
        }

        public Builder addDiscount(com.cheqi.sdk.models.generated.Discount discount) {
            this.discounts.add(discount);
            return this;
        }

        public Builder addDiscount(BigDecimal amount, String label) {
            this.discounts.add(Discount.of(amount, label));
            return this;
        }

        public Builder addDiscount(String amount, String label) {
            this.discounts.add(Discount.of(amount, label));
            return this;
        }

        public Builder addDiscount(BigDecimal amount, Double percentage, String label) {
            this.discounts.add(Discount.of(amount, percentage, label));
            return this;
        }

        public Builder addDiscount(String amount, Double percentage, String label) {
            this.discounts.add(Discount.of(amount, percentage, label));
            return this;
        }

        // ===== CHARGES =====

        public Builder charges(List<com.cheqi.sdk.models.generated.Charge> charges) {
            this.charges = charges != null ? charges : new ArrayList<>();
            return this;
        }

        public Builder addCharge(com.cheqi.sdk.models.generated.Charge charge) {
            this.charges.add(charge);
            return this;
        }

        public Builder addCharge(BigDecimal amount, String label) {
            this.charges.add(Charge.of(amount, label));
            return this;
        }

        public Builder addCharge(String amount, String label) {
            this.charges.add(Charge.of(amount, label));
            return this;
        }

        public Builder addCharge(BigDecimal amount, Double percentage, String label) {
            this.charges.add(Charge.of(amount, percentage, label));
            return this;
        }

        public Builder addCharge(String amount, Double percentage, String label) {
            this.charges.add(Charge.of(amount, percentage, label));
            return this;
        }

        // ===== TAXES =====

        public Builder taxes(List<com.cheqi.sdk.models.generated.Tax> taxes) {
            this.taxes = taxes != null ? taxes : new ArrayList<>();
            return this;
        }

        public Builder addTax(com.cheqi.sdk.models.generated.Tax tax) {
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

        // ===== BARCODES =====

        public Builder barcodes(List<com.cheqi.sdk.models.generated.Barcode> barcodes) {
            this.barcodes = barcodes != null ? barcodes : new ArrayList<>();
            return this;
        }

        public Builder addBarcode(com.cheqi.sdk.models.generated.Barcode barcode) {
            this.barcodes.add(barcode);
            return this;
        }

        public Builder addBarcode(BarcodeType type, String data, String label) {
            this.barcodes.add(Barcode.of(type, data, label));
            return this;
        }

        public Builder addQrCode(String data, String label) {
            this.barcodes.add(Barcode.qrCode(data, label));
            return this;
        }

        // ===== TOTALS & PERIOD =====

        public Builder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Builder subtotal(String subtotal) {
            this.subtotal = new BigDecimal(subtotal);
            return this;
        }

        public Builder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Builder total(String total) {
            this.total = new BigDecimal(total);
            return this;
        }

        public Builder period(com.cheqi.sdk.models.generated.Period period) {
            this.period = period;
            return this;
        }

        public Product build() {
            Product p = new Product();
            p.setName(name);
            p.setIdentifier(identifier);
            p.setDescription(description);
            p.setBrandName(brandName);
            p.setQuantity(quantity);
            p.setBaseQuantity(baseQuantity);
            p.setUnitCode(unitCode);
            p.setUnitPrice(unitPrice);
            p.setDiscounts(discounts.isEmpty() ? null : discounts);
            p.setCharges(charges.isEmpty() ? null : charges);
            p.setTaxes(taxes.isEmpty() ? null : taxes);
            p.setBarcodes(barcodes.isEmpty() ? null : barcodes);
            p.setSubtotal(subtotal);
            p.setTotal(total);
            p.setPeriod(period);
            return p;
        }
    }
}
