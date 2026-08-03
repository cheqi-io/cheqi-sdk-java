package com.cheqi.sdk.encryption;

import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

/** Hybrid encryption used for merchant-to-owner-device receipt payloads. */
public class EncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private final AESEncryptor aesEncryptor;
    private final RSAKeyEncryptor rsaKeyEncryptor;
    private final AESKeyGenerator keyGenerator;

    public EncryptionService() {
        SecureRandom secureRandom = new SecureRandom();
        this.aesEncryptor = new AESEncryptor(secureRandom);
        this.rsaKeyEncryptor = new RSAKeyEncryptor();
        this.keyGenerator = new AESKeyGenerator(secureRandom);
    }

    /**
     * Encrypts the exact supplied JSON independently for one temporary device recipient.
     * No receipt fields are calculated, enriched, or modified.
     */
    public EncryptedReceiptPayload encryptForDevice(String plaintextJson, MatchedRecipient recipient) {
        requireRecipient(recipient);
        if (plaintextJson == null || plaintextJson.trim().isEmpty()) {
            throw new IllegalArgumentException("plaintextJson cannot be null or empty");
        }

        try {
            SecretKey aesKey = keyGenerator.generateKey();
            EncryptedData encryptedData = aesEncryptor.encrypt(plaintextJson, aesKey);
            String encryptedAesKey = rsaKeyEncryptor.encryptKey(aesKey, recipient.getPublicKey());

            return new EncryptedReceiptPayload()
                    .deviceRecipientId(recipient.getId())
                    .encryptedContent(encryptedData.toBase64String())
                    .encryptedAesKey(encryptedAesKey);
        } catch (Exception exception) {
            logger.error("Failed to encrypt payload for temporary recipient {}", recipient.getId());
            throw new EncryptionException(
                    "Failed to encrypt payload for temporary recipient " + recipient.getId(),
                    exception
            );
        }
    }

    /** Compatibility alias for receipt-specific callers. */
    public EncryptedReceiptPayload encryptReceiptForRecipient(
            String receiptPayloadJson,
            MatchedRecipient recipient
    ) {
        return encryptForDevice(receiptPayloadJson, recipient);
    }

    /** Compatibility alias for credit-note-specific callers. */
    public EncryptedReceiptPayload encryptCreditNoteForRecipient(
            String creditNotePayloadJson,
            MatchedRecipient recipient
    ) {
        return encryptForDevice(creditNotePayloadJson, recipient);
    }

    private static void requireRecipient(MatchedRecipient recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("recipient cannot be null");
        }
        if (recipient.getId() == null || recipient.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("recipient.id cannot be null or empty");
        }
        if (recipient.getPublicKey() == null || recipient.getPublicKey().trim().isEmpty()) {
            throw new IllegalArgumentException("recipient.publicKey cannot be null or empty");
        }
    }
}
