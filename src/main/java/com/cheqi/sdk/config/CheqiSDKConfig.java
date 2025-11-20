package com.cheqi.sdk.config;

import com.cheqi.sdk.encryption.EncryptionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main configuration class for the Cheqi SDK.
 *
 * Contains all configuration needed for SDK operation including:
 * - API endpoints and credentials
 * - Encryption configuration
 * - Timeout and retry settings
 *
 * Thread-safe and immutable after construction.
 */
public class CheqiSDKConfig {
    private static final Logger logger = LoggerFactory.getLogger(CheqiSDKConfig.class);

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

        /**
         * Sets the API endpoint using a predefined environment.
         *
         * @param apiEndpoint the predefined environment
         * @return this builder instance
         */
        public Builder apiEndpoint(Environment apiEndpoint) {
            this.apiEndpoint = apiEndpoint.getBaseUrl();
            return this;
        }

        /**
         * Sets a custom API endpoint URL.
         *
         * @param customUrl the custom API base URL
         * @return this builder instance
         */
        public Builder customApiEndpoint(String customUrl) {
            this.apiEndpoint = customUrl;
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
            // Validate API endpoint
            if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
                throw new IllegalStateException("API endpoint must be set using either apiEndpoint(Environment) or customApiEndpoint(String)");
            }
            
            // Validate supplier credentials
            if (supplierClientId == null || supplierClientId.trim().isEmpty()) {
                throw new IllegalStateException("Supplier client ID is required. Use supplierCredentials(clientId, clientSecret)");
            }
            if (supplierClientSecret == null || supplierClientSecret.trim().isEmpty()) {
                throw new IllegalStateException("Supplier client secret is required. Use supplierCredentials(clientId, clientSecret)");
            }
            
            // Validate timeout
            if (timeoutSeconds <= 0) {
                throw new IllegalArgumentException("Timeout must be positive, got: " + timeoutSeconds);
            }
            if (timeoutSeconds > 300) {
                logger.warn("Timeout is very high ({}s). Consider using a lower value for better UX", timeoutSeconds);
            }
            
            // Validate retries
            if (maxRetries < 0) {
                throw new IllegalArgumentException("Max retries cannot be negative, got: " + maxRetries);
            }
            if (maxRetries > 10) {
                logger.warn("Max retries is very high ({}). Consider using a lower value", maxRetries);
            }
            
            // Use default encryption config if not provided
            if (encryptionConfig == null) {
                logger.debug("No encryption config provided, using default");
                encryptionConfig = EncryptionConfig.builder().build();
            }
            
            CheqiSDKConfig config = new CheqiSDKConfig(this);
            logger.info("CheqiSDKConfig created: endpoint={}, timeout={}s, maxRetries={}", 
                    apiEndpoint, timeoutSeconds, maxRetries);
            
            return config;
        }
    }
}