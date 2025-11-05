package com.cheqi.sdk.config;

import com.cheqi.sdk.encryption.EncryptionConfig;

/**
 * Main configuration class for the Cheqi SDK.
 *
 * Contains all configuration needed for SDK operation including:
 * - API endpoints and credentials
 * - Encryption configuration
 * - Timeout and retry settings
 */
public class CheqiSDKConfig {

    private final String apiEndpoint;
    private final String supplierClientId;
    private final String supplierClientSecret;
    private final EncryptionConfig encryptionConfig;
    private final int timeoutSeconds;
    private final int maxRetries;

    private CheqiSDKConfig(Builder builder) {
        this.apiEndpoint = builder.apiEndpoint;
        this.supplierClientId = builder.supplierClientId;
        this.supplierClientSecret = builder.supplierClientSecret;
        this.encryptionConfig = builder.encryptionConfig;
        this.timeoutSeconds = builder.timeoutSeconds;
        this.maxRetries = builder.maxRetries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getSupplierClientId() {
        return supplierClientId;
    }

    public String getSupplierClientSecret() {
        return supplierClientSecret;
    }

    public EncryptionConfig getEncryptionConfig() {
        return encryptionConfig;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public static class Builder {
        private String apiEndpoint;
        private String supplierClientId;
        private String supplierClientSecret;
        private EncryptionConfig encryptionConfig;
        private int timeoutSeconds = 30;
        private int maxRetries = 3;

        public Builder apiEndpoint(Environment apiEndpoint) {
            this.apiEndpoint = apiEndpoint.getBaseUrl();
            return this;
        }

        public Builder supplierCredentials(String clientId, String clientSecret) {
            this.supplierClientId = clientId;
            this.supplierClientSecret = clientSecret;
            return this;
        }

        public Builder encryptionConfig(EncryptionConfig encryptionConfig) {
            this.encryptionConfig = encryptionConfig;
            return this;
        }

        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public CheqiSDKConfig build() {
            if (encryptionConfig == null) {
                encryptionConfig = EncryptionConfig.builder().build();
            }
            return new CheqiSDKConfig(this);
        }
    }
}