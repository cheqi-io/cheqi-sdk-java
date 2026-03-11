package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class PaymentMeans {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "PaymentMeansCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code paymentMeansCode;
    @XmlElement(name = "PaymentMeansDescription", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> paymentMeansDescription;
    @XmlElement(name = "PaymentID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<Identifier> paymentID;
    @XmlElement(name = "PaymentDueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate paymentDueDate;
    @XmlElement(name = "PaymentChannelCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code paymentChannelCode;
    @XmlElement(name = "InstructionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier instructionID;
    @XmlElement(name = "InstructionNote", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> instructionNote;
    @XmlElement(name = "ChargeBearerCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code chargeBearerCode;
    @XmlElement(name = "ServiceLevelCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code serviceLevelCode;
    @XmlElement(name = "CardAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<CardAccount> cardAccount;
    @XmlElement(name = "PayerFinancialAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private FinancialAccount payerFinancialAccount;
    @XmlElement(name = "PayeeFinancialAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private FinancialAccount payeeFinancialAccount;
    @XmlElement(name = "CreditAccount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private CreditAccount creditAccount;
    @XmlElement(name = "PaymentMandate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private PaymentMandate paymentMandate;
    @XmlElement(name = "TradeFinancing", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private TradeFinancing tradeFinancing;
    @XmlElement(name = "RemittanceDocumentDistribution", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentDistribution remittanceDocumentDistribution;

    public PaymentMeans() {
    }

    private PaymentMeans(Builder builder) {
        this.id = builder.id;
        this.paymentMeansCode = builder.paymentMeansCode;
        this.paymentMeansDescription = builder.paymentMeansDescription;
        this.paymentID = builder.paymentID;
        this.paymentDueDate = builder.paymentDueDate;
        this.paymentChannelCode = builder.paymentChannelCode;
        this.instructionID = builder.instructionID;
        this.instructionNote = builder.instructionNote;
        this.chargeBearerCode = builder.chargeBearerCode;
        this.serviceLevelCode = builder.serviceLevelCode;
        this.cardAccount = builder.cardAccount;
        this.payerFinancialAccount = builder.payerFinancialAccount;
        this.payeeFinancialAccount = builder.payeeFinancialAccount;
        this.creditAccount = builder.creditAccount;
        this.paymentMandate = builder.paymentMandate;
        this.tradeFinancing = builder.tradeFinancing;
        this.remittanceDocumentDistribution = builder.remittanceDocumentDistribution;
    }

    public static class Builder {
        private final Code paymentMeansCode;
        private Identifier id;
        private List<String> paymentMeansDescription;
        private List<Identifier> paymentID;
        private LocalDate paymentDueDate;
        private Code paymentChannelCode;
        private Identifier instructionID;
        private List<String> instructionNote;
        private Code chargeBearerCode;
        private Code serviceLevelCode;
        private List<CardAccount> cardAccount;
        private FinancialAccount payerFinancialAccount;
        private FinancialAccount payeeFinancialAccount;
        private CreditAccount creditAccount;
        private PaymentMandate paymentMandate;
        private TradeFinancing tradeFinancing;
        private DocumentDistribution remittanceDocumentDistribution;

        public Builder(Code paymentMeansCode) {
            this.paymentMeansCode = paymentMeansCode;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder paymentMeansDescription(List<String> paymentMeansDescription) {
            this.paymentMeansDescription = paymentMeansDescription;
            return this;
        }

        public Builder paymentID(List<Identifier> paymentID) {
            this.paymentID = paymentID;
            return this;
        }

        public Builder paymentDueDate(LocalDate paymentDueDate) {
            this.paymentDueDate = paymentDueDate;
            return this;
        }

        public Builder paymentChannelCode(Code paymentChannelCode) {
            this.paymentChannelCode = paymentChannelCode;
            return this;
        }

        public Builder instructionID(Identifier instructionID) {
            this.instructionID = instructionID;
            return this;
        }

        public Builder instructionNote(List<String> instructionNote) {
            this.instructionNote = instructionNote;
            return this;
        }

        public Builder chargeBearerCode(Code chargeBearerCode) {
            this.chargeBearerCode = chargeBearerCode;
            return this;
        }

        public Builder serviceLevelCode(Code serviceLevelCode) {
            this.serviceLevelCode = serviceLevelCode;
            return this;
        }

        public Builder cardAccount(List<CardAccount> cardAccount) {
            this.cardAccount = cardAccount;
            return this;
        }

        public Builder payerFinancialAccount(FinancialAccount payerFinancialAccount) {
            this.payerFinancialAccount = payerFinancialAccount;
            return this;
        }

        public Builder payeeFinancialAccount(FinancialAccount payeeFinancialAccount) {
            this.payeeFinancialAccount = payeeFinancialAccount;
            return this;
        }

        public Builder creditAccount(CreditAccount creditAccount) {
            this.creditAccount = creditAccount;
            return this;
        }

        public Builder paymentMandate(PaymentMandate paymentMandate) {
            this.paymentMandate = paymentMandate;
            return this;
        }

        public Builder tradeFinancing(TradeFinancing tradeFinancing) {
            this.tradeFinancing = tradeFinancing;
            return this;
        }

        public Builder remittanceDocumentDistribution(DocumentDistribution remittanceDocumentDistribution) {
            this.remittanceDocumentDistribution = remittanceDocumentDistribution;
            return this;
        }

        public PaymentMeans build() {
            return new PaymentMeans(this);
        }
    }

    public static Builder builder(Code paymentMeansCode) {
        return new Builder(paymentMeansCode);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Code getPaymentMeansCode() {
        return paymentMeansCode;
    }

    public void setPaymentMeansCode(Code paymentMeansCode) {
        this.paymentMeansCode = paymentMeansCode;
    }

    public List<String> getPaymentMeansDescription() {
        return paymentMeansDescription;
    }

    public void setPaymentMeansDescription(List<String> paymentMeansDescription) {
        this.paymentMeansDescription = paymentMeansDescription;
    }

    public List<Identifier> getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(List<Identifier> paymentID) {
        this.paymentID = paymentID;
    }

    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public Code getPaymentChannelCode() {
        return paymentChannelCode;
    }

    public void setPaymentChannelCode(Code paymentChannelCode) {
        this.paymentChannelCode = paymentChannelCode;
    }

    public Identifier getInstructionID() {
        return instructionID;
    }

    public void setInstructionID(Identifier instructionID) {
        this.instructionID = instructionID;
    }

    public List<String> getInstructionNote() {
        return instructionNote;
    }

    public void setInstructionNote(List<String> instructionNote) {
        this.instructionNote = instructionNote;
    }

    public Code getChargeBearerCode() {
        return chargeBearerCode;
    }

    public void setChargeBearerCode(Code chargeBearerCode) {
        this.chargeBearerCode = chargeBearerCode;
    }

    public Code getServiceLevelCode() {
        return serviceLevelCode;
    }

    public void setServiceLevelCode(Code serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
    }

    public List<CardAccount> getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(List<CardAccount> cardAccount) {
        this.cardAccount = cardAccount;
    }

    public FinancialAccount getPayerFinancialAccount() {
        return payerFinancialAccount;
    }

    public void setPayerFinancialAccount(FinancialAccount payerFinancialAccount) {
        this.payerFinancialAccount = payerFinancialAccount;
    }

    public FinancialAccount getPayeeFinancialAccount() {
        return payeeFinancialAccount;
    }

    public void setPayeeFinancialAccount(FinancialAccount payeeFinancialAccount) {
        this.payeeFinancialAccount = payeeFinancialAccount;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    public PaymentMandate getPaymentMandate() {
        return paymentMandate;
    }

    public void setPaymentMandate(PaymentMandate paymentMandate) {
        this.paymentMandate = paymentMandate;
    }

    public TradeFinancing getTradeFinancing() {
        return tradeFinancing;
    }

    public void setTradeFinancing(TradeFinancing tradeFinancing) {
        this.tradeFinancing = tradeFinancing;
    }

    public DocumentDistribution getRemittanceDocumentDistribution() {
        return remittanceDocumentDistribution;
    }

    public void setRemittanceDocumentDistribution(DocumentDistribution remittanceDocumentDistribution) {
        this.remittanceDocumentDistribution = remittanceDocumentDistribution;
    }
}
