package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = EncryptedReceiptRequestDto.Builder.class)
public final class EncryptedReceiptRequestDto {
    @JsonProperty("recipientId")
    private final String recipientId;

    @JsonProperty("receiptId")
    private final String receiptId;

    @JsonProperty("receiverType")
    private final ReceiverType receiverType;

    @JsonProperty("receiptFormats")
    private final List<ReceiptFormat> receiptFormats;

    @JsonProperty("encryptedReceipt")
    private final String encryptedReceipt;

    @JsonProperty("encryptedSymmetricKey")
    private final String encryptedSymmetricKey;

    @JsonProperty("finalHash")
    private final String finalHash;

    @JsonProperty("publicKey")
    private final String publicKey;

    @JsonProperty("supplierPartyId")
    private final UUID supplierPartyId;

    private final Map<String, Object> additionalProperties;

    private EncryptedReceiptRequestDto(
            String recipientId,
            String receiptId,
            ReceiverType receiverType,
            List<ReceiptFormat> receiptFormats,
            String encryptedReceipt,
            String encryptedSymmetricKey,
            String finalHash,
            String publicKey,
            UUID supplierPartyId,
            Map<String, Object> additionalProperties) {
        this.recipientId = recipientId;
        this.receiptId = receiptId;
        this.receiverType = receiverType;
        this.receiptFormats = receiptFormats;
        this.encryptedReceipt = encryptedReceipt;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.finalHash = finalHash;
        this.publicKey = publicKey;
        this.supplierPartyId = supplierPartyId;
        this.additionalProperties = additionalProperties;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public ReceiverType getReceiverType() {
        return receiverType;
    }

    public List<ReceiptFormat> getReceiptFormats() {
        return receiptFormats;
    }

    public String getEncryptedReceipt() {
        return encryptedReceipt;
    }

    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    public String getFinalHash() {
        return finalHash;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public UUID getSupplierPartyId() {
        return supplierPartyId;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof EncryptedReceiptRequestDto && equalTo((EncryptedReceiptRequestDto) other);
    }

    private boolean equalTo(EncryptedReceiptRequestDto other) {
        return Objects.equals(this.recipientId, other.recipientId)
                && Objects.equals(this.receiptId, other.receiptId)
                && Objects.equals(this.receiverType, other.receiverType)
                && Objects.equals(this.receiptFormats, other.receiptFormats)
                && Objects.equals(this.encryptedReceipt, other.encryptedReceipt)
                && Objects.equals(this.encryptedSymmetricKey, other.encryptedSymmetricKey)
                && Objects.equals(this.finalHash, other.finalHash)
                && Objects.equals(this.publicKey, other.publicKey)
                && Objects.equals(this.supplierPartyId, other.supplierPartyId)
                && Objects.equals(this.additionalProperties, other.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.recipientId, this.receiptId, this.receiverType, this.receiptFormats, this.encryptedReceipt,
                this.encryptedSymmetricKey, this.finalHash, this.publicKey, this.supplierPartyId, this.additionalProperties);
    }

    @Override
    public String toString() {
        return "EncryptedReceiptRequestDto{" +
                "recipientId=" + recipientId +
                ", receiptId='" + receiptId + '\'' +
                ", receiverType=" + receiverType +
                ", receiptFormats=" + receiptFormats +
                ", encryptedReceipt='" + encryptedReceipt + '\'' +
                ", encryptedSymmetricKey='" + encryptedSymmetricKey + '\'' +
                ", finalHash='" + finalHash + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", supplierPartyId=" + supplierPartyId +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String recipientId;
        private String receiptId;
        private ReceiverType receiverType;
        private List<ReceiptFormat> receiptFormats;
        private String encryptedReceipt;
        private String encryptedSymmetricKey;
        private String finalHash;
        private String publicKey;
        private UUID supplierPartyId;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Builder from(EncryptedReceiptRequestDto other) {
            recipientId(other.getRecipientId());
            receiptId(other.getReceiptId());
            receiverType(other.getReceiverType());
            receiptFormats(other.getReceiptFormats());
            encryptedReceipt(other.getEncryptedReceipt());
            encryptedSymmetricKey(other.getEncryptedSymmetricKey());
            finalHash(other.getFinalHash());
            publicKey(other.getPublicKey());
            supplierPartyId(other.getSupplierPartyId());
            return this;
        }

        @JsonSetter(value = "recipientId", nulls = Nulls.SKIP)
        public Builder recipientId(String recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        @JsonSetter(value = "receiptId", nulls = Nulls.SKIP)
        public Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        @JsonSetter(value = "receiverType", nulls = Nulls.SKIP)
        public Builder receiverType(ReceiverType receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        @JsonSetter(value = "receiptFormat", nulls = Nulls.SKIP)
        public Builder receiptFormats(List<ReceiptFormat> receiptFormats) {
            this.receiptFormats = receiptFormats;
            return this;
        }

        @JsonSetter(value = "encryptedReceipt", nulls = Nulls.SKIP)
        public Builder encryptedReceipt(String encryptedReceipt) {
            this.encryptedReceipt = encryptedReceipt;
            return this;
        }

        @JsonSetter(value = "encryptedSymmetricKey", nulls = Nulls.SKIP)
        public Builder encryptedSymmetricKey(String encryptedSymmetricKey) {
            this.encryptedSymmetricKey = encryptedSymmetricKey;
            return this;
        }

        @JsonSetter(value = "finalHash", nulls = Nulls.SKIP)
        public Builder finalHash(String finalHash) {
            this.finalHash = finalHash;
            return this;
        }

        @JsonSetter(value = "publicKey", nulls = Nulls.SKIP)
        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        @JsonSetter(value = "supplierPartyId", nulls = Nulls.SKIP)
        public Builder supplierPartyId(UUID supplierPartyId) {
            this.supplierPartyId = supplierPartyId;
            return this;
        }

        public EncryptedReceiptRequestDto build() {
            return new EncryptedReceiptRequestDto(recipientId, receiptId, receiverType, receiptFormats, encryptedReceipt,
                    encryptedSymmetricKey, finalHash, publicKey, supplierPartyId, additionalProperties);
        }
    }
}