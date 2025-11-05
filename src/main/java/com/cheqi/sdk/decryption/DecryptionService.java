package com.cheqi.sdk.decryption;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Decryption service for Cheqi hybrid-encrypted content.
 *
 * This service can decrypt hybrid-encrypted receipts that use:
 * - AES-256-GCM for content encryption
 * - RSA-OAEP for symmetric key encryption
 *
 * Compatible with content encrypted by the Cheqi EncryptionService.
 */
public class DecryptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private final ObjectMapper objectMapper;

    public DecryptionService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Decrypts a hybrid-encrypted receipt using the client's private key.
     *
     * This method handles the complete hybrid decryption process:
     * 1. Decrypts the AES symmetric key using RSA private key
     * 2. Decrypts the receipt content using the AES key
     * 3. Decrypts the customer details using RSA private key
     *
     * @param encryptedReceipt The encrypted receipt DTO from Cheqi API or webhook
     * @param privateKeyBase64 The client's private key in Base64 PKCS#8 format
     * @return DecryptedReceipt containing both receipt content and customer details
     * @throws DecryptionException if decryption fails
     */
    public DecryptedReceipt decryptReceipt(EncryptedReceiptDto encryptedReceipt, String privateKeyBase64) {
        try {
            // Step 1: Decrypt the receipt AES key using RSA private key
            SecretKey receiptAesKey = decryptSymmetricKey(
                    encryptedReceipt.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );

            // Step 2: Decrypt the receipt content using the receipt AES key
            String decryptedReceiptContentJson = decryptReceiptContent(encryptedReceipt.getEncryptedReceipt(), receiptAesKey);

            // Step 3: Decrypt customer details using separate customer AES key (if present)
            String decryptedCustomerDetails = null;
            if (encryptedReceipt.getEncryptedCustomerDetails() != null &&
                    !encryptedReceipt.getEncryptedCustomerDetails().trim().isEmpty() &&
                    encryptedReceipt.getEncryptedCustomerAesKey() != null) {

                // Decrypt the customer AES key using RSA private key
                SecretKey customerAesKey = decryptSymmetricKey(
                        encryptedReceipt.getEncryptedCustomerAesKey(),
                        privateKeyBase64
                );

                // Decrypt customer details using the customer AES key
                decryptedCustomerDetails = decryptReceiptContent(
                        encryptedReceipt.getEncryptedCustomerDetails(),
                        customerAesKey
                );
            }

            return new DecryptedReceipt(decryptedReceiptContentJson, decryptedCustomerDetails);

        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt receipt", e);
        }
    }

    /**
     * Decrypts customer details using RSA private key.
     */
    private String decryptCustomerDetails(String encryptedCustomerDetails, String privateKeyBase64) throws Exception {
        // Parse the private key
        PrivateKey privateKey = parsePrivateKey(privateKeyBase64);

        // Setup RSA cipher with OAEP padding (same as encryption)
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",                          // Hash algorithm
                "MGF1",                            // Mask generation function
                new MGF1ParameterSpec("SHA-256"),  // MGF1 hash algorithm
                PSource.PSpecified.DEFAULT         // Empty label (default)
        );

        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        // Decode and decrypt
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedCustomerDetails);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts the AES symmetric key using RSA private key.
     */
    private SecretKey decryptSymmetricKey(String encryptedSymmetricKey, String privateKeyBase64) throws Exception {
        // Parse the private key
        PrivateKey privateKey = parsePrivateKey(privateKeyBase64);

        // Setup RSA cipher with OAEP padding (same as encryption)
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",                          // Hash algorithm
                "MGF1",                            // Mask generation function
                new MGF1ParameterSpec("SHA-256"),  // MGF1 hash algorithm
                PSource.PSpecified.DEFAULT         // Empty label (default)
        );

        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        // Decrypt the symmetric key
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedSymmetricKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

        // Reconstruct the AES key
        return new SecretKeySpec(decryptedKeyBytes, AES_ALGORITHM);
    }

    /**
     * Decrypts the receipt content using AES-GCM.
     */
    private String decryptReceiptContent(String encryptedReceiptBase64, SecretKey aesKey) throws Exception {
        // Decode the combined data (IV + ciphertext + tag)
        byte[] combinedData = Base64.getDecoder().decode(encryptedReceiptBase64);

        // Extract components (same format as EncryptionService)
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] ciphertext = new byte[combinedData.length - GCM_IV_LENGTH - GCM_TAG_LENGTH];
        byte[] tag = new byte[GCM_TAG_LENGTH];

        System.arraycopy(combinedData, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combinedData, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);
        System.arraycopy(combinedData, GCM_IV_LENGTH + ciphertext.length, tag, 0, GCM_TAG_LENGTH);

        // Reconstruct ciphertext + tag for AES-GCM
        byte[] ciphertextWithTag = new byte[ciphertext.length + tag.length];
        System.arraycopy(ciphertext, 0, ciphertextWithTag, 0, ciphertext.length);
        System.arraycopy(tag, 0, ciphertextWithTag, ciphertext.length, tag.length);

        // Decrypt with AES-GCM
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        byte[] decryptedBytes = cipher.doFinal(ciphertextWithTag);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Parses a Base64-encoded PKCS#8 private key.
     */
    private PrivateKey parsePrivateKey(String privateKeyBase64) throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new Exception("Failed to parse RSA private key: " + e.getMessage(), e);
        }
    }

    /**
     * Custom exception for decryption errors.
     */
    public static class DecryptionException extends RuntimeException {
        public DecryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}