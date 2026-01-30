package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Signature {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "ReasonCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code reasonCode;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> note;
    @XmlElement(name = "ValidationDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate validationDate;
    @XmlElement(name = "ValidationTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalTime validationTime;
    @XmlElement(name = "ValidatorID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier validatorID;
    @XmlElement(name = "CanonicalizationMethod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String canonicalizationMethod;
    @XmlElement(name = "signatureMethod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String signatureMethod;
    @XmlElement(name = "SignatoryParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party signatoryParty;
    @XmlElement(name = "DigitalSignatureAttachment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Attachment digitalSignatureAttachment;
    @XmlElement(name = "OriginalDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentReference documentReference;

    public Signature() {
    }

    private Signature(Builder builder) {
        this.id = builder.id;
        this.reasonCode = builder.reasonCode;
        this.note = builder.note;
        this.validationDate = builder.validationDate;
        this.validationTime = builder.validationTime;
        this.validatorID = builder.validatorID;
        this.canonicalizationMethod = builder.canonicalizationMethod;
        this.signatureMethod = builder.signatureMethod;
        this.signatoryParty = builder.signatoryParty;
        this.digitalSignatureAttachment = builder.digitalSignatureAttachment;
        this.documentReference = builder.documentReference;
    }

    public static class Builder {
        private final Identifier id;
        private Code reasonCode;
        private List<String> note = new ArrayList<>();
        private LocalDate validationDate;
        private LocalTime validationTime;
        private Identifier validatorID;
        private String canonicalizationMethod;
        private String signatureMethod;
        private Party signatoryParty;
        private Attachment digitalSignatureAttachment;
        private DocumentReference documentReference;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder reasonCode(Code reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }

        public Builder note(String note) {
            this.note.add(note);
            return this;
        }

        public Builder validationDate(LocalDate validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        public Builder validationTime(LocalTime validationTime) {
            this.validationTime = validationTime;
            return this;
        }

        public Builder validatorID(Identifier validatorID) {
            this.validatorID = validatorID;
            return this;
        }

        public Builder canonicalizationMethod(String canonicalizationMethod) {
            this.canonicalizationMethod = canonicalizationMethod;
            return this;
        }

        public Builder signatureMethod(String signatureMethod) {
            this.signatureMethod = signatureMethod;
            return this;
        }

        public Builder signatoryParty(Party signatoryParty) {
            this.signatoryParty = signatoryParty;
            return this;
        }

        public Builder digitalSignatureAttachment(Attachment digitalSignatureAttachment) {
            this.digitalSignatureAttachment = digitalSignatureAttachment;
            return this;
        }

        public Builder documentReference(DocumentReference documentReference) {
            this.documentReference = documentReference;
            return this;
        }

        public Signature build() {
            return new Signature(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public Code getReasonCode() {
        return reasonCode;
    }

    public List<String> getNote() {
        return note;
    }

    public LocalDate getValidationDate() {
        return validationDate;
    }

    public LocalTime getValidationTime() {
        return validationTime;
    }

    public Identifier getValidatorID() {
        return validatorID;
    }

    public String getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public Party getSignatoryParty() {
        return signatoryParty;
    }

    public Attachment getDigitalSignatureAttachment() {
        return digitalSignatureAttachment;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }
}