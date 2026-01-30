package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PartyName {
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String name;

    public PartyName() {
    }

    private PartyName(Builder builder) {
        this.name = builder.name;
    }

    public static class Builder {
        private final String name;

        public Builder(String name) {
            this.name = name;
        }

        public PartyName build() {
            return new PartyName(this);
        }
    }

    public String getName() {
        return name;
    }
}
