package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ReceiptCreatedResponse {
    @JsonProperty("cheqiReceiptId")
    private String cheqiReceiptId;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("templateHash")
    private String templateHash;

    public ReceiptCreatedResponse() {}

    public ReceiptCreatedResponse(String cheqiReceiptId, Instant createdAt, String templateHash) {
        this.cheqiReceiptId = cheqiReceiptId;
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
}
