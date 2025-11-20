package com.cheqi.sdk.decryption;

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

/**
 * Decrypts AES keys using RSA-OAEP with a private key.
 */
public class RSAKeyDecryptor {
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
            PrivateKey privateKey = parsePrivateKey(privateKeyBase64);

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

            // Explicit OAEP parameters matching encryption
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    new MGF1ParameterSpec("SHA-256"),
                    PSource.PSpecified.DEFAULT
            );

            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

            return new SecretKeySpec(decryptedKeyBytes, AES_ALGORITHM);

        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt AES key with RSA", e);
        }
    }

    /**
     * Parses a Base64-encoded PKCS#8 private key.
     */
    private PrivateKey parsePrivateKey(String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new DecryptionException("Failed to parse RSA private key", e);
        }
    }
}
