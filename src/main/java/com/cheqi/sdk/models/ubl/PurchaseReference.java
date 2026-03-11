package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PurchaseReference {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String description;

    private PurchaseReference(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
    }

    public Identifier getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private Identifier id;
        private String description;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public PurchaseReference build() {
            return new PurchaseReference(this);
        }
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }
}