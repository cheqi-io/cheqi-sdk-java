package com.cheqi.sdk.encryption;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.DTOs.Recipient;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core encryption service providing AES-GCM encryption for end-to-end security.
 *
 * This service handles:
 * 1. Customer identifier encryption (PAR, IBAN, email)
 * 2. Receipt data encryption
 * 3. Key derivation and management
 * 4. Secure random generation
 *
 */
public class EncryptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";  // Back to OAEP with explicit parameters
    private static final int AES_KEY_LENGTH = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private final SecureRandom secureRandom;

    public EncryptionService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Encrypts a receipt template for multiple devices using hybrid encryption.
     *
     * @param purchaseReceipt The JSON receipt template to encrypt
     * @param recipients Map of device IDs to their public key information
     * @param supplierPartyId Supplier party identifier
     * @return List of encrypted receipt DTOs, one per device
     */
    public Set<EncryptedReceiptDto> encryptReceiptForRecipients(
            String purchaseReceipt,
            List<Recipient> recipients,
            UUID supplierPartyId) {
        try {
            return recipients.stream()
                    .map(recipient -> {
                        // Step 1: Encrypt the receipt (pure encryption logic)
                        EncryptedReceiptDto encryptedReceipt = createHybridEncryptedReceiptForDevice(
                                recipient,
                                purchaseReceipt,
                                supplierPartyId
                        );

                        // Step 2: Add pre-encrypted customer details (business logic)
                        return EncryptedReceiptDto.builder()
                                .from(encryptedReceipt)
                                .encryptedCustomerAesKey(recipient.getEncryptedAesKey())// Copy all existing fields
                                .encryptedCustomerDetails(recipient.getEncryptedCustomerDetails())
                                .build();
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt receipt for recipients", e);
        }
    }

    /**
     * Creates a hybrid-encrypted receipt DTO for a specific device.
     * Uses AES-GCM for receipt data and RSA-OAEP for the AES key.
     */
    private EncryptedReceiptDto createHybridEncryptedReceiptForDevice(
            Recipient recipient,
            String purchaseReceipt,
            UUID supplierPartyId) {
        try {
            // Step 1: Generate random AES-256 key for this receipt
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_LENGTH, secureRandom);
            SecretKey aesKey = keyGen.generateKey();

            EncryptedData encryptedReceiptDataJson = encryptWithAES(purchaseReceipt, aesKey);
            String encryptedReceiptJson = encryptedReceiptDataJson.toBase64String();

            // Step 3: Encrypt AES key with RSA-OAEP
            String encryptedSymmetricKey = encryptAESKeyWithRSA(aesKey, recipient.getPublicKey());

            return EncryptedReceiptDto.builder()
                    .recipientId(recipient.getId())
                    .receiverType(recipient.getReceiverType())
                    .encryptedReceipt(encryptedReceiptJson)
                    .encryptedSymmetricKey(encryptedSymmetricKey)
                    .supplierPartyId(supplierPartyId)
                    .build();

        } catch (Exception e) {
            throw new EncryptionException("Failed to HYBRID encrypt receipt for device: " + recipient.getId(), e);
        }
    }

    /**
     * Encrypts data with AES-GCM (for hybrid approach - currently disabled).
     */
    private EncryptedData encryptWithAES(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);

        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        // Log the AES key being used for encryption
        byte[] keyBytes = key.getEncoded();

        // In AES-GCM, doFinal returns ciphertext + tag combined
        byte[] ciphertextWithTag = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Separate ciphertext and tag for proper storage format
        byte[] ciphertext = new byte[ciphertextWithTag.length - GCM_TAG_LENGTH];
        byte[] tag = new byte[GCM_TAG_LENGTH];

        System.arraycopy(ciphertextWithTag, 0, ciphertext, 0, ciphertext.length);
        System.arraycopy(ciphertextWithTag, ciphertext.length, tag, 0, GCM_TAG_LENGTH);

        return new EncryptedData(ciphertext, iv, tag);
    }

    /**
     * Encrypts an AES key with RSA public key using explicit OAEP parameters for iOS compatibility.
     */
    private String encryptAESKeyWithRSA(SecretKey aesKey, String publicKeyString) throws Exception {
        // Parse public key
        PublicKey publicKey = parsePublicKey(publicKeyString);

        // Encrypt AES key with explicit OAEP parameters for iOS compatibility
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

        // Explicit OAEP parameters to match iOS .rsaEncryptionOAEPSHA256
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",                          // Hash algorithm
                "MGF1",                            // Mask generation function
                new MGF1ParameterSpec("SHA-256"),  // MGF1 hash algorithm
                PSource.PSpecified.DEFAULT         // Empty label (default)
        );

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        // Get raw AES key bytes (should be 32 bytes for AES-256)
        byte[] keyBytes = aesKey.getEncoded();

        // Encrypt the key
        byte[] encryptedKey = cipher.doFinal(keyBytes);

        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    /**
     * Parses a base64-encoded RSA public key string.
     * Handles both X.509 SubjectPublicKeyInfo format and raw RSA format (from iOS SecKeyCopyExternalRepresentation).
     */
    private PublicKey parsePublicKey(String publicKeyString) throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            // First, try X.509 SubjectPublicKeyInfo format (standard Java format)
            try {
                X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
                return keyFactory.generatePublic(x509Spec);
            } catch (Exception x509Exception) {
                // If X.509 fails, try raw RSA format (from iOS SecKeyCopyExternalRepresentation)
                return parseRawRSAPublicKey(keyBytes);
            }
            
        } catch (Exception e) {
            throw new Exception("Failed to parse RSA public key: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses a raw RSA public key (as exported by iOS SecKeyCopyExternalRepresentation).
     * The raw format contains the RSA key material directly without X.509 wrapper.
     */
    private PublicKey parseRawRSAPublicKey(byte[] keyBytes) throws Exception {
        // Raw RSA public key format from iOS contains:
        // - ASN.1 SEQUENCE containing modulus and exponent
        // We need to parse this manually and create an RSAPublicKeySpec
        
        try {
            // Parse the ASN.1 structure to extract modulus and exponent
            RSAKeyComponents components = parseRSAKeyComponents(keyBytes);
            
            // Create RSAPublicKeySpec from the components
            java.security.spec.RSAPublicKeySpec rsaSpec = new java.security.spec.RSAPublicKeySpec(
                components.modulus, 
                components.exponent
            );
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(rsaSpec);
            
        } catch (Exception e) {
            throw new Exception("Failed to parse raw RSA public key: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses RSA key components (modulus and exponent) from raw ASN.1 bytes.
     */
    private RSAKeyComponents parseRSAKeyComponents(byte[] keyBytes) throws Exception {
        int offset = 0;
        
        // Check for SEQUENCE tag (0x30)
        if (keyBytes[offset] != 0x30) {
            throw new Exception("Expected SEQUENCE tag at start of RSA key");
        }
        offset++;
        
        // Skip sequence length
        offset += getLengthBytes(keyBytes, offset);
        
        // Parse modulus (INTEGER)
        if (keyBytes[offset] != 0x02) {
            throw new Exception("Expected INTEGER tag for modulus");
        }
        offset++;
        
        int modulusLength = getLength(keyBytes, offset);
        offset += getLengthBytes(keyBytes, offset);
        
        byte[] modulusBytes = new byte[modulusLength];
        System.arraycopy(keyBytes, offset, modulusBytes, 0, modulusLength);
        offset += modulusLength;
        
        // Parse exponent (INTEGER)
        if (keyBytes[offset] != 0x02) {
            throw new Exception("Expected INTEGER tag for exponent");
        }
        offset++;
        
        int exponentLength = getLength(keyBytes, offset);
        offset += getLengthBytes(keyBytes, offset);
        
        byte[] exponentBytes = new byte[exponentLength];
        System.arraycopy(keyBytes, offset, exponentBytes, 0, exponentLength);
        
        return new RSAKeyComponents(
            new java.math.BigInteger(1, modulusBytes),
            new java.math.BigInteger(1, exponentBytes)
        );
    }
    
    /**
     * Gets the length value from ASN.1 length encoding.
     */
    private int getLength(byte[] data, int offset) {
        int length = data[offset] & 0xFF;
        if ((length & 0x80) == 0) {
            // Short form
            return length;
        } else {
            // Long form
            int numBytes = length & 0x7F;
            length = 0;
            for (int i = 1; i <= numBytes; i++) {
                length = (length << 8) | (data[offset + i] & 0xFF);
            }
            return length;
        }
    }
    
    /**
     * Gets the number of bytes used for length encoding.
     */
    private int getLengthBytes(byte[] data, int offset) {
        int length = data[offset] & 0xFF;
        if ((length & 0x80) == 0) {
            return 1; // Short form
        } else {
            return 1 + (length & 0x7F); // Long form
        }
    }
    
    /**
     * Container for RSA key components.
     */
    private static class RSAKeyComponents {
        final java.math.BigInteger modulus;
        final java.math.BigInteger exponent;
        
        RSAKeyComponents(java.math.BigInteger modulus, java.math.BigInteger exponent) {
            this.modulus = modulus;
            this.exponent = exponent;
        }
    }

    /**
     * Container for encrypted data with IV.
     */
    private static class EncryptedData {
        private final byte[] ciphertext;
        private final byte[] iv;
        private final byte[] tag;
    
        public EncryptedData(byte[] ciphertext, byte[] iv, byte[] tag) {
            this.ciphertext = ciphertext;
            this.iv = iv;
            this.tag = tag;
        }

        public String toBase64String() {
            // Combine IV + ciphertext + tag for storage (iOS expects this format)
            byte[] combined = new byte[iv.length + ciphertext.length + tag.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            System.arraycopy(tag, 0, combined, iv.length + ciphertext.length, tag.length);

            return Base64.getEncoder().encodeToString(combined);
        }
    }

    /**
     * Custom exception for encryption errors.
     */
    public static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}