package com.cheqi.sdk.encryption;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Configuration class for encryption settings and key management.
 *
 * This class manages:
 * - Encryption algorithm parameters
 * - Salt values for deterministic matching
 * - Key derivation settings
 * - Security parameters
 */
public class EncryptionConfig {

    private final byte[] matchingSalt;
    private final String environment;
    private final boolean strictMode;

    private EncryptionConfig(Builder builder) {
        this.matchingSalt = builder.matchingSalt;
        this.environment = builder.environment;
        this.strictMode = builder.strictMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getMatchingSalt() {
        return matchingSalt.clone(); // Return copy for security
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public static class Builder {
        private byte[] matchingSalt;
        private String environment = "production";
        private boolean strictMode = true;

        public Builder matchingSalt(String salt) {
            this.matchingSalt = salt.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public Builder matchingSalt(byte[] salt) {
            this.matchingSalt = salt.clone();
            return this;
        }

        public Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public Builder strictMode(boolean strictMode) {
            this.strictMode = strictMode;
            return this;
        }

        public EncryptionConfig build() {
            if (matchingSalt == null) {
                // Generate a random salt if none provided (for testing only)
                SecureRandom random = new SecureRandom();
                matchingSalt = new byte[32];
                random.nextBytes(matchingSalt);
            }
            return new EncryptionConfig(this);
        }
    }
}