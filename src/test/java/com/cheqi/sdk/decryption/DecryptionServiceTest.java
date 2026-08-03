package com.cheqi.sdk.decryption;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.AESEncryptor;
import com.cheqi.sdk.encryption.AESKeyGenerator;
import com.cheqi.sdk.encryption.EncryptedData;
import com.cheqi.sdk.encryption.RSAKeyEncryptor;
import com.cheqi.sdk.models.generated.ReceiptDelivery;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecryptionServiceTest {

    @Test
    void decryptsCurrentReceiptDeliveryEnvelopeFields() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        ReceiptEnvelope expected = new ReceiptEnvelope()
                .cheqiReceiptId("CHQ-20260730-000001")
                .envelopeVersion(1)
                .receiptGeneratorVersion("0.2.1")
                .receiptUuid(UUID.randomUUID());
        String plaintext = ObjectMapperConfig.getInstance().writeValueAsString(expected);

        SecureRandom random = new SecureRandom();
        SecretKey aesKey = new AESKeyGenerator(random).generateKey();
        EncryptedData encrypted = new AESEncryptor(random).encrypt(plaintext, aesKey);

        ReceiptDelivery delivery = new ReceiptDelivery()
                .encryptedEnvelope(encrypted.toBase64String())
                .encryptedEnvelopeKey(new RSAKeyEncryptor().encryptKey(aesKey, publicKey));

        ReceiptEnvelope actual = new DecryptionService().decryptReceipt(delivery, privateKey);

        assertEquals(expected, actual);
    }

    @Test
    void rejectsDeliveryWithoutCurrentEnvelopeFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new DecryptionService().decryptReceipt(new ReceiptDelivery(), "private-key"));
    }
}
