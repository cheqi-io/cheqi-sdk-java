package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TradeFinancing {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "FinancingInstrumentCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code financingInstrumentCode;
    @XmlElement(name = "ContractDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentReference contractDocumentReference;
    @XmlElement(name = "DocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<DocumentReference> documentReference;
    @XmlElement(name = "FinancingParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party financingParty;
    @XmlElement(name = "FinancingFinancialAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private FinancialAccount financingFinancialAccount;
    @XmlElement(name = "Clause", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Clause clause;

    public TradeFinancing() {
    }

    private TradeFinancing(Builder builder) {
        this.id = builder.id;
        this.financingInstrumentCode = builder.financingInstrumentCode;
        this.contractDocumentReference = builder.contractDocumentReference;
        this.documentReference = builder.documentReference;
        this.financingParty = builder.financingParty;
        this.financingFinancialAccount = builder.financingFinancialAccount;
        this.clause = builder.clause;
    }

    public static class Builder {
        private final Party financingParty;
        private Identifier id;
        private Code financingInstrumentCode;
        private DocumentReference contractDocumentReference;
        private List<DocumentReference> documentReference;
        private FinancialAccount financingFinancialAccount;
        private Clause clause;

        public Builder(Party financingParty) {
            this.financingParty = financingParty;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder financingInstrumentCode(Code financingInstrumentCode) {
            this.financingInstrumentCode = financingInstrumentCode;
            return this;
        }

        public Builder contractDocumentReference(DocumentReference contractDocumentReference) {
            this.contractDocumentReference = contractDocumentReference;
            return this;
        }

        public Builder documentReference(List<DocumentReference> documentReference) {
            this.documentReference = documentReference;
            return this;
        }

        public Builder financingFinancialAccount(FinancialAccount financingFinancialAccount) {
            this.financingFinancialAccount = financingFinancialAccount;
            return this;
        }

        public Builder clause(Clause clause) {
            this.clause = clause;
            return this;
        }

        public TradeFinancing build() {
            return new TradeFinancing(this);
        }
    }

    public static Builder builder(Party financingParty) {
        return new Builder(financingParty);
    }

    public Identifier getId() {
        return id;
    }

    public Code getFinancingInstrumentCode() {
        return financingInstrumentCode;
    }

    public DocumentReference getContractDocumentReference() {
        return contractDocumentReference;
    }

    public List<DocumentReference> getDocumentReference() {
        return documentReference;
    }

    public Party getFinancingParty() {
        return financingParty;
    }

    public FinancialAccount getFinancingFinancialAccount() {
        return financingFinancialAccount;
    }

    public Clause getClause() {
        return clause;
    }
}
