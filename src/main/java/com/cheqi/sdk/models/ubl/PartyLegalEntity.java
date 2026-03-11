package com.cheqi.sdk.models.ubl;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PartyLegalEntity {
    @XmlElement(name = "RegistrationName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String registrationName;
    @XmlElement(name = "CompanyID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    @JsonProperty("companyID")
    private Identifier companyId;
    @XmlElement(name = "CompanyLegalForm", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String companyLegalForm;

    public PartyLegalEntity() {
    }

    private PartyLegalEntity(Builder builder) {
        this.registrationName = builder.registrationName;
        this.companyId = builder.companyId;
        this.companyLegalForm = builder.companyLegalForm;
    }

    public static class Builder {
        private final String registrationName;
        private Identifier companyId;
        private String companyLegalForm;

        public Builder(String registrationName) {
            if (registrationName == null) {
                throw new IllegalArgumentException("RegistrationName cannot be null");
            }
            this.registrationName = registrationName;
        }

        public Builder companyID(Identifier companyIdentifier) {
            this.companyId = companyIdentifier;
            return this;
        }

        public Builder companyLegalForm(String companyLegalForm) {
            this.companyLegalForm = companyLegalForm;
            return this;
        }

        public PartyLegalEntity build() {
            return new PartyLegalEntity(this);
        }
    }

    public String getRegistrationName() {
        return registrationName;
    }

    public void setRegistrationName(String registrationName) {
        this.registrationName = registrationName;
    }

    public Identifier getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Identifier companyId) {
        this.companyId = companyId;
    }

    public String getCompanyLegalForm() {
        return companyLegalForm;
    }

    public void setCompanyLegalForm(String companyLegalForm) {
        this.companyLegalForm = companyLegalForm;
    }
}
