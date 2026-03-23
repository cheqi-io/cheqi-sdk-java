package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.CheqiCreditNote;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditNoteEnvelope {
    @JsonProperty("cheqiCreditNote")
    private CheqiCreditNote cheqiCreditNote;

    @JsonProperty("ublXml")
    private String ublXml;

    public CreditNoteEnvelope() {
    }

    public CreditNoteEnvelope(CheqiCreditNote cheqiCreditNote, String ublXml) {
        this.cheqiCreditNote = cheqiCreditNote;
        this.ublXml = ublXml;
    }

    public CheqiCreditNote getCheqiCreditNote() {
        return cheqiCreditNote;
    }

    public void setCheqiCreditNote(CheqiCreditNote cheqiCreditNote) {
        this.cheqiCreditNote = cheqiCreditNote;
    }

    public String getUblXml() {
        return ublXml;
    }

    public void setUblXml(String ublXml) {
        this.ublXml = ublXml;
    }
}
