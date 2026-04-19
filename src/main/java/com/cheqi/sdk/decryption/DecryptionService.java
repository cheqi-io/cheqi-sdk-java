package com.cheqi.sdk.decryption;

import com.cheqi.sdk.creditNote.EncryptedCreditNoteInitiationRequest;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteDto;
import com.cheqi.sdk.models.EncryptedReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

/**
 * Decryption service for Cheqi hybrid-encrypted content.
 *
 * This service orchestrates:
 * - RSA-OAEP decryption for AES keys
 * - AES-256-GCM decryption for content
 *
 * Compatible with content encrypted by the Cheqi EncryptionService.
 */
public class DecryptionService {
    private static final Logger logger = LoggerFactory.getLogger(DecryptionService.class);

    private final AESDecryptor aesDecryptor;
    private final RSAKeyDecryptor rsaKeyDecryptor;
    private final DecryptedReceiptFactory receiptFactory;

    public DecryptionService() {
        this.aesDecryptor = new AESDecryptor();
        this.rsaKeyDecryptor = new RSAKeyDecryptor();
        this.receiptFactory = new DecryptedReceiptFactory();
        logger.info("DecryptionService initialized with AES-256-GCM and RSA-OAEP");
    }

    /**
     * Decrypts a hybrid-encrypted receipt using the client's private key.
     *
     * This method handles the complete hybrid decryption process:
     * 1. Decrypts the AES symmetric key using RSA private key
     * 2. Decrypts the receipt content using the AES key
     * 3. Decrypts the receipt context (if present)
     *
     * @param encryptedReceipt The encrypted receipt DTO from Cheqi API or webhook
     * @param privateKeyBase64 The client's private key in PKCS#8 format
     * @return DecryptedReceipt containing both receipt content and receipt context
     * @throws DecryptionException if decryption fails
     */
    public DecryptedReceipt decryptReceipt(EncryptedReceipt encryptedReceipt, String privateKeyBase64) {
        try {
            logger.debug("Decrypting receipt envelope");
            SecretKey receiptAesKey = rsaKeyDecryptor.decryptKey(
                    encryptedReceipt.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );

            String decryptedReceiptContent = aesDecryptor.decrypt(
                    encryptedReceipt.getEncryptedReceipt(), 
                    receiptAesKey
            );

            String decryptedReceiptContext = null;
            if (hasReceiptContext(encryptedReceipt)) {
                logger.debug("Decrypting receipt context envelope");
                SecretKey customerAesKey = rsaKeyDecryptor.decryptKey(
                        encryptedReceipt.getEncryptedCustomerSymmetricKey(),
                        privateKeyBase64
                );

                decryptedReceiptContext = aesDecryptor.decrypt(
                        encryptedReceipt.getEncryptedCustomerDetails(),
                        customerAesKey
                );
            }

            return receiptFactory.create(decryptedReceiptContent, decryptedReceiptContext);

        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt receipt", e);
        }
    }

    /**
     * Decrypts a hybrid-encrypted credit note request using the issuer's private key.
     *
     * This method handles the complete hybrid decryption process:
     * 1. Decrypts the AES symmetric key using RSA private key
     * 2. Decrypts the credit note content using the AES key
     *
     * @param encryptedCreditNote The encrypted credit note from Cheqi API or webhook
     * @param privateKeyBase64 The issuer's private key in PKCS#8 format
     * @return DecryptedCreditNote containing the credit note request details (JSON)
     * @throws DecryptionException if decryption fails
     */
    public DecryptedCreditNote decryptCreditNote(
            EncryptedCreditNoteInitiationRequest encryptedCreditNote,
            String privateKeyBase64) {
        try {
            logger.debug("Decrypting credit note envelope for receipt: {}", encryptedCreditNote.getCheqiReceiptId());
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    encryptedCreditNote.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            
            String decryptedCreditNoteContent = aesDecryptor.decrypt(
                    encryptedCreditNote.getEncryptedCreditNoteInitiationRequest(),
                    aesKey
            );
            return new DecryptedCreditNote(decryptedCreditNoteContent);
        } catch (Exception e) {
            logger.error("Failed to decrypt credit note for receipt {}: {}", 
                        encryptedCreditNote.getCheqiReceiptId(), e.getMessage(), e);
            throw new DecryptionException("Failed to decrypt credit note", e);
        }
    }

    public DecryptedDeliveredCreditNote decryptDeliveredCreditNote(
            EncryptedCreditNoteDto encryptedCreditNote,
            String privateKeyBase64) {
        try {
            logger.debug("Decrypting delivered credit note envelope");
            SecretKey creditNoteAesKey = rsaKeyDecryptor.decryptKey(
                    encryptedCreditNote.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );

            String decryptedCreditNoteContent = aesDecryptor.decrypt(
                    encryptedCreditNote.getEncryptedCreditNote(),
                    creditNoteAesKey
            );

            String decryptedCreditNoteContext = null;
            if (encryptedCreditNote.getEncryptedCustomerDetails() != null
                    && !encryptedCreditNote.getEncryptedCustomerDetails().trim().isEmpty()
                    && encryptedCreditNote.getEncryptedCustomerAesKey() != null
                    && !encryptedCreditNote.getEncryptedCustomerAesKey().trim().isEmpty()) {
                SecretKey customerAesKey = rsaKeyDecryptor.decryptKey(
                        encryptedCreditNote.getEncryptedCustomerAesKey(),
                        privateKeyBase64
                );
                decryptedCreditNoteContext = aesDecryptor.decrypt(
                        encryptedCreditNote.getEncryptedCustomerDetails(),
                        customerAesKey
                );
            }

            return receiptFactory.createDeliveredCreditNote(decryptedCreditNoteContent, decryptedCreditNoteContext);
        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt delivered credit note", e);
        }
    }

    /**
     * Checks if the encrypted receipt contains receipt context.
     */
    private boolean hasReceiptContext(EncryptedReceipt encryptedReceipt) {
        return encryptedReceipt.getEncryptedCustomerDetails() != null &&
               !encryptedReceipt.getEncryptedCustomerDetails().trim().isEmpty() &&
               encryptedReceipt.getEncryptedCustomerSymmetricKey() != null;
    }
}
