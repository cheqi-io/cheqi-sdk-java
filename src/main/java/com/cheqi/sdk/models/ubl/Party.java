package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Party {
    @XmlElement(name = "EndpointID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier endpointID;
    @XmlElement(name = "PartyIdentification")
    private List<PartyIdentification> partyIdentification;
    @XmlElement(name = "PartyName")
    private PartyName partyName;
    @XmlElement(name = "PostalAddress")
    private Address postalAddress;
    @XmlElement(name = "PartyTaxScheme")
    private List<PartyTaxScheme> partyTaxScheme;
    @XmlElement(name = "PartyLegalEntity")
    private PartyLegalEntity partyLegalEntity;
    @XmlElement(name = "Contact")
    private Contact contact;


    public Party() {
    }

    private Party(Builder builder) {
        this.endpointID = builder.endpointID;
        this.postalAddress = builder.postalAddress;
        this.partyLegalEntity = builder.partyLegalEntity;
        this.partyIdentification = builder.partyIdentification;
        this.partyName = builder.partyName;
        this.partyTaxScheme = builder.partyTaxScheme;
        this.contact = builder.contact;
    }

    public static class Builder {
        private final Identifier endpointID;
        private List<PartyIdentification> partyIdentification;
        private PartyName partyName;
        private final Address postalAddress;
        private List<PartyTaxScheme> partyTaxScheme;
        private final PartyLegalEntity partyLegalEntity;
        private Contact contact;

        public Builder(Identifier endpointID, PartyLegalEntity partyLegalEntity, Address postalAddress) {
            if (endpointID == null || partyLegalEntity == null || postalAddress == null) {
                throw new IllegalArgumentException("EndpointID, PartyLegalEntity, and PostalAddress cannot be null");
            }
            this.endpointID = endpointID;
            this.partyLegalEntity = partyLegalEntity;
            this.postalAddress = postalAddress;
        }

        public Builder partyIdentification(List<PartyIdentification> partyIdentification) {
            this.partyIdentification = partyIdentification;
            return this;
        }

        public Builder partyName(PartyName partyName) {
            this.partyName = partyName;
            return this;
        }

        public Builder partyTaxScheme(List<PartyTaxScheme> partyTaxScheme) {
            if (partyTaxScheme != null && partyTaxScheme.size() > 2) {
                throw new IllegalStateException("Cannot add more than 2 PartyTaxScheme objects");
            }
            this.partyTaxScheme = partyTaxScheme;
            return this;
        }

        public Builder contact(Contact contact) {
            this.contact = contact;
            return this;
        }

        public Party build() {
            return new Party(this);
        }
    }

    public Identifier getEndpointID() {
        return endpointID;
    }

    public void setEndpointID(Identifier endpointID) {
        this.endpointID = endpointID;
    }

    public List<PartyIdentification> getPartyIdentification() {
        return partyIdentification;
    }

    public void setPartyIdentification(List<PartyIdentification> partyIdentification) {
        this.partyIdentification = partyIdentification;
    }

    public PartyName getPartyName() {
        return partyName;
    }

    public void setPartyName(PartyName partyName) {
        this.partyName = partyName;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<PartyTaxScheme> getPartyTaxScheme() {
        return partyTaxScheme;
    }

    public void setPartyTaxScheme(List<PartyTaxScheme> partyTaxScheme) {
        this.partyTaxScheme = partyTaxScheme;
    }

    public PartyLegalEntity getPartyLegalEntity() {
        return partyLegalEntity;
    }

    public void setPartyLegalEntity(PartyLegalEntity partyLegalEntity) {
        this.partyLegalEntity = partyLegalEntity;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
