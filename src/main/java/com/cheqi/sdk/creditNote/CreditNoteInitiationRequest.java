package com.cheqi.sdk.creditNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plaintext customer return request that is encrypted for the original receipt issuer.
 * Cheqi routes the encrypted payload and cannot validate these fields server-side.
 */
public class CreditNoteInitiationRequest {
    private String cheqiReceiptId;
    private String receiptId;
    private String customerNote;
    private List<ReturnLineItem> lineItems;
    private RefundPreference refundPreference;
    private RefundBankAccount refundBankAccount;

    public CreditNoteInitiationRequest() {
    }

    private CreditNoteInitiationRequest(Builder builder) {
        this.cheqiReceiptId = builder.cheqiReceiptId;
        this.receiptId = builder.receiptId;
        this.customerNote = builder.customerNote;
        this.lineItems = builder.lineItems == null
                ? null
                : Collections.unmodifiableList(new ArrayList<>(builder.lineItems));
        this.refundPreference = builder.refundPreference;
        this.refundBankAccount = builder.refundBankAccount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireText(cheqiReceiptId, "cheqiReceiptId");
        requireText(receiptId, "receiptId");
        if (customerNote != null && customerNote.length() > 1000) {
            throw new IllegalArgumentException("customerNote must be at most 1000 characters");
        }
        if (lineItems == null || lineItems.isEmpty()) {
            throw new IllegalArgumentException("At least one return line item is required");
        }
        lineItems.forEach(ReturnLineItem::validate);
        if (refundPreference == null) {
            throw new IllegalArgumentException("refundPreference is required");
        }
        if (refundPreference == RefundPreference.BANK_TRANSFER) {
            if (refundBankAccount == null) {
                throw new IllegalArgumentException("refundBankAccount is required for BANK_TRANSFER");
            }
            requireText(refundBankAccount.getIban(), "refundBankAccount.iban");
            requireText(refundBankAccount.getAccountHolder(), "refundBankAccount.accountHolder");
        } else if (refundBankAccount != null) {
            throw new IllegalArgumentException("refundBankAccount is only allowed for BANK_TRANSFER");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
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

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
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
        private String customerNote;
        private List<ReturnLineItem> lineItems;
        private RefundPreference refundPreference;
        private RefundBankAccount refundBankAccount;

        public Builder cheqiReceiptId(String cheqiReceiptId) {
            this.cheqiReceiptId = cheqiReceiptId;
            return this;
        }

        public Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder customerNote(String customerNote) {
            this.customerNote = customerNote;
            return this;
        }

        public Builder lineItems(List<ReturnLineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder refundPreference(RefundPreference refundPreference) {
            this.refundPreference = refundPreference;
            return this;
        }

        public Builder refundBankAccount(RefundBankAccount refundBankAccount) {
            this.refundBankAccount = refundBankAccount;
            return this;
        }

        public CreditNoteInitiationRequest build() {
            CreditNoteInitiationRequest request = new CreditNoteInitiationRequest(this);
            request.validate();
            return request;
        }
    }
}
