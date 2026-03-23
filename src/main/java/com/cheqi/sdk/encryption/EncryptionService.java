package com.cheqi.sdk.encryption;

import com.cheqi.sdk.models.generated.EncryptedCreditNote;
import com.cheqi.sdk.models.generated.EncryptedReceiptRequest;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * Core encryption service providing hybrid encryption for end-to-end security.
 *
 * Orchestrates AES-256-GCM encryption for data and RSA-OAEP encryption for keys.
 * Thread-safe and designed for high-throughput receipt encryption.
 */
public class EncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private final SecureRandom secureRandom;
    private final AESEncryptor aesEncryptor;
    private final RSAKeyEncryptor rsaKeyEncryptor;
    private final AESKeyGenerator keyGenerator;

    public EncryptionService() {
        this.secureRandom = new SecureRandom();
        this.aesEncryptor = new AESEncryptor(secureRandom);
        this.rsaKeyEncryptor = new RSAKeyEncryptor();
        this.keyGenerator = new AESKeyGenerator(secureRandom);
        logger.info("EncryptionService initialized with AES-256-GCM and RSA-OAEP");
    }

    /**
     * Encrypts a receipt for a recipient using hybrid encryption.
     * Generates a unique AES key encrypted with the recipient's RSA public key.
     *
     * @param purchaseReceipt The JSON receipt to encrypt
     * @param recipient Recipient with public key
     * @return Encrypted receipt
     */
    public EncryptedReceiptRequest encryptReceiptForRecipients(String purchaseReceipt, MatchedRecipient recipient) {
        try {
            logger.debug("Encrypting receipt for recipient: {}", recipient.getId());

            SecretKey aesKey = keyGenerator.generateKey();
            EncryptedData encryptedData = aesEncryptor.encrypt(purchaseReceipt, aesKey);
            String encryptedSymmetricKey = rsaKeyEncryptor.encryptKey(aesKey, recipient.getPublicKey());

            EncryptedReceiptRequest dto = new EncryptedReceiptRequest();
            dto.setRecipientId(recipient.getId());
            dto.setPublicKey(recipient.getPublicKey());
            dto.setEncryptedReceipt(encryptedData.toBase64String());
            dto.setEncryptedSymmetricKey(encryptedSymmetricKey);
            return dto;

        } catch (Exception e) {
            logger.error("Failed to encrypt receipt for recipient {}: {}", recipient.getId(), e.getMessage());
            throw new EncryptionException("Failed to encrypt receipt for recipient: " + recipient.getId(), e);
        }
    }

    /**
     * Encrypts a credit note for a recipient using hybrid encryption.
     * Generates a unique AES key encrypted with the recipient's RSA public key.
     *
     * @param creditNote The JSON credit note to encrypt
     * @param recipient Recipient with public key
     * @return Encrypted credit note DTO
     */
    public EncryptedCreditNote encryptCreditNoteForRecipient(String creditNote, MatchedRecipient recipient) {
        try {
            logger.debug("Encrypting credit note for recipient: {}", recipient.getId());

            SecretKey aesKey = keyGenerator.generateKey();
            EncryptedData encryptedData = aesEncryptor.encrypt(creditNote, aesKey);
            String encryptedSymmetricKey = rsaKeyEncryptor.encryptKey(aesKey, recipient.getPublicKey());

            EncryptedCreditNote dto = new EncryptedCreditNote();
            dto.setRecipientId(recipient.getId());
            dto.setPublicKey(recipient.getPublicKey());
            dto.setEncryptedCreditNote(encryptedData.toBase64String());
            dto.setEncryptedSymmetricKey(encryptedSymmetricKey);
            return dto;

        } catch (Exception e) {
            logger.error("Failed to encrypt credit note for recipient {}: {}", recipient.getId(), e.getMessage());
            throw new EncryptionException("Failed to encrypt credit note for recipient: " + recipient.getId(), e);
        }
    }
}
