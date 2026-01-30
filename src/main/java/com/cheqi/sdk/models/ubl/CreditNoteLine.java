package com.cheqi.sdk.models.ubl;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreditNoteLine {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;

    @XmlElement(name = "CreditedQuantity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Quantity creditedQuantity;

    @XmlElement(name = "LineExtensionAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount lineExtensionAmount;

    @XmlElement(name = "InvoicePeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Period invoicePeriod;

    @XmlElement(name = "Item", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Item item;

    @XmlElement(name = "Price", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Price price;

    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String note;

    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TaxTotal> taxTotal;

    public CreditNoteLine() {
    }

    private CreditNoteLine(Builder builder) {
        this.id = builder.id;
        this.creditedQuantity = builder.creditedQuantity;
        this.lineExtensionAmount = builder.lineExtensionAmount;
        this.invoicePeriod = builder.invoicePeriod;
        this.item = builder.item;
        this.price = builder.price;
        this.note = builder.note;
        this.taxTotal = builder.taxTotal;
    }

    public static class Builder {
        // Mandatory fields (per UBL spec)
        private final Identifier id;
        private final Quantity creditedQuantity;
        private final Amount lineExtensionAmount;

        // Optional fields
        private Period invoicePeriod;
        private Item item;
        private Price price;
        private String note;
        private List<TaxTotal> taxTotal;

        public Builder(Identifier id, Quantity creditedQuantity, Amount lineExtensionAmount) {
            this.id = id;
            this.creditedQuantity = creditedQuantity;
            this.lineExtensionAmount = lineExtensionAmount;
        }

        public Builder invoicePeriod(Period invoicePeriod) {
            this.invoicePeriod = invoicePeriod;
            return this;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder price(Price price) {
            this.price = price;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder taxTotal(List<TaxTotal> taxTotal) {
            this.taxTotal = taxTotal;
            return this;
        }

        public CreditNoteLine build() {
            return new CreditNoteLine(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public Quantity getCreditedQuantity() {
        return creditedQuantity;
    }

    public Amount getLineExtensionAmount() {
        return lineExtensionAmount;
    }

    public Period getInvoicePeriod() {
        return invoicePeriod;
    }

    public Item getItem() {
        return item;
    }

    public Price getPrice() {
        return price;
    }

    public String getNote() {
        return note;
    }

    public List<TaxTotal> getTaxTotal() {
        return taxTotal;
    }

    @Override
    public String toString() {
        return "CreditNoteLine{" +
                "id='" + id + '\'' +
                ", creditedQuantity=" + creditedQuantity +
                ", lineExtensionAmount=" + lineExtensionAmount +
                ", invoicePeriod=" + invoicePeriod +
                ", item=" + item +
                ", price=" + price +
                ", note='" + note + '\'' +
                ", taxTotal=" + taxTotal.toString() +
                '}';
    }
}
