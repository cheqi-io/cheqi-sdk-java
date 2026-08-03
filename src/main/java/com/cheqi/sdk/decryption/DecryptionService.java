package com.cheqi.sdk.decryption;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.creditNote.CreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteInitiationRequestResponse;
import com.cheqi.sdk.models.generated.EncryptedReceiptDeliveryResponse;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.WebhookCreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.WebhookReceiptEnvelope;
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
        return decryptReceiptEnvelope(
                delivery.getEncryptedEnvelope(),
                delivery.getEncryptedEnvelopeKey(),
                privateKeyBase64
        );
    }

    public ReceiptEnvelope decryptReceipt(
            WebhookReceiptEnvelope delivery,
            String privateKeyBase64
    ) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery cannot be null");
        }
        return decryptReceiptEnvelope(
                delivery.getEncryptedEnvelope(),
                delivery.getEncryptedEnvelopeKey(),
                privateKeyBase64
        );
    }

    private ReceiptEnvelope decryptReceiptEnvelope(
            String encryptedEnvelope,
            String encryptedEnvelopeKey,
            String privateKeyBase64
    ) {
        requireText(encryptedEnvelope, "delivery.encryptedEnvelope");
        requireText(encryptedEnvelopeKey, "delivery.encryptedEnvelopeKey");
        try {
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    encryptedEnvelopeKey,
                    privateKeyBase64
            );
            String plaintext = aesDecryptor.decrypt(encryptedEnvelope, aesKey);
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
        return decryptCreditNoteInitiation(
                request.getEncryptedCreditNoteInitiationRequest(),
                request.getEncryptedSymmetricKey(),
                privateKeyBase64
        );
    }

    public String decryptCreditNoteInitiation(
            EncryptedCreditNoteInitiationRequestResponse request,
            String privateKeyBase64
    ) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        return decryptCreditNoteInitiation(
                request.getEncryptedCreditNoteInitiationRequest(),
                request.getEncryptedSymmetricKey(),
                privateKeyBase64
        );
    }

    public String decryptCreditNoteInitiation(
            WebhookCreditNoteInitiationRequest request,
            String privateKeyBase64
    ) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        return decryptCreditNoteInitiation(
                request.getEncryptedCreditNoteInitiationRequest(),
                request.getEncryptedSymmetricKey(),
                privateKeyBase64
        );
    }

    private String decryptCreditNoteInitiation(
            String encryptedRequest,
            String encryptedSymmetricKey,
            String privateKeyBase64
    ) {
        requireText(encryptedRequest, "request.encryptedCreditNoteInitiationRequest");
        requireText(encryptedSymmetricKey, "request.encryptedSymmetricKey");
        try {
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    encryptedSymmetricKey,
                    privateKeyBase64
            );
            return aesDecryptor.decrypt(encryptedRequest, aesKey);
        } catch (Exception exception) {
            throw new DecryptionException(
                    "Failed to decrypt credit-note initiation request",
                    exception
            );
        }
    }

    /**
     * Decrypts, deserializes, and validates a customer return request.
     */
    public CreditNoteInitiationRequest decryptCreditNoteInitiationRequest(
            EncryptedCreditNoteInitiationRequest request,
            String privateKeyBase64
    ) {
        return deserializeCreditNoteInitiationRequest(
                decryptCreditNoteInitiation(request, privateKeyBase64)
        );
    }

    public CreditNoteInitiationRequest decryptCreditNoteInitiationRequest(
            EncryptedCreditNoteInitiationRequestResponse request,
            String privateKeyBase64
    ) {
        return deserializeCreditNoteInitiationRequest(
                decryptCreditNoteInitiation(request, privateKeyBase64)
        );
    }

    public CreditNoteInitiationRequest decryptCreditNoteInitiationRequest(
            WebhookCreditNoteInitiationRequest request,
            String privateKeyBase64
    ) {
        return deserializeCreditNoteInitiationRequest(
                decryptCreditNoteInitiation(request, privateKeyBase64)
        );
    }

    private CreditNoteInitiationRequest deserializeCreditNoteInitiationRequest(String plaintext) {
        try {
            CreditNoteInitiationRequest initiationRequest = objectMapper.readValue(
                    plaintext,
                    CreditNoteInitiationRequest.class
            );
            initiationRequest.validate();
            return initiationRequest;
        } catch (Exception exception) {
            throw new DecryptionException(
                    "Failed to deserialize credit-note initiation request",
                    exception
            );
        }
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or empty");
        }
    }
}
