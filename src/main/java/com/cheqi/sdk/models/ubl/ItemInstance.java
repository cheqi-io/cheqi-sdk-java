package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlElement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ItemInstance {
    @XmlElement(name = "ProductTraceID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier productTraceID;

    @XmlElement(name = "ManufactureDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate manufactureDate;

    @XmlElement(name = "ManufactureTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalTime manufactureTime;

    @XmlElement(name = "BestBeforeDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate bestBeforeDate;

    @XmlElement(name = "RegistrationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier registrationID;

    @XmlElement(name = "SerialID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier serialID;

    @XmlElement(name = "AdditionalItemProperty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemProperty> additionalItemProperty;

    @XmlElement(name = "LotIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private LotIdentification lotIdentification;

    private ItemInstance() {
    }

    private ItemInstance(Builder builder) {
        this.productTraceID = builder.productTraceID;
        this.manufactureDate = builder.manufactureDate;
        this.manufactureTime = builder.manufactureTime;
        this.bestBeforeDate = builder.bestBeforeDate;
        this.registrationID = builder.registrationID;
        this.serialID = builder.serialID;
        this.additionalItemProperty = builder.additionalItemProperty;
        this.lotIdentification = builder.lotIdentification;
    }

    public static class Builder {
        private Identifier productTraceID;
        private LocalDate manufactureDate;
        private LocalTime manufactureTime;
        private LocalDate bestBeforeDate;
        private Identifier registrationID;
        private Identifier serialID;
        private List<ItemProperty> additionalItemProperty;
        private LotIdentification lotIdentification;

        public Builder productTraceID(Identifier productTraceID) {
            this.productTraceID = productTraceID;
            return this;
        }

        public Builder manufactureDate(LocalDate manufactureDate) {
            this.manufactureDate = manufactureDate;
            return this;
        }

        public Builder manufactureTime(LocalTime manufactureTime) {
            this.manufactureTime = manufactureTime;
            return this;
        }

        public Builder bestBeforeDate(LocalDate bestBeforeDate) {
            this.bestBeforeDate = bestBeforeDate;
            return this;
        }

        public Builder registrationID(Identifier registrationID) {
            this.registrationID = registrationID;
            return this;
        }

        public Builder serialID(Identifier serialID) {
            this.serialID = serialID;
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

        public Builder lotIdentification(LotIdentification lotIdentification) {
            this.lotIdentification = lotIdentification;
            return this;
        }

        public ItemInstance build() {
            return new ItemInstance(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public Identifier getProductTraceID() {
        return productTraceID;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public LocalTime getManufactureTime() {
        return manufactureTime;
    }

    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }

    public Identifier getRegistrationID() {
        return registrationID;
    }

    public Identifier getSerialID() {
        return serialID;
    }

    public List<ItemProperty> getAdditionalItemProperty() {
        return additionalItemProperty;
    }

    public LotIdentification getLotIdentification() {
        return lotIdentification;
    }

    @Override
    public String toString() {
        return "ItemInstance{" +
                "productTraceID=" + productTraceID +
                ", manufactureDate=" + manufactureDate +
                ", manufactureTime=" + manufactureTime +
                ", bestBeforeDate=" + bestBeforeDate +
                ", registrationID=" + registrationID +
                ", serialID=" + serialID +
                ", additionalItemProperty=" + additionalItemProperty +
                ", lotIdentification=" + lotIdentification +
                '}';
    }
}
