package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CreditNoteCreatedResponse {
    @JsonProperty("cheqiReceiptId")
    private String cheqiReceiptId;

    @JsonProperty("parentCheqiReceiptId")
    private String parentCheqiReceiptId;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("templateHash")
    private String templateHash;

    public CreditNoteCreatedResponse() {}

    public CreditNoteCreatedResponse(String cheqiReceiptId, String parentCheqiReceiptId, Instant createdAt, String templateHash) {
        this.cheqiReceiptId = cheqiReceiptId;
        this.parentCheqiReceiptId = parentCheqiReceiptId;
        this.createdAt = createdAt;
        this.templateHash = templateHash;
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public void setCheqiReceiptId(String cheqiReceiptId) {
        this.cheqiReceiptId = cheqiReceiptId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getTemplateHash() {
        return templateHash;
    }

    public void setTemplateHash(String templateHash) {
        this.templateHash = templateHash;
    }

    public String getParentCheqiReceiptId() {
        return parentCheqiReceiptId;
    }

    public void setParentCheqiReceiptId(String parentCheqiReceiptId) {
        this.parentCheqiReceiptId = parentCheqiReceiptId;
    }
}
