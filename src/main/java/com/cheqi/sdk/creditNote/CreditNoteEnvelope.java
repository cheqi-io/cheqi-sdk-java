package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.CheqiCreditNote;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditNoteEnvelope {
    @JsonProperty("cheqiCreditNote")
    private CheqiCreditNote cheqiCreditNote;

    @JsonProperty("ublCreditNote")
    private String ublCreditNote;

    public CreditNoteEnvelope() {
    }

    public CreditNoteEnvelope(CheqiCreditNote cheqiCreditNote, String ublCreditNote) {
        this.cheqiCreditNote = cheqiCreditNote;
        this.ublCreditNote = ublCreditNote;
    }

    public CheqiCreditNote getCheqiCreditNote() {
        return cheqiCreditNote;
    }

    public void setCheqiCreditNote(CheqiCreditNote cheqiCreditNote) {
        this.cheqiCreditNote = cheqiCreditNote;
    }

    public String getUblCreditNote() {
        return ublCreditNote;
    }

    public void setUblCreditNote(String ublCreditNote) {
        this.ublCreditNote = ublCreditNote;
    }
}
