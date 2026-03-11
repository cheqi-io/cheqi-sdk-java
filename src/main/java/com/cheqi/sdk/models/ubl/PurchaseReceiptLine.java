package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PurchaseReceiptLine {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Identifier id;
    @XmlElement(name = "UUID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private UUID uuid;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> note;
    @XmlElement(name = "InvoicedQuantity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Quantity quantity;
    @XmlElement(name = "LineExtensionAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount lineExtensionAmount;
    @XmlElement(name = "TaxInclusiveLineExtensionAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount taxInclusiveLineExtensionAmount;
    @XmlElement(name = "PurchaseLinePeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:AggregateComponents-2")
    private Period purchaseLinePeriod;
    @XmlElement(name = "PurchaseReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private PurchaseReference purchaseReference;
    @XmlElement(name = "AllowanceCharge", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<AllowanceCharge> allowanceCharges;
    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TaxTotal> taxTotals;
    @XmlElement(name = "Item", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", required = true)
    private Item item;  
    @XmlElement(name = "Price", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Price price;

    public PurchaseReceiptLine() {
    }

    private PurchaseReceiptLine(Builder builder) {
        this.id = builder.id;
        this.item = builder.item;
        this.uuid = builder.uuid;
        this.note = builder.note;
        this.quantity = builder.quantity;
        this.lineExtensionAmount = builder.lineExtensionAmount;
        this.taxInclusiveLineExtensionAmount = builder.taxInclusiveLineExtensionAmount;
        this.purchaseLinePeriod = builder.purchaseLinePeriod;
        this.purchaseReference = builder.purchaseReference;
        this.allowanceCharges = builder.allowanceCharges;
        this.taxTotals = builder.taxTotals;
        this.price = builder.price;
    }

    public static class Builder {
        private final Identifier id;
        private final Item item;
        private UUID uuid;
        private List<String> note;
        private Quantity quantity;
        private Amount lineExtensionAmount;
        private Amount taxInclusiveLineExtensionAmount;
        private Period purchaseLinePeriod;
        private PurchaseReference purchaseReference;
        private List<AllowanceCharge> allowanceCharges;
        private List<TaxTotal> taxTotals;
        private Price price;

        public Builder(Identifier id, Item item) {
            if (id == null) {
                throw new IllegalArgumentException("ID is mandatory and cannot be null");
            }
            if (item == null) {
                throw new IllegalArgumentException("Item is mandatory and cannot be null");
            }
            this.id = id;
            this.item = item;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder note(List<String> note) {
            this.note = note;
            return this;
        }

        public Builder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder lineExtensionAmount(Amount lineExtensionAmount) {
            this.lineExtensionAmount = lineExtensionAmount;
            return this;
        }

        public Builder taxInclusiveLineExtensionAmount(Amount taxInclusiveLineExtensionAmount) {
            this.taxInclusiveLineExtensionAmount = taxInclusiveLineExtensionAmount;
            return this;
        }

        public Builder purchaseLinePeriod(Period purchaseLinePeriod) {
            this.purchaseLinePeriod = purchaseLinePeriod;
            return this;
        }

        public Builder purchaseReference(PurchaseReference purchaseReference) {
            this.purchaseReference = purchaseReference;
            return this;
        }

        public Builder allowanceCharges(List<AllowanceCharge> allowanceCharges) {
            this.allowanceCharges = allowanceCharges;
            return this;
        }

        public Builder taxTotals(List<TaxTotal> taxTotals) {
            this.taxTotals = taxTotals;
            return this;
        }

        public Builder price(Price price) {
            this.price = price;
            return this;
        }

        public PurchaseReceiptLine build() {
            return new PurchaseReceiptLine(this);
        }
    }

    // Getters for all fields

    public Identifier getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<String> getNote() {
        return note;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Amount getLineExtensionAmount() {
        return lineExtensionAmount;
    }

    public Amount getTaxInclusiveLineExtensionAmount() {
        return taxInclusiveLineExtensionAmount;
    }

    public Period getPurchaseLinePeriod() {
        return purchaseLinePeriod;
    }

    public PurchaseReference getPurchaseReference() {
        return purchaseReference;
    }

    public List<AllowanceCharge> getAllowanceCharges() {
        return allowanceCharges;
    }

    public List<TaxTotal> getTaxTotals() {
        return taxTotals;
    }

    public Item getItem() {
        return item;
    }

    public Price getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PurchaseReceiptLine{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", note=" + note +
                ", quantity=" + quantity +
                ", lineExtensionAmount=" + lineExtensionAmount +
                ", taxInclusiveLineExtensionAmount=" + taxInclusiveLineExtensionAmount +
                ", purchaseLinePeriod=" + purchaseLinePeriod +
                ", purchaseReference=" + purchaseReference +
                ", allowanceCharges=" + allowanceCharges +
                ", taxTotals=" + taxTotals +
                ", item=" + item +
                ", price=" + price +
                '}';
    }
}
