package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditNoteTemplateResponse {
    @JsonProperty("ubl")
    private String ubl;

    @JsonProperty("cheqi")
    private CheqiCreditNote cheqi;

    public CreditNoteTemplateResponse() {
    }

    public CreditNoteTemplateResponse(String ubl, CheqiCreditNote cheqi) {
        this.ubl = ubl;
        this.cheqi = cheqi;
    }

    public String getUbl() {
        return ubl;
    }

    public void setUbl(String ubl) {
        this.ubl = ubl;
    }

    public CheqiCreditNote getCheqi() {
        return cheqi;
    }

    public void setCheqi(CheqiCreditNote cheqi) {
        this.cheqi = cheqi;
    }
}
