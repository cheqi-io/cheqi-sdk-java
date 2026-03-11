package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class FinancialAccount {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name name;
    @XmlElement(name = "AliasName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Name aliasName;
    @XmlElement(name = "AccountTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code accountTypeCode;
    @XmlElement(name = "AccountFormatCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code accountFormatCode;
    @XmlElement(name = "CurrencyCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code currencyCode;
    @XmlElement(name = "PaymentNote", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> paymentNote;
    @XmlElement(name = "FinancialInstitutionBranch", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Branch financialInstitutionBranch;
    @XmlElement(name = "Country", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Country country;

    public FinancialAccount() {
    }

    private FinancialAccount(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.aliasName = builder.aliasName;
        this.accountTypeCode = builder.accountTypeCode;
        this.accountFormatCode = builder.accountFormatCode;
        this.currencyCode = builder.currencyCode;
        this.paymentNote = builder.paymentNote;
        this.financialInstitutionBranch = builder.financialInstitutionBranch;
        this.country = builder.country;
    }

    public static class Builder {
        private Identifier id;
        private Name name;
        private Name aliasName;
        private Code accountTypeCode;
        private Code accountFormatCode;
        private Code currencyCode;
        private List<String> paymentNote;
        private Branch financialInstitutionBranch;
        private Country country;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder aliasName(Name aliasName) {
            this.aliasName = aliasName;
            return this;
        }

        public Builder accountTypeCode(Code accountTypeCode) {
            this.accountTypeCode = accountTypeCode;
            return this;
        }

        public Builder accountFormatCode(Code accountFormatCode) {
            this.accountFormatCode = accountFormatCode;
            return this;
        }

        public Builder currencyCode(Code currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder paymentNote(List<String> paymentNote) {
            this.paymentNote = paymentNote;
            return this;
        }

        public Builder financialInstitutionBranch(Branch financialInstitutionBranch) {
            this.financialInstitutionBranch = financialInstitutionBranch;
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }

        public FinancialAccount build() {
            return new FinancialAccount(this);
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

    public Name getAliasName() {
        return aliasName;
    }

    public Code getAccountTypeCode() {
        return accountTypeCode;
    }

    public Code getAccountFormatCode() {
        return accountFormatCode;
    }

    public Code getCurrencyCode() {
        return currencyCode;
    }

    public List<String> getPaymentNote() {
        return paymentNote;
    }

    public Branch getFinancialInstitutionBranch() {
        return financialInstitutionBranch;
    }

    public Country getCountry() {
        return country;
    }
}
