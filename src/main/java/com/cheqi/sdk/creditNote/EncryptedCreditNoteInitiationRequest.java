package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedCreditNoteInitiationRequest {
    @JsonProperty("cheqiReceiptId")
    private String cheqiReceiptId;
    @JsonProperty("publicKey")
    private String publicKey;
    @JsonProperty("encryptedCreditNoteInitiationRequest")
    private String encryptedCreditNoteInitiationRequest;
    @JsonProperty("encryptedSymmetricKey")
    private String encryptedSymmetricKey;

    public EncryptedCreditNoteInitiationRequest() {
    }

    private EncryptedCreditNoteInitiationRequest(EncryptedCreditNoteInitiationRequest.Builder builder) {
        this.cheqiReceiptId = builder.cheqiReceiptId;
        this.publicKey = builder.publicKey;
        this.encryptedCreditNoteInitiationRequest = builder.encryptedCreditNoteInitiationRequest;
        this.encryptedSymmetricKey = builder.encryptedSymmetricKey;
    }

    public static EncryptedCreditNoteInitiationRequest.Builder builder() {
        return new EncryptedCreditNoteInitiationRequest.Builder();
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

    public String getEncryptedCreditNoteInitiationRequest() {
        return encryptedCreditNoteInitiationRequest;
    }

    public void setEncryptedCreditNoteInitiationRequest(String encryptedCreditNoteInitiationRequest) {
        this.encryptedCreditNoteInitiationRequest = encryptedCreditNoteInitiationRequest;
    }

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public void setEncryptedSymmetricKey(String encryptedSymmetricKey) {
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }

    public static class Builder {
        private String cheqiReceiptId;
        private String publicKey;
        private String encryptedCreditNoteInitiationRequest;
        private String encryptedSymmetricKey;

        public EncryptedCreditNoteInitiationRequest.Builder cheqiReceiptId(String cheqiReceiptId) {
            this.cheqiReceiptId = cheqiReceiptId;
            return this;
        }

        public EncryptedCreditNoteInitiationRequest.Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public EncryptedCreditNoteInitiationRequest.Builder encryptedCreditNoteInitiationRequest(String encryptedCreditNoteInitiationRequest) {
            this.encryptedCreditNoteInitiationRequest = encryptedCreditNoteInitiationRequest;
            return this;
        }

        public EncryptedCreditNoteInitiationRequest.Builder encryptedSymmetricKey(String encryptedSymmetricKey) {
            this.encryptedSymmetricKey = encryptedSymmetricKey;
            return this;
        }

        public EncryptedCreditNoteInitiationRequest build() {
            return new EncryptedCreditNoteInitiationRequest(this);
        }
    }
}
