package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class FinancialInstitutionBranch {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;

    public FinancialInstitutionBranch() {
        //No args constructor for JAXB
    }

    private FinancialInstitutionBranch(Builder builder) {
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

        public FinancialInstitutionBranch build() {
            return new FinancialInstitutionBranch(this);
        }
    }

    public Identifier getId() {
        return id;
    }
}
