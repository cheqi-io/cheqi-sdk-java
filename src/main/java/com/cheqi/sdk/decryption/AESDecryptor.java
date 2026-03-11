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
            logger.info("🔐 Starting AES-GCM decryption");
            logger.info("🔍 Encrypted data Base64 length: {} characters", encryptedDataBase64.length());
            
            // Decode the combined data (IV + ciphertext + tag)
            byte[] combinedData = Base64.getDecoder().decode(encryptedDataBase64);
            logger.info("✅ Decoded combined data: {} bytes", combinedData.length);
            logger.info("🔍 Combined data (hex, first 32 bytes): {}", bytesToHex(combinedData, 32));

            // Extract components
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combinedData.length - GCM_IV_LENGTH - GCM_TAG_LENGTH];
            byte[] tag = new byte[GCM_TAG_LENGTH];

            System.arraycopy(combinedData, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combinedData, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);
            System.arraycopy(combinedData, GCM_IV_LENGTH + ciphertext.length, tag, 0, GCM_TAG_LENGTH);
            
            logger.info("✅ Extracted components: IV={} bytes, ciphertext={} bytes, tag={} bytes", 
                       iv.length, ciphertext.length, tag.length);
            logger.info("🔍 IV (hex): {}", bytesToHex(iv, iv.length));
            logger.info("🔍 Tag (hex): {}", bytesToHex(tag, tag.length));

            // Reconstruct ciphertext + tag for AES-GCM
            byte[] ciphertextWithTag = new byte[ciphertext.length + tag.length];
            System.arraycopy(ciphertext, 0, ciphertextWithTag, 0, ciphertext.length);
            System.arraycopy(tag, 0, ciphertextWithTag, ciphertext.length, tag.length);
            logger.info("✅ Reconstructed ciphertext+tag: {} bytes", ciphertextWithTag.length);

            // Decrypt with AES-GCM
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            logger.info("🔧 Using AES transformation: {}", AES_TRANSFORMATION);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
            logger.info("✅ Cipher initialized with GCM parameters (tag length: {} bits)", GCM_TAG_LENGTH * 8);

            byte[] decryptedBytes = cipher.doFinal(ciphertextWithTag);
            logger.info("✅ AES-GCM decryption successful: {} bytes", decryptedBytes.length);
            
            String decryptedPayload = new String(decryptedBytes, StandardCharsets.UTF_8);
            logger.info("✅ Converted to UTF-8 string: {} characters", decryptedPayload.length());
            logger.info("🔍 Decrypted content (first 200 chars): {}...",
                    decryptedPayload.substring(0, Math.min(200, decryptedPayload.length())));
            
            return decryptedPayload;

        } catch (Exception e) {
            logger.error("❌ AES-GCM decryption failed: {}", e.getMessage(), e);
            throw new DecryptionException("AES decryption failed", e);
        }
    }
    
    /**
     * Converts byte array to hex string for logging.
     */
    private String bytesToHex(byte[] bytes, int maxBytes) {
        int length = Math.min(bytes.length, maxBytes);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
}
