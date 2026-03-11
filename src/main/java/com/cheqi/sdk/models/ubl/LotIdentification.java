package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class LotIdentification {
    @XmlElement(name = "LotNumberID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier lotNumberID;
    @XmlElement(name = "ExpiryDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate expiryDate;
    @XmlElement(name = "AdditionalItemProperty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemProperty> additionalItemProperty;

    public LotIdentification() {
    }

    private LotIdentification(Builder builder) {
        this.lotNumberID = builder.lotNumberID;
        this.expiryDate = builder.expiryDate;
        this.additionalItemProperty = builder.additionalItemProperty;
    }

    public static class Builder {
        private Identifier lotNumberID;
        private LocalDate expiryDate;
        private List<ItemProperty> additionalItemProperty;

        public Builder lotNumberID(Identifier lotNumberID) {
            this.lotNumberID = lotNumberID;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder additionalItemProperty(List<ItemProperty> additionalItemProperty) {
            this.additionalItemProperty = additionalItemProperty;
            return this;
        }

        public Builder addAdditionalItemProperty(ItemProperty itemProperty) {
            if (this.additionalItemProperty == null) {
                this.additionalItemProperty = new ArrayList<>();
            }
            this.additionalItemProperty.add(itemProperty);
            return this;
        }

        public LotIdentification build() {
            return new LotIdentification(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public Identifier getLotNumberID() {
        return lotNumberID;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public List<ItemProperty> getAdditionalItemProperty() {
        return additionalItemProperty;
    }

    @Override
    public String toString() {
        return "LotIdentification{" +
                "lotNumberID=" + lotNumberID +
                ", expiryDate=" + expiryDate +
                ", additionalItemProperty=" + additionalItemProperty +
                '}';
    }
}
