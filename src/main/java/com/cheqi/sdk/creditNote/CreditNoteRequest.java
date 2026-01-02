package com.cheqi.sdk.creditNote;

import java.util.List;

public class CreditNoteRequest {
    private String cheqiReceiptId;
    private String receiptId;
    private String returnReason;
    private String returnReasonDescription;
    private List<ReturnLineItem> lineItems;
    private RefundPreference refundPreference;
    private RefundBankAccount refundBankAccount;

    public CreditNoteRequest() {
    }

    public CreditNoteRequest(String cheqiReceiptId, String receiptId, String returnReason,
                            String returnReasonDescription, List<ReturnLineItem> lineItems,
                            RefundPreference refundPreference, RefundBankAccount refundBankAccount) {
        this.cheqiReceiptId = cheqiReceiptId;
        this.receiptId = receiptId;
        this.returnReason = returnReason;
        this.returnReasonDescription = returnReasonDescription;
        this.lineItems = lineItems;
        this.refundPreference = refundPreference;
        this.refundBankAccount = refundBankAccount;
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
}