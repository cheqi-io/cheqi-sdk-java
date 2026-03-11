package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsumingPartyEnvelope {
    @JsonProperty("receivingParty")
    private ReceivingParty receivingParty;
    @JsonProperty("xmlParty")
    private String xmlParty;

    public ConsumingPartyEnvelope() {
    }

    public ConsumingPartyEnvelope(ReceivingParty receivingParty, String xmlParty) {
        this.receivingParty = receivingParty;
        this.xmlParty = xmlParty;
    }

    public ReceivingParty getReceivingParty() {
        return receivingParty;
    }

    public void setReceivingParty(ReceivingParty receivingParty) {
        this.receivingParty = receivingParty;
    }

    public String getXmlParty() {
        return xmlParty;
    }

    public void setXmlParty(String xmlParty) {
        this.xmlParty = xmlParty;
    }
}
