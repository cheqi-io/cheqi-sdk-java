package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PartyTaxScheme {
    @XmlElement(name = "CompanyID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String companyID;
    @XmlElement(name = "TaxScheme")
    private TaxScheme taxScheme;

    public PartyTaxScheme() {
    }

    private PartyTaxScheme(Builder builder) {
        this.companyID = builder.companyID;
        this.taxScheme = builder.taxScheme;
    }

    public static class Builder {
        private String companyID;
        private TaxScheme taxScheme;

        public Builder(String companyID) {
            this.companyID = companyID;
        }

        public Builder companyID(String companyID) {
            this.companyID = companyID;
            return this;
        }

        public Builder taxScheme(TaxScheme taxScheme) {
            this.taxScheme = taxScheme;
            return this;
        }

        public PartyTaxScheme build() {
            return new PartyTaxScheme(this);
        }
    }

    public String getCompanyID() {
        return companyID;
    }

    public TaxScheme getTaxScheme() {
        return taxScheme;
    }
}
