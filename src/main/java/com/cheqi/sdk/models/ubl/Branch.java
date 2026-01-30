package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Branch {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name name;
    @XmlElement(name = "FinancialInstitution", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private FinancialInstitution financialInstitution;
    @XmlElement(name = "Address", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Address address;

    public Branch() {
    }

    private Branch(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.financialInstitution = builder.financialInstitution;
        this.address = builder.address;
    }

    public static class Builder {
        private Identifier id;
        private Name name;
        private FinancialInstitution financialInstitution;
        private Address address;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder financialInstitution(FinancialInstitution financialInstitution) {
            this.financialInstitution = financialInstitution;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Branch build() {
            return new Branch(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Identifier getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public FinancialInstitution getFinancialInstitution() {
        return financialInstitution;
    }

    public Address getAddress() {
        return address;
    }
}
