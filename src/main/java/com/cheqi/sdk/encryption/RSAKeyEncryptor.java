package com.cheqi.sdk.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

/**
 * Encrypts AES keys with RSA-OAEP for hybrid encryption.
 */
public class RSAKeyEncryptor {
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private final RSAPublicKeyParser keyParser;

    public RSAKeyEncryptor() {
        this.keyParser = new RSAPublicKeyParser();
    }

    /**
     * Encrypts an AES key with RSA-OAEP using the recipient's public key.
     */
    public String encryptKey(SecretKey aesKey, String publicKeyBase64) {
        try {
            var publicKey = keyParser.parse(publicKeyBase64);

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);

            // Explicit OAEP parameters for iOS compatibility
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    new MGF1ParameterSpec("SHA-256"),
                    PSource.PSpecified.DEFAULT
            );

            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
            byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());

            return Base64.getEncoder().encodeToString(encryptedKey);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt AES key with RSA", e);
        }
    }
}
