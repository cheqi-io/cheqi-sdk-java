package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PartyIdentification {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;

    public PartyIdentification() {
    }

    private PartyIdentification(Builder builder) {
        this.id = builder.id;
    }

    public static class Builder {
        private Identifier id;

        public Builder(Identifier id) {
            if (id == null) {
                throw new IllegalArgumentException("Identifier cannot be null");
            }
            this.id = id;
        }

        public PartyIdentification build() {
            return new PartyIdentification(this);
        }
    }

    public Identifier getId() {
        return id;
    }
}
