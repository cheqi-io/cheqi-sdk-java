package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.ReceiverType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class EncryptedCreditNote {
    @JsonProperty("recipientId")
    private UUID recipientId;

    @JsonProperty("creditNoteId")
    private String creditNoteId;


    @JsonProperty("originalReceiptId")
    private String originalReceiptId;

    @JsonProperty("receiverType")
    private ReceiverType receiverType;

    @JsonProperty("encryptedCreditNote")
    private String encryptedCreditNote;

    /**
     * Base64-encoded device-specific encrypted symmetric key.
     */
    @JsonProperty("encryptedSymmetricKey")
    private String encryptedSymmetricKey;

    /**
     * Base64-encoded encrypted customer details.
     */
    @JsonProperty("encryptedCustomerDetails")
    private  String encryptedCustomerDetails;

    @JsonProperty("encryptedCustomerAesKey")
    private String encryptedCustomerAesKey;

    @JsonProperty("finalHash")
    private String finalHash;

    @JsonProperty("publicKey")
    private String publicKey;

    // ===== OPTIONAL FIELDS =====

    /**
     * Supplier party ID for tracking purposes.
     */
    @JsonProperty("supplierPartyId")
    private UUID supplierPartyId;

    private Map<String, Object> additionalProperties;

    public EncryptedCreditNote() {
    }

    public EncryptedCreditNote(UUID recipientId, String creditNoteId, String originalReceiptId, ReceiverType receiverType, String encryptedCreditNote, String encryptedSymmetricKey, String encryptedCustomerDetails, String encryptedCustomerAesKey, String finalHash, String publicKey, UUID supplierPartyId, Map<String, Object> additionalProperties) {
        this.recipientId = recipientId;
        this.creditNoteId = creditNoteId;
        this.originalReceiptId = originalReceiptId;
        this.receiverType = receiverType;
        this.encryptedCreditNote = encryptedCreditNote;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.encryptedCustomerDetails = encryptedCustomerDetails;
        this.encryptedCustomerAesKey = encryptedCustomerAesKey;
        this.finalHash = finalHash;
        this.publicKey = publicKey;
        this.supplierPartyId = supplierPartyId;
        this.additionalProperties = additionalProperties;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public String getCreditNoteId() {
        return creditNoteId;
    }

    public void setCreditNoteId(String creditNoteId) {
        this.creditNoteId = creditNoteId;
    }

    public String getOriginalReceiptId() {
        return originalReceiptId;
    }

    public void setOriginalReceiptId(String originalReceiptId) {
        this.originalReceiptId = originalReceiptId;
    }

    public ReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public String getEncryptedCreditNote() {
        return encryptedCreditNote;
    }

    public void setEncryptedCreditNote(String encryptedCreditNote) {
        this.encryptedCreditNote = encryptedCreditNote;
    }

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public void setEncryptedSymmetricKey(String encryptedSymmetricKey) {
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }

    public String getEncryptedCustomerDetails() {
        return encryptedCustomerDetails;
    }

    public void setEncryptedCustomerDetails(String encryptedCustomerDetails) {
        this.encryptedCustomerDetails = encryptedCustomerDetails;
    }

    public String getEncryptedCustomerAesKey() {
        return encryptedCustomerAesKey;
    }

    public void setEncryptedCustomerAesKey(String encryptedCustomerAesKey) {
        this.encryptedCustomerAesKey = encryptedCustomerAesKey;
    }

    public String getFinalHash() {
        return finalHash;
    }

    public void setFinalHash(String finalHash) {
        this.finalHash = finalHash;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public UUID getSupplierPartyId() {
        return supplierPartyId;
    }

    public void setSupplierPartyId(UUID supplierPartyId) {
        this.supplierPartyId = supplierPartyId;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID recipientId;
        private String creditNoteId;
        private String originalReceiptId;
        private ReceiverType receiverType;
        private String encryptedCreditNote;
        private String encryptedSymmetricKey;
        private String encryptedCustomerDetails;
        private String encryptedCustomerAesKey;
        private String finalHash;
        private String publicKey;
        private UUID supplierPartyId;
        private Map<String, Object> additionalProperties;

        public Builder recipientId(UUID recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        public Builder creditNoteId(String creditNoteId) {
            this.creditNoteId = creditNoteId;
            return this;
        }

        public Builder originalReceiptId(String originalReceiptId) {
            this.originalReceiptId = originalReceiptId;
            return this;
        }

        public Builder receiverType(ReceiverType receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        public Builder encryptedCreditNote(String encryptedCreditNote) {
            this.encryptedCreditNote = encryptedCreditNote;
            return this;
        }

        public Builder encryptedSymmetricKey(String encryptedSymmetricKey) {
            this.encryptedSymmetricKey = encryptedSymmetricKey;
            return this;
        }

        public Builder encryptedCustomerDetails(String encryptedCustomerDetails) {
            this.encryptedCustomerDetails = encryptedCustomerDetails;
            return this;
        }

        public Builder encryptedCustomerAesKey(String encryptedCustomerAesKey) {
            this.encryptedCustomerAesKey = encryptedCustomerAesKey;
            return this;
        }

        public Builder finalHash(String finalHash) {
            this.finalHash = finalHash;
            return this;
        }

        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder supplierPartyId(UUID supplierPartyId) {
            this.supplierPartyId = supplierPartyId;
            return this;
        }

        public Builder additionalProperties(Map<String, Object> additionalProperties) {
            this.additionalProperties = additionalProperties;
            return this;
        }

        public EncryptedCreditNote build() {
            return new EncryptedCreditNote(
                recipientId,
                creditNoteId,
                originalReceiptId,
                receiverType,
                encryptedCreditNote,
                encryptedSymmetricKey,
                encryptedCustomerDetails,
                encryptedCustomerAesKey,
                finalHash,
                publicKey,
                supplierPartyId,
                additionalProperties
            );
        }
    }
}
