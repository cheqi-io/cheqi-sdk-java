package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Delivery {
    @XmlElement(name = "ActualDeliveryDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String actualDeliveryDate;
    @XmlElement(name = "DeliveryLocation")
    private Location deliveryLocation;
    @XmlElement(name = "DeliveryParty")
    private Party deliveryParty;

    public Delivery() {
        // no-arg constructor for JAXB
    }

    private Delivery(Builder builder) {
        this.actualDeliveryDate = builder.actualDeliveryDate;
        this.deliveryLocation = builder.deliveryLocation;
        this.deliveryParty = builder.deliveryParty;
    }

    public static class Builder {
        private String actualDeliveryDate;
        private Location deliveryLocation;
        private Party deliveryParty;

        public Builder(String actualDeliveryDate, Location deliveryLocation, Party deliveryParty) {
            if (actualDeliveryDate == null || deliveryLocation == null || deliveryParty == null) {
                throw new IllegalArgumentException("ActualDeliveryDate, DeliveryLocation and DeliveryParty cannot be null");
            }
            this.actualDeliveryDate = actualDeliveryDate;
            this.deliveryLocation = deliveryLocation;
            this.deliveryParty = deliveryParty;
        }

        public Delivery build() {
            return new Delivery(this);
        }
    }

    public String getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public Party getDeliveryParty() {
        return deliveryParty;
    }
}
