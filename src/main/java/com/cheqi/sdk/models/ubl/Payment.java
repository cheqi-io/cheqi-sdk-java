package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;
import java.time.LocalTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class Payment {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "PaidAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount paidAmount;
    @XmlElement(name = "PaidCashAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount paidCashAmount;
    @XmlElement(name = "CashChangeAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Amount cashChangeAmount;
    @XmlElement(name = "ReceivedDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate receivedDate;
    @XmlElement(name = "PaidDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalDate paidDate;
    @XmlElement(name = "PaidTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private LocalTime paidTime;
    @XmlElement(name = "InstructionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier instructionID;
    @XmlElement(name = "MerchantID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier merchantID;
    @XmlElement(name = "AuthorizationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier authorizationID;
    @XmlElement(name = "TransactionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier transactionID;
    @XmlElement(name = "paymentTerminalID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier paymentTerminalID;

    public Payment() {
    }

    private Payment(Builder builder) {
        this.id = builder.id;
        this.paidAmount = builder.paidAmount;
        this.paidCashAmount = builder.paidCashAmount;
        this.cashChangeAmount = builder.cashChangeAmount;
        this.receivedDate = builder.receivedDate;
        this.paidDate = builder.paidDate;
        this.paidTime = builder.paidTime;
        this.instructionID = builder.instructionID;
        this.merchantID = builder.merchantID;
        this.authorizationID = builder.authorizationID;
        this.transactionID = builder.transactionID;
        this.paymentTerminalID = builder.paymentTerminalID;
    }

    public static class Builder {
        private Identifier id;
        private Amount paidAmount;
        private Amount paidCashAmount;
        private Amount cashChangeAmount;
        private LocalDate receivedDate;
        private LocalDate paidDate;
        private LocalTime paidTime;
        private Identifier instructionID;
        private Identifier merchantID;
        private Identifier authorizationID;
        private Identifier transactionID;
        private Identifier paymentTerminalID;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder paidAmount(Amount paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public Builder paidCashAmount(Amount paidCashAmount) {
            this.paidCashAmount = paidCashAmount;
            return this;
        }

        public Builder cashChangeAmount(Amount cashChangeAmount) {
            this.cashChangeAmount = cashChangeAmount;
            return this;
        }

        public Builder receivedDate(LocalDate receivedDate) {
            this.receivedDate = receivedDate;
            return this;
        }

        public Builder paidDate(LocalDate paidDate) {
            this.paidDate = paidDate;
            return this;
        }

        public Builder paidTime(LocalTime paidTime) {
            this.paidTime = paidTime;
            return this;
        }

        public Builder instructionID(Identifier instructionID) {
            this.instructionID = instructionID;
            return this;
        }

        public Builder merchantID(Identifier merchantID) {
            this.merchantID = merchantID;
            return this;
        }

        public Builder authorizationID(Identifier authorizationID) {
            this.authorizationID = authorizationID;
            return this;
        }

        public Builder transactionID(Identifier transactionID) {
            this.transactionID = transactionID;
            return this;
        }

        public Builder paymentTerminalID(Identifier paymentTerminalID) {
            this.paymentTerminalID = paymentTerminalID;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Amount getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Amount paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Amount getPaidCashAmount() {
        return paidCashAmount;
    }

    public void setPaidCashAmount(Amount paidCashAmount) {
        this.paidCashAmount = paidCashAmount;
    }

    public Amount getCashChangeAmount() {
        return cashChangeAmount;
    }

    public void setCashChangeAmount(Amount cashChangeAmount) {
        this.cashChangeAmount = cashChangeAmount;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public LocalTime getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(LocalTime paidTime) {
        this.paidTime = paidTime;
    }

    public Identifier getInstructionID() {
        return instructionID;
    }

    public void setInstructionID(Identifier instructionID) {
        this.instructionID = instructionID;
    }

    public Identifier getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(Identifier merchantID) {
        this.merchantID = merchantID;
    }

    public Identifier getAuthorizationID() {
        return authorizationID;
    }

    public void setAuthorizationID(Identifier authorizationID) {
        this.authorizationID = authorizationID;
    }

    public Identifier getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Identifier transactionID) {
        this.transactionID = transactionID;
    }

    public Identifier getPaymentTerminalID() {
        return paymentTerminalID;
    }

    public void setPaymentTerminalID(Identifier paymentTerminalID) {
        this.paymentTerminalID = paymentTerminalID;
    }
}
