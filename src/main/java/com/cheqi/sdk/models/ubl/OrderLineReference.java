package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class OrderLineReference {
    @XmlElement(name = "LineID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String lineID;

    public OrderLineReference() {
        //No args constructor for JAXB
    }

    private OrderLineReference(Builder builder) {
        this.lineID = builder.lineID;
    }

    public static class Builder {
        private String lineID;

        public Builder(String lineID) {
            if (lineID == null) {
                throw new IllegalArgumentException("LineID cannot be null");
            }
            this.lineID = lineID;
        }

        public OrderLineReference build() {
            return new OrderLineReference(this);
        }
    }

    public String getLineID() {
        return lineID;
    }
}
