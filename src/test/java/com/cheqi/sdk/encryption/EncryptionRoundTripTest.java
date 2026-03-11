package com.cheqi.sdk.encryption;

import com.cheqi.sdk.decryption.AESDecryptor;
import com.cheqi.sdk.decryption.RSAKeyDecryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionRoundTripTest {

    private static String publicKeyBase64;
    private static String privateKeyBase64;

    @BeforeAll
    static void generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        publicKeyBase64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        privateKeyBase64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
    }

    @Test
    void aesEncryptDecryptRoundTrip() {
        SecureRandom random = new SecureRandom();
        AESKeyGenerator keyGen = new AESKeyGenerator(random);
        AESEncryptor encryptor = new AESEncryptor(random);
        AESDecryptor decryptor = new AESDecryptor();

        SecretKey key = keyGen.generateKey();
        String plaintext = "Hello, Cheqi! This is a test receipt with special chars: {}\u20ac\u00e9";

        EncryptedData encrypted = encryptor.encrypt(plaintext, key);
        String decrypted = decryptor.decrypt(encrypted.toBase64String(), key);

        assertEquals(plaintext, decrypted);
    }

    @Test
    void rsaKeyEncryptDecryptRoundTrip() {
        SecureRandom random = new SecureRandom();
        AESKeyGenerator keyGen = new AESKeyGenerator(random);
        RSAKeyEncryptor rsaEncryptor = new RSAKeyEncryptor();
        RSAKeyDecryptor rsaDecryptor = new RSAKeyDecryptor();

        SecretKey originalKey = keyGen.generateKey();
        String encryptedKeyBase64 = rsaEncryptor.encryptKey(originalKey, publicKeyBase64);
        SecretKey decryptedKey = rsaDecryptor.decryptKey(encryptedKeyBase64, privateKeyBase64);

        assertArrayEquals(originalKey.getEncoded(), decryptedKey.getEncoded());
    }

    @Test
    void fullEncryptionServiceDecryptionServiceRoundTrip() {
        SecureRandom random = new SecureRandom();
        AESKeyGenerator keyGen = new AESKeyGenerator(random);
        AESEncryptor aesEncryptor = new AESEncryptor(random);
        RSAKeyEncryptor rsaEncryptor = new RSAKeyEncryptor();
        AESDecryptor aesDecryptor = new AESDecryptor();
        RSAKeyDecryptor rsaDecryptor = new RSAKeyDecryptor();

        String originalReceipt = "{\"documentNumber\":\"INV-001\",\"totalAmount\":99.95}";

        // Encrypt: AES key -> encrypt data -> encrypt key with RSA
        SecretKey aesKey = keyGen.generateKey();
        EncryptedData encryptedData = aesEncryptor.encrypt(originalReceipt, aesKey);
        String encryptedReceiptBase64 = encryptedData.toBase64String();
        String encryptedSymmetricKey = rsaEncryptor.encryptKey(aesKey, publicKeyBase64);

        // Decrypt: RSA decrypt key -> AES decrypt data
        SecretKey decryptedAesKey = rsaDecryptor.decryptKey(encryptedSymmetricKey, privateKeyBase64);
        String decryptedReceipt = aesDecryptor.decrypt(encryptedReceiptBase64, decryptedAesKey);

        assertEquals(originalReceipt, decryptedReceipt);
    }

    @Test
    void eachEncryptionProducesUniqueCiphertext() {
        SecureRandom random = new SecureRandom();
        AESKeyGenerator keyGen = new AESKeyGenerator(random);
        AESEncryptor encryptor = new AESEncryptor(random);

        String plaintext = "same content";
        SecretKey key = keyGen.generateKey();

        String ct1 = encryptor.encrypt(plaintext, key).toBase64String();
        String ct2 = encryptor.encrypt(plaintext, key).toBase64String();

        assertNotEquals(ct1, ct2, "Each encryption must produce unique ciphertext due to random IV");
    }

    @Test
    void tamperedCiphertextFailsDecryption() {
        SecureRandom random = new SecureRandom();
        AESKeyGenerator keyGen = new AESKeyGenerator(random);
        AESEncryptor encryptor = new AESEncryptor(random);
        AESDecryptor decryptor = new AESDecryptor();

        SecretKey key = keyGen.generateKey();
        EncryptedData encrypted = encryptor.encrypt("sensitive data", key);
        String base64 = encrypted.toBase64String();

        // Tamper with the ciphertext
        byte[] raw = Base64.getDecoder().decode(base64);
        raw[raw.length / 2] ^= 0xFF;
        String tampered = Base64.getEncoder().encodeToString(raw);

        assertThrows(Exception.class, () -> decryptor.decrypt(tampered, key));
    }
}
