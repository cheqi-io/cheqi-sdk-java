package com.cheqi.sdk.models.ubl;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "CreditNote", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreditNote {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueDate;
    @XmlElement(name = "IssueTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String issueTime;
    @XmlElement(name = "DocumentCurrencyCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String documentCurrencyCode;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String note;
    @XmlElement(name = "CreditNoteTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code creditNoteTypeCode;
    @XmlElement(name = "AccountingSupplierParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party accountingSupplierParty;
    @XmlElement(name = "AccountingCustomerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party accountingCustomerParty;
    @XmlElement(name = "LegalMonetaryTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private MonetaryTotal legalMonetaryTotal;
    @XmlElement(name = "OriginatorDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<DocumentReference> originatorDocumentReference;
    @XmlElement(name = "CreditNoteLine", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<CreditNoteLine> creditNoteLine;
    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TaxTotal> taxTotal;
    @XmlElement(name = "VerificationNonce", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String verificationNonce;

    public CreditNote() {
    }

    private CreditNote(Builder builder) {
        this.id = builder.id;
        this.issueDate = builder.issueDate;
        this.issueTime = builder.issueTime;
        this.accountingSupplierParty = builder.accountingSupplierParty;
        this.legalMonetaryTotal = builder.legalMonetaryTotal;
        this.creditNoteLine = builder.creditNoteLine;
        this.documentCurrencyCode = builder.documentCurrencyCode;
        this.note = builder.note;
        this.creditNoteTypeCode = builder.creditNoteTypeCode;
        this.accountingCustomerParty = builder.accountingCustomerParty;
        this.originatorDocumentReference = builder.originatorDocumentReference;
        this.taxTotal = builder.taxTotal;
        this.verificationNonce = builder.verificationNonce;
    }

    public static class Builder {
        // Mandatory fields
        private final Identifier id;
        private final String issueDate;
        private final String issueTime;
        private final Party accountingSupplierParty;
        private final MonetaryTotal legalMonetaryTotal;
        private final List<CreditNoteLine>creditNoteLine;

        // Optional fields
        private String documentCurrencyCode;
        private String note;
        private Code creditNoteTypeCode;
        private Party accountingCustomerParty;
        private List<DocumentReference> originatorDocumentReference;
        private List<TaxTotal> taxTotal;
        private String verificationNonce;

        public Builder(Identifier id, String issueDate, String issueTime, Party accountingSupplierParty, String documentCurrencyCode,
                       MonetaryTotal legalMonetaryTotal, List<CreditNoteLine> creditNoteLine) {
            this.id = id;
            this.issueDate = issueDate;
            this.issueTime = issueTime;
            this.documentCurrencyCode = documentCurrencyCode;
            this.accountingSupplierParty = accountingSupplierParty;
            this.legalMonetaryTotal = legalMonetaryTotal;
            this.creditNoteLine = creditNoteLine;
        }

        public Builder documentCurrencyCode(String documentCurrencyCode) {
            this.documentCurrencyCode = documentCurrencyCode;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder creditNoteTypeCode(Code creditNoteTypeCode) {
            this.creditNoteTypeCode = creditNoteTypeCode;
            return this;
        }

        public Builder accountingCustomerParty(Party accountingCustomerParty) {
            this.accountingCustomerParty = accountingCustomerParty;
            return this;
        }

        public Builder originatorDocumentReference(List<DocumentReference> originatorDocumentReference) {
            this.originatorDocumentReference = originatorDocumentReference;
            return this;
        }

        public Builder taxTotal(List<TaxTotal> taxTotal) {
            this.taxTotal = taxTotal;
            return this;
        }

        public Builder verificationNonce(String verificationNonce) {
            this.verificationNonce = verificationNonce;
            return this;
        }

        public CreditNote build() {
            return new CreditNote(this);
        }
    }

    public void setVerificationNonce(String verificationNonce) {
        this.verificationNonce = verificationNonce;
    }

    public Identifier getId() {
        return id;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public String getDocumentCurrencyCode() {
        return documentCurrencyCode;
    }

    public String getNote() {
        return note;
    }

    public Code getCreditNoteTypeCode() {
        return creditNoteTypeCode;
    }

    public Party getAccountingSupplierParty() {
        return accountingSupplierParty;
    }

    public Party getAccountingCustomerParty() {
        return accountingCustomerParty;
    }

    public MonetaryTotal getLegalMonetaryTotal() {
        return legalMonetaryTotal;
    }

    public List<DocumentReference> getOriginatorDocumentReference() {
        return originatorDocumentReference;
    }

    public List<CreditNoteLine> getCreditNoteLine() {
        return creditNoteLine;
    }

    public List<TaxTotal> getTaxTotal() {
        return taxTotal;
    }

    public String getVerificationNonce() {
        return verificationNonce;
    }

    @Override
    public String toString() {
        return "CreditNote{" +
                "id='" + id + '\'' +
                ", issueDate=" + issueDate +
                ", issueTime=" + issueTime +
                ", documentCurrencyCode=" + documentCurrencyCode +
                ", note='" + note + '\'' +
                ", creditNoteTypeCode=" + creditNoteTypeCode +
                ", accountingSupplierParty=" + accountingSupplierParty +
                ", accountingCustomerParty=" + accountingCustomerParty +
                ", legalMonetaryTotal=" + legalMonetaryTotal +
                ", originatorDocumentReference=" + (originatorDocumentReference.toString()) +
                ", creditNoteLine=" + creditNoteLine.toString() +
                ", taxTotal=" + taxTotal.toString() +
                ", verificationNonce='" + verificationNonce + '\'' +
                '}';
    }
}