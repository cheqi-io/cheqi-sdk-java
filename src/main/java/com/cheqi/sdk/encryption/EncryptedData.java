package com.cheqi.sdk.encryption;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents encrypted data with all necessary metadata for decryption.
 *
 * This class encapsulates:
 * - The encrypted content (Base64 encoded)
 * - Initialization vector (IV) for AES-GCM
 * - Encrypted symmetric key
 * - Algorithm information
 * - Key identifier for key management
 */
public class EncryptedData {

    private final String encryptedContent;
    private final String iv;
    private final String encryptedKey;
    private final String algorithm;
    private final String keyId;

    @JsonCreator
    public EncryptedData(
            @JsonProperty("encryptedContent") String encryptedContent,
            @JsonProperty("iv") String iv,
            @JsonProperty("encryptedKey") String encryptedKey,
            @JsonProperty("algorithm") String algorithm,
            @JsonProperty("keyId") String keyId) {
        this.encryptedContent = encryptedContent;
        this.iv = iv;
        this.encryptedKey = encryptedKey;
        this.algorithm = algorithm;
        this.keyId = keyId;
    }

    public static EncryptedDataBuilder builder() {
        return new EncryptedDataBuilder();
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public String getIv() {
        return iv;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getKeyId() {
        return keyId;
    }

    public static class EncryptedDataBuilder {
        private String encryptedContent;
        private String iv;
        private String encryptedKey;
        private String algorithm;
        private String keyId;

        public EncryptedDataBuilder encryptedContent(String encryptedContent) {
            this.encryptedContent = encryptedContent;
            return this;
        }

        public EncryptedDataBuilder iv(String iv) {
            this.iv = iv;
            return this;
        }

        public EncryptedDataBuilder encryptedKey(String encryptedKey) {
            this.encryptedKey = encryptedKey;
            return this;
        }

        public EncryptedDataBuilder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public EncryptedDataBuilder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public EncryptedData build() {
            return new EncryptedData(encryptedContent, iv, encryptedKey, algorithm, keyId);
        }
    }
}