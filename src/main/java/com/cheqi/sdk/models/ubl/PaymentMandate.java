package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PaymentMandate {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "MandateTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code mandateTypeCode;
    @XmlElement(name = "MaximumPaymentInstructionsNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private double MaximumPaymentInstructionsNumeric;
    @XmlElement(name = "MaximumPaidAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount maximumPaidAmount;
    @XmlElement(name = "SignatureID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier signatureID;
    @XmlElement(name = "PayerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party payerParty;
    @XmlElement(name = "PayerFinancialAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private FinancialAccount payerFinancialAccount;
    @XmlElement(name = "ValidityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Period validityPeriod;
    @XmlElement(name = "PaymentReversalPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Period paymentReversalPeriod;
    @XmlElement(name = "Clause", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Clause clause;

    public PaymentMandate() {
    }

    private PaymentMandate(Builder builder) {
        this.id = builder.id;
        this.mandateTypeCode = builder.mandateTypeCode;
        this.MaximumPaymentInstructionsNumeric = builder.MaximumPaymentInstructionsNumeric;
        this.maximumPaidAmount = builder.maximumPaidAmount;
        this.signatureID = builder.signatureID;
        this.payerParty = builder.payerParty;
        this.payerFinancialAccount = builder.payerFinancialAccount;
        this.validityPeriod = builder.validityPeriod;
        this.paymentReversalPeriod = builder.paymentReversalPeriod;
        this.clause = builder.clause;
    }

    public static class Builder {
        private Identifier id;
        private Code mandateTypeCode;
        private double MaximumPaymentInstructionsNumeric;
        private Amount maximumPaidAmount;
        private Identifier signatureID;
        private Party payerParty;
        private FinancialAccount payerFinancialAccount;
        private Period validityPeriod;
        private Period paymentReversalPeriod;
        private Clause clause;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder mandateTypeCode(Code mandateTypeCode) {
            this.mandateTypeCode = mandateTypeCode;
            return this;
        }

        public Builder MaximumPaymentInstructionsNumeric(double MaximumPaymentInstructionsNumeric) {
            this.MaximumPaymentInstructionsNumeric = MaximumPaymentInstructionsNumeric;
            return this;
        }

        public Builder maximumPaidAmount(Amount maximumPaidAmount) {
            this.maximumPaidAmount = maximumPaidAmount;
            return this;
        }

        public Builder signatureID(Identifier signatureID) {
            this.signatureID = signatureID;
            return this;
        }

        public Builder payerParty(Party payerParty) {
            this.payerParty = payerParty;
            return this;
        }

        public Builder payerFinancialAccount(FinancialAccount payerFinancialAccount) {
            this.payerFinancialAccount = payerFinancialAccount;
            return this;
        }

        public Builder validityPeriod(Period validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder paymentReversalPeriod(Period paymentReversalPeriod) {
            this.paymentReversalPeriod = paymentReversalPeriod;
            return this;
        }

        public Builder clause(Clause clause) {
            this.clause = clause;
            return this;
        }

        public PaymentMandate build() {
            return new PaymentMandate(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public Code getMandateTypeCode() {
        return mandateTypeCode;
    }

    public double getMaximumPaymentInstructionsNumeric() {
        return MaximumPaymentInstructionsNumeric;
    }

    public Amount getMaximumPaidAmount() {
        return maximumPaidAmount;
    }

    public Identifier getSignatureID() {
        return signatureID;
    }

    public Party getPayerParty() {
        return payerParty;
    }

    public FinancialAccount getPayerFinancialAccount() {
        return payerFinancialAccount;
    }

    public Period getValidityPeriod() {
        return validityPeriod;
    }

    public Period getPaymentReversalPeriod() {
        return paymentReversalPeriod;
    }

    public Clause getClause() {
        return clause;
    }
}
