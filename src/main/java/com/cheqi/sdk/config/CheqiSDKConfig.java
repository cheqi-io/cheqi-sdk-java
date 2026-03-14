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
    private final String apiKey;
    private final String privateKeyBase64;
    private final EncryptionConfig encryptionConfig;
    private final int timeoutSeconds;
    private final int maxRetries;

    private CheqiSDKConfig(Builder builder) {
        this.apiEndpoint = builder.apiEndpoint;
        this.apiKey = builder.apiKey;
        this.privateKeyBase64 = builder.privateKeyBase64;
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

    public String getApiKey() {
        return apiKey;
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

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public static class Builder {
        private String apiEndpoint;
        private String apiKey;
        private String privateKeyBase64;
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

        /**
         * Sets the API key for direct company access.
         * Use this for companies accessing their own data.
         *
         * @param apiKey API key (starts with sk_live_ or sk_test_)
         * @return this builder instance
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
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

        /**
         * Sets the issuer's private key for decrypting credit note requests.
         *
         * @param privateKeyBase64 RSA private key in Base64 PKCS#8 format
         * @return this builder instance
         */
        public Builder privateKey(String privateKeyBase64) {
            this.privateKeyBase64 = privateKeyBase64;
            return this;
        }

        public CheqiSDKConfig build() {
            // Validate API endpoint
            if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
                throw new IllegalStateException("API endpoint must be set using either apiEndpoint(Environment) or customApiEndpoint(String)");
            }
            
            // Optional authentication validation - log warning if no auth configured
            boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
            if (!hasApiKey) {
                logger.warn("No authentication configured. API calls will fail unless access tokens are provided explicitly.");
            }
            
            if (hasApiKey && !isValidApiKeyFormat(apiKey)) {
                logger.warn("API key does not match expected format (sk_live_* or sk_test_*)");
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
            String authMode = hasApiKey ? "API Key" : "None (tokens required per-call)";
            logger.info("CheqiSDKConfig created: endpoint={}, authMode={}, timeout={}s, maxRetries={}", 
                    apiEndpoint, authMode, timeoutSeconds, maxRetries);
            
            return config;
        }
        
        private boolean isValidApiKeyFormat(String key) {
            return key != null && (key.startsWith("sk_live_") || key.startsWith("sk_test_"));
        }
    }
}
