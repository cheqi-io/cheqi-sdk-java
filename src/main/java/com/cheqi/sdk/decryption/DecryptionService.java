package com.cheqi.sdk.decryption;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.sdk.creditNote.EncryptedCreditNoteRequest;

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

    public DecryptionService() {
        this.aesDecryptor = new AESDecryptor();
        this.rsaKeyDecryptor = new RSAKeyDecryptor();
        logger.info("DecryptionService initialized with AES-256-GCM and RSA-OAEP");
    }

    /**
     * Decrypts a hybrid-encrypted receipt using the client's private key.
     *
     * This method handles the complete hybrid decryption process:
     * 1. Decrypts the AES symmetric key using RSA private key
     * 2. Decrypts the receipt content using the AES key
     * 3. Decrypts the customer details (if present)
     *
     * @param encryptedReceipt The encrypted receipt DTO from Cheqi API or webhook
     * @param privateKeyBase64 The client's private key in Base64 PKCS#8 format
     * @return DecryptedReceipt containing both receipt content and customer details
     * @throws DecryptionException if decryption fails
     */
    public DecryptedReceipt decryptReceipt(EncryptedReceiptDto encryptedReceipt, String privateKeyBase64) {
        logger.debug("Decrypting receipt for recipient: {}", encryptedReceipt.getRecipientId());
        
        try {
            // Step 1: Decrypt the receipt AES key using RSA private key
            SecretKey receiptAesKey = rsaKeyDecryptor.decryptKey(
                    encryptedReceipt.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );

            // Step 2: Decrypt the receipt content using the receipt AES key
            String decryptedReceiptContent = aesDecryptor.decrypt(
                    encryptedReceipt.getEncryptedReceipt(), 
                    receiptAesKey
            );

            // Step 3: Decrypt customer details using separate customer AES key (if present)
            String decryptedCustomerDetails = null;
            if (hasCustomerDetails(encryptedReceipt)) {
                logger.debug("Decrypting customer details");
                
                SecretKey customerAesKey = rsaKeyDecryptor.decryptKey(
                        encryptedReceipt.getEncryptedCustomerAesKey(),
                        privateKeyBase64
                );

                decryptedCustomerDetails = aesDecryptor.decrypt(
                        encryptedReceipt.getEncryptedCustomerDetails(),
                        customerAesKey
                );
            }

            logger.info("Successfully decrypted receipt for recipient: {}", encryptedReceipt.getRecipientId());
            return new DecryptedReceipt(decryptedReceiptContent, decryptedCustomerDetails);

        } catch (Exception e) {
            logger.error("Failed to decrypt receipt: {}", e.getMessage());
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
     * @param privateKeyBase64 The issuer's private key in Base64 PKCS#8 format
     * @return DecryptedCreditNote containing the credit note request details (JSON)
     * @throws DecryptionException if decryption fails
     */
    public DecryptedCreditNote decryptCreditNote(
            EncryptedCreditNoteRequest encryptedCreditNote, 
            String privateKeyBase64) {
        logger.debug("Decrypting credit note for receipt: {}", encryptedCreditNote.getCheqiReceiptId());
        
        try {
            // Step 1: Decrypt the AES key using RSA private key
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    encryptedCreditNote.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            // Step 2: Decrypt the credit note content using the AES key
            String decryptedCreditNoteContent = aesDecryptor.decrypt(
                    encryptedCreditNote.getEncryptedCreditRequest(), 
                    aesKey
            );
            logger.info("Successfully decrypted credit note for receipt: {}", 
                    encryptedCreditNote.getCheqiReceiptId());
            return new DecryptedCreditNote(decryptedCreditNoteContent);
        } catch (Exception e) {
            logger.error("Failed to decrypt credit note: {}", e.getMessage());
            throw new DecryptionException("Failed to decrypt credit note", e);
        }
    }

    /**
     * Checks if the encrypted receipt contains customer details.
     */
    private boolean hasCustomerDetails(EncryptedReceiptDto encryptedReceipt) {
        return encryptedReceipt.getEncryptedCustomerDetails() != null &&
               !encryptedReceipt.getEncryptedCustomerDetails().trim().isEmpty() &&
               encryptedReceipt.getEncryptedCustomerAesKey() != null;
    }
}