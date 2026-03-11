package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Certificate {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Identifier id;
    @XmlElement(name = "CertificateTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String certificateTypeCode;
    @XmlElement(name = "CertificateType", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> certificateType;
    @XmlElement(name = "Remarks", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> remarks;
    @XmlElement(name = "IssuerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party issuerParty;
    @XmlElement(name = "DocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<DocumentReference> documentReference;
    @XmlElement(name = "Signature", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Signature> signature;

    public Certificate() {
    }

    private Certificate(Builder builder) {
        this.id = builder.id;
        this.certificateTypeCode = builder.certificateTypeCode;
        this.certificateType = builder.certificateType;
        this.remarks = builder.remarks;
        this.issuerParty = builder.issuerParty;
        this.documentReference = builder.documentReference;
        this.signature = builder.signature;
    }

    public static class Builder {
        private final Identifier id;
        private String certificateTypeCode;
        private List<String> certificateType;
        private List<String> remarks;
        private Party issuerParty;
        private List<DocumentReference> documentReference;
        private List<Signature> signature;

        public Builder(Identifier id) {
            if (id == null) {
                throw new IllegalArgumentException("ID is mandatory and cannot be null");
            }
            this.id = id;
        }

        public Builder certificateTypeCode(String certificateTypeCode) {
            this.certificateTypeCode = certificateTypeCode;
            return this;
        }

        public Builder certificateType(List<String> certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public Builder addCertificateType(String type) {
            if (this.certificateType == null) {
                this.certificateType = new ArrayList<>();
            }
            this.certificateType.add(type);
            return this;
        }

        public Builder remarks(List<String> remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder addRemark(String remark) {
            if (this.remarks == null) {
                this.remarks = new ArrayList<>();
            }
            this.remarks.add(remark);
            return this;
        }

        public Builder issuerParty(Party issuerParty) {
            this.issuerParty = issuerParty;
            return this;
        }

        public Builder documentReference(List<DocumentReference> documentReference) {
            this.documentReference = documentReference;
            return this;
        }

        public Builder addDocumentReference(DocumentReference reference) {
            if (this.documentReference == null) {
                this.documentReference = new ArrayList<>();
            }
            this.documentReference.add(reference);
            return this;
        }

        public Builder signature(List<Signature> signature) {
            this.signature = signature;
            return this;
        }

        public Builder addSignature(Signature sig) {
            if (this.signature == null) {
                this.signature = new ArrayList<>();
            }
            this.signature.add(sig);
            return this;
        }

        public Certificate build() {
            return new Certificate(this);
        }
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public String getCertificateTypeCode() {
        return certificateTypeCode;
    }

    public List<String> getCertificateType() {
        return certificateType;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public Party getIssuerParty() {
        return issuerParty;
    }

    public List<DocumentReference> getDocumentReference() {
        return documentReference;
    }

    public List<Signature> getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", certificateTypeCode='" + certificateTypeCode + '\'' +
                ", certificateType=" + certificateType +
                ", remarks=" + remarks +
                ", issuerParty=" + issuerParty +
                ", documentReference=" + documentReference +
                ", signature=" + signature +
                '}';
    }
}
