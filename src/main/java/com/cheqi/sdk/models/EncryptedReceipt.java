package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedReceipt {
    @JsonProperty("encryptedReceipt")
    private String encryptedReceipt;

    /**
     * Base64-encoded device-specific encrypted symmetric key.
     */
    @JsonProperty("encryptedSymmetricKey")
    private String encryptedSymmetricKey;

    /**
     * Base64-encoded encrypted customer details (party envelope).
     */
    @JsonProperty("encryptedCustomerDetails")
    private String encryptedCustomerDetails;


    @JsonProperty("encryptedCustomerAesKey")
    private String encryptedCustomerSymmetricKey;

    @JsonProperty("Hash")
    private String Hash;

    public EncryptedReceipt() {
    }

    public EncryptedReceipt(String encryptedReceipt, String encryptedSymmetricKey, String encryptedCustomerDetails, String encryptedCustomerSymmetricKey) {
        this.encryptedReceipt = encryptedReceipt;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.encryptedCustomerDetails = encryptedCustomerDetails;
        this.encryptedCustomerSymmetricKey = encryptedCustomerSymmetricKey;
    }

    public String getEncryptedReceipt() {
        return encryptedReceipt;
    }

    public void setEncryptedReceipt(String encryptedReceipt) {
        this.encryptedReceipt = encryptedReceipt;
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

    public String getEncryptedCustomerSymmetricKey() {
        return encryptedCustomerSymmetricKey;
    }

    public void setEncryptedCustomerSymmetricKey(String encryptedCustomerSymmetricKey) {
        this.encryptedCustomerSymmetricKey = encryptedCustomerSymmetricKey;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }
}
