package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiptTemplateResponse {
    @JsonProperty("ubl")
    private String ubl;
    @JsonProperty("cheqi")
    private CheqiReceipt cheqi;

    public ReceiptTemplateResponse() {
    }

    public ReceiptTemplateResponse(String ubl, CheqiReceipt cheqi) {
        this.ubl = ubl;
        this.cheqi = cheqi;
    }

    public String getUbl() {
        return ubl;
    }

    public void setUbl(String ubl) {
        this.ubl = ubl;
    }

    public CheqiReceipt getCheqi() {
        return cheqi;
    }

    public void setCheqi(CheqiReceipt cheqi) {
        this.cheqi = cheqi;
    }
}
