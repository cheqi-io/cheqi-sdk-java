package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.CheqiCreditNote;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditNoteEnvelope {
    @JsonProperty("cheqiCreditNote")
    private CheqiCreditNote cheqiCreditNote;

    @JsonProperty("ublPurchaseReceipt")
    private String ublPurchaseReceipt;

    public CreditNoteEnvelope() {
    }

    public CreditNoteEnvelope(CheqiCreditNote cheqiCreditNote, String ublPurchaseReceipt) {
        this.cheqiCreditNote = cheqiCreditNote;
        this.ublPurchaseReceipt = ublPurchaseReceipt;
    }

    public CheqiCreditNote getCheqiCreditNote() {
        return cheqiCreditNote;
    }

    public void setCheqiCreditNote(CheqiCreditNote cheqiCreditNote) {
        this.cheqiCreditNote = cheqiCreditNote;
    }

    public String getUblPurchaseReceipt() {
        return ublPurchaseReceipt;
    }

    public void setUblPurchaseReceipt(String ublPurchaseReceipt) {
        this.ublPurchaseReceipt = ublPurchaseReceipt;
    }
}
