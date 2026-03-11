package com.cheqi.sdk.decryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import com.cheqi.sdk.utils.PemUtils;

/**
 * Decrypts AES keys using RSA-OAEP with a private key.
 */
public class RSAKeyDecryptor {
    private static final Logger logger = LoggerFactory.getLogger(RSAKeyDecryptor.class);
    private static final String AES_ALGORITHM = "AES";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * Decrypts an AES key that was encrypted with RSA-OAEP.
     *
     * @param encryptedKeyBase64 Base64-encoded encrypted AES key
     * @param privateKeyBase64 Base64-encoded RSA private key in PKCS#8 format
     * @return Decrypted AES SecretKey
     * @throws DecryptionException if decryption fails
     */
    public SecretKey decryptKey(String encryptedKeyBase64, String privateKeyBase64) {
        try {
            logger.info("🔐 Starting RSA-OAEP AES key decryption");
            logger.info("🔑 Encrypted key Base64 length: {} characters", encryptedKeyBase64.length());
            logger.info("🔑 Encrypted key Base64 (first 100 chars): {}...", encryptedKeyBase64.substring(0, Math.min(100, encryptedKeyBase64.length())));
            
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            logger.info("✅ Decoded encrypted key: {} bytes", encryptedKeyBytes.length);
            logger.info("🔍 Encrypted key (hex, first 32 bytes): {}", bytesToHex(encryptedKeyBytes, 32));
            
            PrivateKey privateKey = parsePrivateKey(privateKeyBase64);
            logger.info("✅ Parsed private key successfully");

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            logger.info("🔧 Using RSA transformation: {}", RSA_TRANSFORMATION);

            // Explicit OAEP parameters matching encryption
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    new MGF1ParameterSpec("SHA-256"),
                    PSource.PSpecified.DEFAULT
            );
            logger.info("🔧 OAEP parameters: hash=SHA-256, mgf=MGF1(SHA-256), label=empty");

            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            logger.info("✅ Cipher initialized in DECRYPT_MODE");

            byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
            logger.info("✅ RSA decryption successful: {} bytes", decryptedKeyBytes.length);
            logger.info("🔍 Decrypted AES key (hex, first 16 bytes): {}", bytesToHex(decryptedKeyBytes, 16));
            
            if (decryptedKeyBytes.length != 32) {
                logger.error("❌ Decrypted AES key size mismatch: expected 32 bytes, got {}", decryptedKeyBytes.length);
                throw new DecryptionException("Decrypted AES key is not 32 bytes: " + decryptedKeyBytes.length);
            }

            SecretKey secretKey = new SecretKeySpec(decryptedKeyBytes, AES_ALGORITHM);
            logger.info("✅ Created AES SecretKey successfully");
            return secretKey;

        } catch (Exception e) {
            logger.error("❌ RSA key decryption failed: {}", e.getMessage(), e);
            throw new DecryptionException("Failed to decrypt AES key with RSA", e);
        }
    }

    /**
     * Parses a Base64-encoded PKCS#8 private key.
     */
    private PrivateKey parsePrivateKey(String privateKeyBase64) {
        try {
            logger.debug("🔑 Parsing private key from Base64 (PKCS#8 format)");
            byte[] keyBytes = PemUtils.decodeKey(privateKeyBase64);
            logger.debug("✅ Decoded private key: {} bytes", keyBytes.length);
            
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            logger.debug("✅ Generated RSA private key");
            return privateKey;
        } catch (Exception e) {
            logger.error("❌ Failed to parse RSA private key: {}", e.getMessage());
            throw new DecryptionException("Failed to parse RSA private key", e);
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
