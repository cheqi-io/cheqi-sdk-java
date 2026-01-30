package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = EncryptedReceipt.Builder.class)
public class EncryptedReceipt {
    // ===== MANDATORY FIELDS =====
    /**
     * The UUID of the target device for this encrypted receipt.
     */
    @JsonProperty("recipientId")
    private final UUID recipientId;

    @JsonProperty("receiptId")
    private final String receiptId;

    @JsonProperty("receiverType")
    private final ReceiverType receiverType;

    @JsonProperty("encryptedReceipt")
    private final String encryptedReceipt;

    /**
     * Base64-encoded device-specific encrypted symmetric key.
     */
    @JsonProperty("encryptedSymmetricKey")
    private final String encryptedSymmetricKey;

    /**
     * Base64-encoded encrypted receipt data.
     */
    @JsonProperty("encryptedCustomerDetails")
    private final String encryptedCustomerDetails;


    @JsonProperty("encryptedCustomerAesKey")
    private final String encryptedCustomerAesKey;

    @JsonProperty("finalHash")
    private final String finalHash;

    /**
     * Public key used to encrypt the symmetric key (for key selection on decrypt).
     */
    @JsonProperty("publicKey")
    private final String publicKey;

    // ===== OPTIONAL FIELDS =====

    /**
     * Supplier party ID for tracking purposes.
     */
    @JsonProperty("supplierPartyId")
    private final UUID supplierPartyId;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private EncryptedReceipt(
            UUID recipientId,
            String receiptId,
            ReceiverType receiverType,
            String encryptedReceipt,
            String encryptedSymmetricKey,
            String encryptedCustomerDetails,
            String encryptedCustomerAesKey,
            String finalHash,
            String publicKey,
            UUID supplierPartyId,
            Map<String, Object> additionalProperties) {
        this.recipientId = recipientId;
        this.receiptId = receiptId;
        this.receiverType = receiverType;
        this.encryptedReceipt = encryptedReceipt;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.encryptedCustomerDetails = encryptedCustomerDetails;
        this.encryptedCustomerAesKey = encryptedCustomerAesKey;
        this.finalHash = finalHash;
        this.publicKey = publicKey;
        this.supplierPartyId = supplierPartyId;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public UUID getRecipientId() {
        return recipientId;
    }

    @JsonIgnore
    public ReceiverType getReceiverType() {
        return receiverType;
    }

    @JsonIgnore
    public String getEncryptedReceipt() {
        return encryptedReceipt;
    }

    @JsonIgnore
    public String getEncryptedCustomerDetails() {
        return encryptedCustomerDetails;
    }

    @JsonIgnore
    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }

    @JsonIgnore
    public String getEncryptedCustomerAesKey() {
        return encryptedCustomerAesKey;
    }

    @JsonIgnore
    public String getReceiptId() {
        return receiptId;
    }

    @JsonIgnore
    public String getFinalHash() {
        return finalHash;
    }

    @JsonIgnore
    public String getPublicKey() {
        return publicKey;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    @JsonIgnore
    public UUID getSupplierPartyId() {
        return supplierPartyId;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof EncryptedReceipt && equalTo((EncryptedReceipt) other);
    }

    private boolean equalTo(EncryptedReceipt other) {
        return Objects.equals(this.recipientId, other.recipientId)
                && Objects.equals(this.receiptId, other.receiptId)
                && Objects.equals(this.receiverType, other.receiverType)
                && Objects.equals(this.encryptedReceipt, other.encryptedReceipt)
                && Objects.equals(this.encryptedCustomerDetails, other.encryptedCustomerDetails)
                && Objects.equals(this.encryptedSymmetricKey, other.encryptedSymmetricKey)
                && Objects.equals(this.encryptedCustomerAesKey, other.encryptedCustomerAesKey)
                && Objects.equals(this.finalHash, other.finalHash)
                && Objects.equals(this.supplierPartyId, other.supplierPartyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.recipientId, this.encryptedReceipt, this.encryptedCustomerDetails, this.encryptedSymmetricKey, this.encryptedCustomerAesKey,this.supplierPartyId);
    }

    @Override
    public String toString() {
        return "EncryptedReceipt{" +
                "recipientId=" + recipientId +
                ", receiptId=" + receiptId +
                ", receiverType=" + receiverType +
                ", encryptedReceipt='" + (encryptedReceipt != null ? "[ENCRYPTED]" : null) + '\'' +
                ", encryptedCustomerDetails='" + (encryptedCustomerDetails != null ? "[ENCRYPTED]" : null) + '\'' +
                ", encryptedSymmetricKey='" + (encryptedSymmetricKey != null ? "[ENCRYPTED]" : null) + '\'' +
                ", encryptedCustomerAesKey='" + (encryptedCustomerAesKey != null ? "[ENCRYPTED]" : null) + '\'' +
                ", finalHash='" + (finalHash != null ? "[ENCRYPTED]" : null) + '\'' +
                ", supplierPartyId=" + supplierPartyId +
                '}';
    }

    public static EncryptedReceipt.Builder builder() {
        return new EncryptedReceipt.Builder();
    }

    @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private UUID recipientId;
        private String receiptId;
        private ReceiverType receiverType;
        private String encryptedReceipt;
        private String encryptedCustomerDetails;
        private String encryptedSymmetricKey;
        private String encryptedCustomerAesKey;
        private String finalHash;
        private String publicKey;
        private UUID supplierPartyId;
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public EncryptedReceipt.Builder from(EncryptedReceipt other) {
            recipientId(other.getRecipientId());
            receiptId(other.getReceiptId());
            receiverType(other.getReceiverType());
            encryptedReceipt(other.getEncryptedReceipt());
            encryptedCustomerDetails(other.getEncryptedCustomerDetails());
            encryptedSymmetricKey(other.getEncryptedSymmetricKey());
            encryptedCustomerAesKey(other.getEncryptedCustomerAesKey());
            finalHash(other.getFinalHash());
            publicKey(other.getPublicKey());
            supplierPartyId(other.getSupplierPartyId());
            return this;
        }

        @JsonSetter(value = "recipientId", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder recipientId(UUID recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        @JsonSetter(value = "receiptId", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        @JsonSetter(value = "receiverType", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder receiverType(ReceiverType receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        @JsonSetter(value = "encryptedReceipt", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder encryptedReceipt(String encryptedReceipt) {
            this.encryptedReceipt = encryptedReceipt;
            return this;
        }

        @JsonSetter(value = "encryptedCustomerDetails", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder encryptedCustomerDetails(String encryptedCustomerDetails) {
            this.encryptedCustomerDetails = encryptedCustomerDetails;
            return this;
        }

        @JsonSetter(value = "encryptedSymmetricKey", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder encryptedSymmetricKey(String encryptedSymmetricKey) {
            this.encryptedSymmetricKey = encryptedSymmetricKey;
            return this;
        }

        @JsonSetter(value = "encryptedCustomerAesKey", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder encryptedCustomerAesKey(String encryptedCustomerAesKey) {
            this.encryptedCustomerAesKey = encryptedCustomerAesKey;
            return this;
        }

        @JsonSetter(value = "supplierPartyId", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder supplierPartyId(UUID supplierPartyId) {
            this.supplierPartyId = supplierPartyId;
            return this;
        }

        @JsonSetter(value = "finalHash", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder finalHash(String finalHash) {
            this.finalHash = finalHash;
            return this;
        }

        @JsonSetter(value = "publicKey", nulls = Nulls.SKIP)
        public EncryptedReceipt.Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public EncryptedReceipt build() {
            return new EncryptedReceipt(
                    recipientId,
                    receiptId,
                    receiverType,
                    encryptedReceipt,
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

    // ===== VALIDATION METHODS =====

    /**
     * Validates this encrypted receipt DTO.
     * @return true if all mandatory fields are present and valid
     */
    public boolean isValid() {
        List<String> errors = getValidationErrors();
        return errors.isEmpty();
    }

    /**
     * Gets detailed validation errors for this encrypted receipt DTO.
     * @return List of validation error messages, empty if valid
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Mandatory field validation
        if (recipientId == null) {
            errors.add("Device ID is required");
        }

        if (receiverType == null) {
            errors.add("Receiver type is required");
        }

        if (encryptedReceipt == null || encryptedReceipt.trim().isEmpty()) {
            errors.add("Encrypted receipt data is required");
        }

        if (encryptedSymmetricKey == null || encryptedSymmetricKey.trim().isEmpty()) {
            errors.add("Encrypted symmetric key is required");
        }

        return errors;
    }
}
