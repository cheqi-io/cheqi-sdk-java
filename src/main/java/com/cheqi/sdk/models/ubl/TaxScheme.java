package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TaxScheme {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name name;
    @XmlElement(name = "TaxTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code taxTypeCode;
    @XmlElement(name = "CurrencyCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code currencyCode;
    @XmlElement(name = "JurisdictionRegionAddress")
    private Address jurisdictionRegionAddress;

    public TaxScheme() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Identifier id;
        private Name name;
        private Code taxTypeCode;
        private Code currencyCode;
        private Address jurisdictionRegionAddress;

        private Builder() {
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder taxTypeCode(Code taxTypeCode) {
            this.taxTypeCode = taxTypeCode;
            return this;
        }

        public Builder currencyCode(Code currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder jurisdictionRegionAddress(Address jurisdictionRegionAddress) {
            this.jurisdictionRegionAddress = jurisdictionRegionAddress;
            return this;
        }

        public TaxScheme build() {
            TaxScheme taxScheme = new TaxScheme();
            taxScheme.id = this.id;
            taxScheme.name = this.name;
            taxScheme.taxTypeCode = this.taxTypeCode;
            taxScheme.currencyCode = this.currencyCode;
            taxScheme.jurisdictionRegionAddress = this.jurisdictionRegionAddress;
            return taxScheme;
        }
    }

    // Getters (if needed)
    public Identifier getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Code getTaxTypeCode() {
        return taxTypeCode;
    }

    public Code getCurrencyCode() {
        return currencyCode;
    }

    public Address getJurisdictionRegionAddress() {
        return jurisdictionRegionAddress;
    }
}