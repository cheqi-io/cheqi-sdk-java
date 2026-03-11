package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class DocumentReference {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "CopyIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private boolean copyIndicator;
    @XmlElement(name = "UUID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private UUID uuid;
    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueDate;
    @XmlElement(name = "IssueTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueTime;
    @XmlElement(name = "DocumentTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String documentTypeCode;
    @XmlElement(name = "ReferencedDocumentInternalAddress", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String referencedDocumentInternalAddress;
    @XmlElement(name = "LanguageID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String languageID;
    @XmlElement(name = "LocaleCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String localeCode;
    @XmlElement(name = "VersionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String versionID;
    @XmlElement(name = "DocumentStatusCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String documentStatusCode;
    @XmlElement(name = "DocumentDescription", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String documentDescription;
    @XmlElement(name = "Attachment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Attachment attachment;
    @XmlElement(name = "ValidityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Period validityPeriod;
    @XmlElement(name = "IssuerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party issuerParty;
    @XmlElement(name = "ResultOfVerification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ResultOfVerification resultOfVerification;

    private DocumentReference(Builder builder) {
        this.id = builder.id;
        this.copyIndicator = builder.copyIndicator;
        this.uuid = builder.uuid;
        this.issueDate = builder.issueDate;
        this.issueTime = builder.issueTime;
        this.documentTypeCode = builder.documentTypeCode;
        this.referencedDocumentInternalAddress = builder.referencedDocumentInternalAddress;
        this.languageID = builder.languageID;
        this.localeCode = builder.localeCode;
        this.versionID = builder.versionID;
        this.documentStatusCode = builder.documentStatusCode;
        this.documentDescription = builder.documentDescription;
        this.attachment = builder.attachment;
        this.validityPeriod = builder.validityPeriod;
        this.issuerParty = builder.issuerParty;
        this.resultOfVerification = builder.resultOfVerification;
    }

    public DocumentReference(){
    }

    public static class Builder {
        // Mandatory field
        private final Identifier id;

        // Optional fields
        private boolean copyIndicator;
        private UUID uuid;
        private String issueDate;
        private String issueTime;
        private String documentTypeCode;
        private String referencedDocumentInternalAddress;
        private String languageID;
        private String localeCode;
        private String versionID;
        private String documentStatusCode;
        private String documentDescription;
        private Attachment attachment;
        private Period validityPeriod;
        private Party issuerParty;
        private ResultOfVerification resultOfVerification;

        // Constructor for mandatory field
        public Builder(Identifier identifier) {
            this.id = identifier;
        }

        public Builder copyIndicator(boolean copyIndicator) {
            this.copyIndicator = copyIndicator;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder issueDate(String issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder issueTime(String issueTime) {
            this.issueTime = issueTime;
            return this;
        }

        public Builder documentTypeCode(String documentTypeCode) {
            this.documentTypeCode = documentTypeCode;
            return this;
        }

        public Builder referencedDocumentInternalAddress(String referencedDocumentInternalAddress) {
            this.referencedDocumentInternalAddress = referencedDocumentInternalAddress;
            return this;
        }

        public Builder languageID(String languageID) {
            this.languageID = languageID;
            return this;
        }

        public Builder localeCode(String localeCode) {
            this.localeCode = localeCode;
            return this;
        }

        public Builder versionID(String versionID) {
            this.versionID = versionID;
            return this;
        }

        public Builder documentStatusCode(String documentStatusCode) {
            this.documentStatusCode = documentStatusCode;
            return this;
        }

        public Builder documentDescription(String documentDescription) {
            this.documentDescription = documentDescription;
            return this;
        }

        public Builder attachment(Attachment attachment) {
            this.attachment = attachment;
            return this;
        }

        public Builder validityPeriod(Period validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder issuerParty(Party issuerParty) {
            this.issuerParty = issuerParty;
            return this;
        }

        public Builder resultOfVerification(ResultOfVerification resultOfVerification) {
            this.resultOfVerification = resultOfVerification;
            return this;
        }

        public DocumentReference build() {
            return new DocumentReference(this);
        }
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public boolean isCopyIndicator() {
        return copyIndicator;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public String getReferencedDocumentInternalAddress() {
        return referencedDocumentInternalAddress;
    }

    public String getLanguageID() {
        return languageID;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getVersionID() {
        return versionID;
    }

    public String getDocumentStatusCode() {
        return documentStatusCode;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public Period getValidityPeriod() {
        return validityPeriod;
    }

    public Party getIssuerParty() {
        return issuerParty;
    }

    public ResultOfVerification getResultOfVerification() {
        return resultOfVerification;
    }

    // Setters for Jackson deserialization
    public void setId(Identifier id) {
        this.id = id;
    }

    public void setCopyIndicator(boolean copyIndicator) {
        this.copyIndicator = copyIndicator;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public void setReferencedDocumentInternalAddress(String referencedDocumentInternalAddress) {
        this.referencedDocumentInternalAddress = referencedDocumentInternalAddress;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public void setDocumentStatusCode(String documentStatusCode) {
        this.documentStatusCode = documentStatusCode;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public void setValidityPeriod(Period validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setIssuerParty(Party issuerParty) {
        this.issuerParty = issuerParty;
    }

    public void setResultOfVerification(ResultOfVerification resultOfVerification) {
        this.resultOfVerification = resultOfVerification;
    }
}