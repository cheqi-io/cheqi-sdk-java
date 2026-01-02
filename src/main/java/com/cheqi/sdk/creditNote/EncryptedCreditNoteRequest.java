package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedCreditNoteRequest {
    @JsonProperty("cheqiReceiptId")
    private String cheqiReceiptId;
    @JsonProperty("publicKey")
    private String publicKey;
    @JsonProperty("encryptedCreditRequest")
    private String encryptedCreditRequest;
    @JsonProperty("encryptedSymmetricKey")
    private String encryptedSymmetricKey;

    public EncryptedCreditNoteRequest() {
    }

    public EncryptedCreditNoteRequest(String cheqiReceiptId, String publicKey, 
                                     String encryptedCreditRequest, String encryptedSymmetricKey) {
        this.cheqiReceiptId = cheqiReceiptId;
        this.publicKey = publicKey;
        this.encryptedCreditRequest = encryptedCreditRequest;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public void setCheqiReceiptId(String cheqiReceiptId) {
        this.cheqiReceiptId = cheqiReceiptId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getEncryptedCreditRequest() {
        return encryptedCreditRequest;
    }

    public void setEncryptedCreditRequest(String encryptedCreditRequest) {
        this.encryptedCreditRequest = encryptedCreditRequest;
    }

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public void setEncryptedSymmetricKey(String encryptedSymmetricKey) {
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }
}