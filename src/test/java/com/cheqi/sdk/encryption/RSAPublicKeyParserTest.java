package com.cheqi.sdk.encryption;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class RSAPublicKeyParserTest {

    private final RSAPublicKeyParser parser = new RSAPublicKeyParser();

    @Test
    void parseX509FormatKey() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        String base64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());

        PublicKey parsed = parser.parse(base64);

        assertNotNull(parsed);
        assertEquals("RSA", parsed.getAlgorithm());
        assertInstanceOf(RSAPublicKey.class, parsed);
    }

    @Test
    void parseRawIosRsaFormat() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Extract raw RSA key (strip X.509 wrapper) — the X.509 SubjectPublicKeyInfo
        // wraps the raw PKCS#1 RSAPublicKey with an AlgorithmIdentifier prefix.
        // The raw key starts at a fixed offset for RSA 2048.
        byte[] x509Bytes = kp.getPublic().getEncoded();
        // X.509 header for RSA 2048 is 24 bytes; raw RSA key follows
        byte[] rawKey = new byte[x509Bytes.length - 24];
        System.arraycopy(x509Bytes, 24, rawKey, 0, rawKey.length);
        String rawBase64 = Base64.getEncoder().encodeToString(rawKey);

        PublicKey parsed = parser.parse(rawBase64);

        assertNotNull(parsed);
        assertEquals("RSA", parsed.getAlgorithm());
        // Verify the modulus matches
        RSAPublicKey original = (RSAPublicKey) kp.getPublic();
        RSAPublicKey parsedRsa = (RSAPublicKey) parsed;
        assertEquals(original.getModulus(), parsedRsa.getModulus());
        assertEquals(original.getPublicExponent(), parsedRsa.getPublicExponent());
    }

    @Test
    void invalidInputThrowsEncryptionException() {
        assertThrows(EncryptionException.class, () -> parser.parse("notvalidbase64!!!"));
        assertThrows(EncryptionException.class,
                () -> parser.parse(Base64.getEncoder().encodeToString(new byte[]{0x01, 0x02, 0x03})));
    }

    @Test
    void parsedKeyUsableForEncryption() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        String base64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());

        PublicKey parsed = parser.parse(base64);

        // Use the parsed key to encrypt something with RSA
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        assertDoesNotThrow(() -> cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, parsed));
    }
}
