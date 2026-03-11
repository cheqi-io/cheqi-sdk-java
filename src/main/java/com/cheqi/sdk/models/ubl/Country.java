package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Country {

    @XmlElement(name = "IdentificationCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String identificationCode;

    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String name;

    public Country() {
    }

    private Country(Builder builder) {
        this.identificationCode = builder.identificationCode;
        this.name = builder.name;
    }

    public static class Builder {
        private String identificationCode;
        private String name;

        public Builder identificationCode(String identificationCode) {
            this.identificationCode = identificationCode;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getIdentificationCode() {
        return identificationCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "identificationCode='" + identificationCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
