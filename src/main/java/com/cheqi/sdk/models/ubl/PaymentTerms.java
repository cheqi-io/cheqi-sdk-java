package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PaymentTerms {
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String note;

    public PaymentTerms() {
        //No-arg constructor for JAXB
    }

    private PaymentTerms(Builder builder) {
        this.note = builder.note;
    }

    public static class Builder {
        private final String note;

        public Builder(String note) {
            if (note == null) {
                throw new IllegalArgumentException("Note cannot be null");
            }
            this.note = note;
        }

        public PaymentTerms build() {
            return new PaymentTerms(this);
        }
    }

    public String getNote() {
        return note;
    }
}
