package com.cheqi.sdk.decryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Handles AES-256-GCM decryption for receipt content.
 */
public class AESDecryptor {
    private static final Logger logger = LoggerFactory.getLogger(AESDecryptor.class);
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * Decrypts AES-GCM encrypted content.
     *
     * @param encryptedDataBase64 Base64-encoded data in format: IV + ciphertext + tag
     * @param aesKey The AES key to use for decryption
     * @return Decrypted plaintext string
     * @throws DecryptionException if decryption fails
     */
    public String decrypt(String encryptedDataBase64, SecretKey aesKey) {
        try {
            logger.debug("Starting AES-GCM decryption");
            
            byte[] combinedData = Base64.getDecoder().decode(encryptedDataBase64);
            logger.debug("Decoded encrypted payload ({} bytes)", combinedData.length);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combinedData.length - GCM_IV_LENGTH - GCM_TAG_LENGTH];
            byte[] tag = new byte[GCM_TAG_LENGTH];

            System.arraycopy(combinedData, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combinedData, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);
            System.arraycopy(combinedData, GCM_IV_LENGTH + ciphertext.length, tag, 0, GCM_TAG_LENGTH);
            logger.debug("Extracted AES-GCM components: iv={} bytes, ciphertext={} bytes, tag={} bytes",
                    iv.length, ciphertext.length, tag.length);

            byte[] ciphertextWithTag = new byte[ciphertext.length + tag.length];
            System.arraycopy(ciphertext, 0, ciphertextWithTag, 0, ciphertext.length);
            System.arraycopy(tag, 0, ciphertextWithTag, ciphertext.length, tag.length);
            logger.debug("Reconstructed ciphertext+tag buffer ({} bytes)", ciphertextWithTag.length);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(ciphertextWithTag);
            logger.debug("AES-GCM decryption completed ({} bytes)", decryptedBytes.length);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("AES-GCM decryption failed: {}", e.getMessage(), e);
            throw new DecryptionException("AES decryption failed", e);
        }
    }
}
