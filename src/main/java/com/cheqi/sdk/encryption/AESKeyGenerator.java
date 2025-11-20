package com.cheqi.sdk.encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * Generates AES-256 keys for receipt encryption.
 */
public class AESKeyGenerator {
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_LENGTH = 256;

    private final SecureRandom secureRandom;

    public AESKeyGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    /**
     * Generates a new random AES-256 key.
     */
    public SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_LENGTH, secureRandom);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new EncryptionException("Failed to generate AES key", e);
        }
    }
}
