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
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            logger.debug("Starting RSA-OAEP AES key decryption (ciphertext={} bytes)", encryptedKeyBytes.length);
            
            PrivateKey privateKey = parsePrivateKey(privateKeyBase64);

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    new MGF1ParameterSpec("SHA-256"),
                    PSource.PSpecified.DEFAULT
            );

            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

            byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
            logger.debug("RSA-OAEP key decryption completed ({} bytes)", decryptedKeyBytes.length);
            
            if (decryptedKeyBytes.length != 32) {
                logger.error("Decrypted AES key size mismatch: expected 32 bytes, got {}", decryptedKeyBytes.length);
                throw new DecryptionException("Decrypted AES key is not 32 bytes: " + decryptedKeyBytes.length);
            }

            return new SecretKeySpec(decryptedKeyBytes, AES_ALGORITHM);

        } catch (Exception e) {
            logger.error("RSA key decryption failed: {}", e.getMessage(), e);
            throw new DecryptionException("Failed to decrypt AES key with RSA", e);
        }
    }

    /**
     * Parses a Base64-encoded PKCS#8 private key.
     */
    private PrivateKey parsePrivateKey(String privateKeyBase64) {
        try {
            byte[] keyBytes = PemUtils.decodeKey(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            logger.error("Failed to parse RSA private key: {}", e.getMessage());
            throw new DecryptionException("Failed to parse RSA private key", e);
        }
    }
}
