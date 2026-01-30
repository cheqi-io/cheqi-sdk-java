package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class OrderReference {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "SalesOrderID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String salesOrderID;

    public OrderReference() {
        //No args constructor for JAXB
    }

    public OrderReference(Identifier id) {
        this.id = id;
    }

    public OrderReference(Identifier id, String salesOrderID) {
        this.id = id;
        this.salesOrderID = salesOrderID;
    }

    private OrderReference(Builder builder) {
        this.id = builder.id;
        this.salesOrderID = builder.salesOrderID;
    }

    public static class Builder {
        private Identifier id;
        private String salesOrderID;

        public Builder(Identifier id) {
            if (id == null) {
                throw new IllegalArgumentException("Identifier cannot be null");
            }
            this.id = id;
        }

        public Builder salesOrderID(String salesOrderID) {
            this.salesOrderID = salesOrderID;
            return this;
        }

        public OrderReference build() {
            return new OrderReference(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public String getSalesOrderID() {
        return salesOrderID;
    }
}
