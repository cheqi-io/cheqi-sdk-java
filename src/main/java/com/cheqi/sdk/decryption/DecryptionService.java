package com.cheqi.sdk.decryption;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.EncryptedReceiptDeliveryResponse;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;

/** Decrypts complete recipient envelopes with the recipient's private key. */
public class DecryptionService {
    private final AESDecryptor aesDecryptor = new AESDecryptor();
    private final RSAKeyDecryptor rsaKeyDecryptor = new RSAKeyDecryptor();
    private final ObjectMapper objectMapper = ObjectMapperConfig.getInstance();

    public ReceiptEnvelope decryptReceipt(
            EncryptedReceiptDeliveryResponse delivery,
            String privateKeyBase64
    ) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery cannot be null");
        }
        try {
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    delivery.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            String plaintext = aesDecryptor.decrypt(delivery.getEncryptedReceipt(), aesKey);
            return objectMapper.readValue(plaintext, ReceiptEnvelope.class);
        } catch (Exception exception) {
            throw new DecryptionException("Failed to decrypt receipt envelope", exception);
        }
    }

    public String decryptCreditNoteInitiation(
            EncryptedCreditNoteInitiationRequest request,
            String privateKeyBase64
    ) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        try {
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    request.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            return aesDecryptor.decrypt(
                    request.getEncryptedCreditNoteInitiationRequest(),
                    aesKey
            );
        } catch (Exception exception) {
            throw new DecryptionException(
                    "Failed to decrypt credit-note initiation request",
                    exception
            );
        }
    }
}
