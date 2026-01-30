package com.cheqi.sdk.creditNote;

import java.util.List;

public class CreditNoteInitiationRequest {
    private String cheqiReceiptId;
    private String receiptId;
    private String returnReason;
    private String returnReasonDescription;
    private List<ReturnLineItem> lineItems;
    private RefundPreference refundPreference;
    private RefundBankAccount refundBankAccount;

    public CreditNoteInitiationRequest() {
    }

    private CreditNoteInitiationRequest(CreditNoteInitiationRequest.Builder builder) {
        this.cheqiReceiptId = builder.cheqiReceiptId;
        this.receiptId = builder.receiptId;
        this.returnReason = builder.returnReason;
        this.returnReasonDescription = builder.returnReasonDescription;
        this.lineItems = builder.lineItems;
        this.refundPreference = builder.refundPreference;
        this.refundBankAccount = builder.refundBankAccount;
    }

    public static CreditNoteInitiationRequest.Builder builder() {
        return new CreditNoteInitiationRequest.Builder();
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public void setCheqiReceiptId(String cheqiReceiptId) {
        this.cheqiReceiptId = cheqiReceiptId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getReturnReasonDescription() {
        return returnReasonDescription;
    }

    public void setReturnReasonDescription(String returnReasonDescription) {
        this.returnReasonDescription = returnReasonDescription;
    }

    public List<ReturnLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<ReturnLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public RefundPreference getRefundPreference() {
        return refundPreference;
    }

    public void setRefundPreference(RefundPreference refundPreference) {
        this.refundPreference = refundPreference;
    }

    public RefundBankAccount getRefundBankAccount() {
        return refundBankAccount;
    }

    public void setRefundBankAccount(RefundBankAccount refundBankAccount) {
        this.refundBankAccount = refundBankAccount;
    }

    public static class Builder {
        private String cheqiReceiptId;
        private String receiptId;
        private String returnReason;
        private String returnReasonDescription;
        private List<ReturnLineItem> lineItems;
        private RefundPreference refundPreference;
        private RefundBankAccount refundBankAccount;

        public CreditNoteInitiationRequest.Builder cheqiReceiptId(String cheqiReceiptId) {
            this.cheqiReceiptId = cheqiReceiptId;
            return this;
        }

        public CreditNoteInitiationRequest.Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public CreditNoteInitiationRequest.Builder returnReason(String returnReason) {
            this.returnReason = returnReason;
            return this;
        }

        public CreditNoteInitiationRequest.Builder returnReasonDescription(String returnReasonDescription) {
            this.returnReasonDescription = returnReasonDescription;
            return this;
        }

        public CreditNoteInitiationRequest.Builder lineItems(List<ReturnLineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public CreditNoteInitiationRequest.Builder refundPreference(RefundPreference refundPreference) {
            this.refundPreference = refundPreference;
            return this;
        }

        public CreditNoteInitiationRequest.Builder refundBankAccount(RefundBankAccount refundBankAccount) {
            this.refundBankAccount = refundBankAccount;
            return this;
        }

        public CreditNoteInitiationRequest build() {
            return new CreditNoteInitiationRequest(this);
        }
    }
}
