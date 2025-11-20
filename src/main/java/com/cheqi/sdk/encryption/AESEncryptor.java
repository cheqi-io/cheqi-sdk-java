package com.cheqi.sdk.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AESEncryptor {
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private final SecureRandom secureRandom;

    public AESEncryptor(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    /**
     * Encrypts plaintext with AES-256-GCM.
     */
    public EncryptedData encrypt(String plaintext, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            // In AES-GCM, doFinal returns ciphertext + tag combined
            byte[] ciphertextWithTag = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Separate ciphertext and tag
            byte[] ciphertext = new byte[ciphertextWithTag.length - GCM_TAG_LENGTH];
            byte[] tag = new byte[GCM_TAG_LENGTH];

            System.arraycopy(ciphertextWithTag, 0, ciphertext, 0, ciphertext.length);
            System.arraycopy(ciphertextWithTag, ciphertext.length, tag, 0, GCM_TAG_LENGTH);

            return new EncryptedData(ciphertext, iv, tag);
        } catch (Exception e) {
            throw new EncryptionException("AES encryption failed", e);
        }
    }
}
