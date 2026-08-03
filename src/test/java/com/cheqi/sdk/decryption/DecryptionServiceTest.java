package com.cheqi.sdk.decryption;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.creditNote.CreditNoteInitiationRequest;
import com.cheqi.sdk.creditNote.RefundPreference;
import com.cheqi.sdk.creditNote.ReturnLineItem;
import com.cheqi.sdk.creditNote.ReturnReasonCode;
import com.cheqi.sdk.encryption.AESEncryptor;
import com.cheqi.sdk.encryption.AESKeyGenerator;
import com.cheqi.sdk.encryption.EncryptedData;
import com.cheqi.sdk.encryption.RSAKeyEncryptor;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteInitiationRequestResponse;
import com.cheqi.sdk.models.generated.EncryptedReceiptDeliveryResponse;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.WebhookCreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.WebhookReceiptEnvelope;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecryptionServiceTest {

    @Test
    void decryptsQueuedAndWebhookReceiptEnvelopes() throws Exception {
        ReceiptEnvelope expected = new ReceiptEnvelope()
                .cheqiReceiptId("CHQ-20260730-000001")
                .envelopeVersion(1)
                .receiptGeneratorVersion("0.2.1")
                .receiptUuid(UUID.randomUUID());
        EncryptionFixture encrypted = encrypt(expected);

        EncryptedReceiptDeliveryResponse queuedDelivery = new EncryptedReceiptDeliveryResponse()
                .encryptedEnvelope(encrypted.ciphertext)
                .encryptedEnvelopeKey(encrypted.encryptedKey);
        WebhookReceiptEnvelope webhookDelivery = new WebhookReceiptEnvelope()
                .encryptedEnvelope(encrypted.ciphertext)
                .encryptedEnvelopeKey(encrypted.encryptedKey);

        DecryptionService service = new DecryptionService();

        assertEquals(expected, service.decryptReceipt(queuedDelivery, encrypted.privateKey));
        assertEquals(expected, service.decryptReceipt(webhookDelivery, encrypted.privateKey));
    }

    @Test
    void rejectsDeliveryWithoutCurrentEnvelopeFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new DecryptionService().decryptReceipt(
                        new EncryptedReceiptDeliveryResponse(),
                        "private-key"
                ));
    }

    @Test
    void decryptsQueuedAndWebhookCreditNoteInitiationRequests() throws Exception {
        CreditNoteInitiationRequest expected = CreditNoteInitiationRequest.builder()
                .cheqiReceiptId("CHQ-20260730-000001")
                .receiptId("merchant-receipt-1")
                .lineItems(List.of(
                        new ReturnLineItem(
                                "SKU-1",
                                BigDecimal.ONE,
                                ReturnReasonCode.DAMAGED,
                                "Seal broken"
                        )
                ))
                .refundPreference(RefundPreference.ORIGINAL_PAYMENT_METHOD)
                .build();
        EncryptionFixture encrypted = encrypt(expected);

        EncryptedCreditNoteInitiationRequestResponse queuedRequest =
                new EncryptedCreditNoteInitiationRequestResponse()
                        .encryptedCreditNoteInitiationRequest(encrypted.ciphertext)
                        .encryptedSymmetricKey(encrypted.encryptedKey);
        WebhookCreditNoteInitiationRequest webhookRequest =
                new WebhookCreditNoteInitiationRequest()
                        .encryptedCreditNoteInitiationRequest(encrypted.ciphertext)
                        .encryptedSymmetricKey(encrypted.encryptedKey);

        DecryptionService service = new DecryptionService();
        CreditNoteInitiationRequest queued = service.decryptCreditNoteInitiationRequest(
                queuedRequest,
                encrypted.privateKey
        );
        CreditNoteInitiationRequest webhook = service.decryptCreditNoteInitiationRequest(
                webhookRequest,
                encrypted.privateKey
        );

        assertEquals(expected.getCheqiReceiptId(), queued.getCheqiReceiptId());
        assertEquals(expected.getReceiptId(), queued.getReceiptId());
        assertEquals(
                expected.getLineItems().get(0).getProductId(),
                queued.getLineItems().get(0).getProductId()
        );
        assertEquals(expected.getCheqiReceiptId(), webhook.getCheqiReceiptId());
        assertEquals(expected.getReceiptId(), webhook.getReceiptId());
    }

    private EncryptionFixture encrypt(Object plaintext) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String json = ObjectMapperConfig.getInstance().writeValueAsString(plaintext);

        SecureRandom random = new SecureRandom();
        SecretKey aesKey = new AESKeyGenerator(random).generateKey();
        EncryptedData encryptedData = new AESEncryptor(random).encrypt(json, aesKey);
        String encryptedKey = new RSAKeyEncryptor().encryptKey(aesKey, publicKey);
        return new EncryptionFixture(encryptedData.toBase64String(), encryptedKey, privateKey);
    }

    private static final class EncryptionFixture {
        private final String ciphertext;
        private final String encryptedKey;
        private final String privateKey;

        private EncryptionFixture(String ciphertext, String encryptedKey, String privateKey) {
            this.ciphertext = ciphertext;
            this.encryptedKey = encryptedKey;
            this.privateKey = privateKey;
        }
    }
}
