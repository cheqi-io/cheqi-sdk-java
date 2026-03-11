package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class CardAccount {
    @XmlElement(name = "PrimaryAccountNumberID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier primaryAccountNumberID;
    @XmlElement(name = "NetworkID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier networkID;
    @XmlElement(name = "CardTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code cardTypeCode;
    @XmlElement(name = "ValidityStartDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate validityStartDate;
    @XmlElement(name = "ExpiryDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate expiryDate;
    @XmlElement(name = "IssuerID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier issuerID;
    @XmlElement(name = "IssueNumberID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier issueNumberID;
    @XmlElement(name = "CV2ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier cV2ID;
    @XmlElement(name = "CardChipCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code cardChipCode;
    @XmlElement(name = "ChipApplicationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier chipApplicationID;
    @XmlElement(name = "HolderName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String holderName;
    @XmlElement(name = "HolderID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code roleCode;

    public CardAccount() {
    }

    private CardAccount(Builder builder) {
        this.primaryAccountNumberID = builder.primaryAccountNumberID;
        this.networkID = builder.networkID;
        this.cardTypeCode = builder.cardTypeCode;
        this.validityStartDate = builder.validityStartDate;
        this.expiryDate = builder.expiryDate;
        this.issuerID = builder.issuerID;
        this.issueNumberID = builder.issueNumberID;
        this.cV2ID = builder.cV2ID;
        this.cardChipCode = builder.cardChipCode;
        this.chipApplicationID = builder.chipApplicationID;
        this.holderName = builder.holderName;
        this.roleCode = builder.roleCode;
    }

    public static class Builder {
        private final Identifier primaryAccountNumberID;
        private final Identifier networkID;
        private Code cardTypeCode;
        private LocalDate validityStartDate;
        private LocalDate expiryDate;
        private Identifier issuerID;
        private Identifier issueNumberID;
        private Identifier cV2ID;
        private Code cardChipCode;
        private Identifier chipApplicationID;
        private String holderName;
        private Code roleCode;

        public Builder(Identifier primaryAccountNumberID, Identifier networkID) {
            this.primaryAccountNumberID = primaryAccountNumberID;
            this.networkID = networkID;
        }

        public Builder cardTypeCode(Code cardTypeCode) {
            this.cardTypeCode = cardTypeCode;
            return this;
        }

        public Builder validityStartDate(LocalDate validityStartDate) {
            this.validityStartDate = validityStartDate;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder issuerID(Identifier issuerID) {
            this.issuerID = issuerID;
            return this;
        }

        public Builder issueNumberID(Identifier issueNumberID) {
            this.issueNumberID = issueNumberID;
            return this;
        }

        public Builder cV2ID(Identifier cV2ID) {
            this.cV2ID = cV2ID;
            return this;
        }

        public Builder cardChipCode(Code cardChipCode) {
            this.cardChipCode = cardChipCode;
            return this;
        }

        public Builder chipApplicationID(Identifier chipApplicationID) {
            this.chipApplicationID = chipApplicationID;
            return this;
        }

        public Builder holderName(String holderName) {
            this.holderName = holderName;
            return this;
        }

        public Builder roleCode(Code roleCode) {
            this.roleCode = roleCode;
            return this;
        }

        public CardAccount build() {
            return new CardAccount(this);
        }
    }

    public static Builder builder(Identifier primaryAccountNumberID, Identifier networkID) {
        return new Builder(primaryAccountNumberID, networkID);
    }

    public Identifier getPrimaryAccountNumberID() {
        return primaryAccountNumberID;
    }

    public Identifier getNetworkID() {
        return networkID;
    }

    public Code getCardTypeCode() {
        return cardTypeCode;
    }

    public LocalDate getValidityStartDate() {
        return validityStartDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Identifier getIssuerID() {
        return issuerID;
    }

    public Identifier getIssueNumberID() {
        return issueNumberID;
    }

    public Identifier getcV2ID() {
        return cV2ID;
    }

    public Code getCardChipCode() {
        return cardChipCode;
    }

    public Identifier getChipApplicationID() {
        return chipApplicationID;
    }

    public String getHolderName() {
        return holderName;
    }

    public Code getRoleCode() {
        return roleCode;
    }
}
