package com.cheqi.sdk.decryption;

import com.cheqi.sdk.creditNote.EncryptedCreditNoteInitiationRequest;
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
    public DecryptedReceipt decryptReceipt(EncryptedReceipt encryptedReceipt, String privateKeyBase64) {
        logger.info("📦 Starting receipt decryption for recipient: {}", encryptedReceipt.getRecipientId());
        logger.info("🔍 Encrypted receipt length: {} characters", encryptedReceipt.getEncryptedReceipt().length());
        logger.info("🔍 Encrypted symmetric key length: {} characters", encryptedReceipt.getEncryptedSymmetricKey().length());
        
        try {
            // Step 1: Decrypt the receipt AES key using RSA private key
            logger.info("🔐 Step 1: Decrypting receipt AES key with RSA-OAEP...");
            SecretKey receiptAesKey = rsaKeyDecryptor.decryptKey(
                    encryptedReceipt.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            logger.info("✅ Step 1 complete: Receipt AES key decrypted");

            // Step 2: Decrypt the receipt content using the receipt AES key
            logger.info("🔐 Step 2: Decrypting receipt content with AES-GCM...");
            String decryptedReceiptContent = aesDecryptor.decrypt(
                    encryptedReceipt.getEncryptedReceipt(), 
                    receiptAesKey
            );
            logger.info("✅ Step 2 complete: Receipt content decrypted ({} characters)", decryptedReceiptContent.length());

            // Step 3: Decrypt customer details using separate customer AES key (if present)
            String decryptedCustomerDetails = null;
            if (hasCustomerDetails(encryptedReceipt)) {
                logger.info("🔐 Step 3: Decrypting customer details...");
                
                SecretKey customerAesKey = rsaKeyDecryptor.decryptKey(
                        encryptedReceipt.getEncryptedCustomerAesKey(),
                        privateKeyBase64
                );

                decryptedCustomerDetails = aesDecryptor.decrypt(
                        encryptedReceipt.getEncryptedCustomerDetails(),
                        customerAesKey
                );
                logger.info("✅ Step 3 complete: Customer details decrypted ({} characters)", decryptedCustomerDetails.length());
            } else {
                logger.info("ℹ️ Step 3 skipped: No customer details present");
            }

            logger.info("✅ Successfully decrypted receipt for recipient: {}", encryptedReceipt.getRecipientId());
            logger.info("📊 Decryption summary: receipt={} chars, customer={} chars", 
                       decryptedReceiptContent.length(), 
                       decryptedCustomerDetails != null ? decryptedCustomerDetails.length() : 0);
            return new DecryptedReceipt(decryptedReceiptContent, decryptedCustomerDetails);

        } catch (Exception e) {
            logger.error("❌ Failed to decrypt receipt for recipient {}: {}", 
                        encryptedReceipt.getRecipientId(), e.getMessage(), e);
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
            EncryptedCreditNoteInitiationRequest encryptedCreditNote,
            String privateKeyBase64) {
        logger.info("📦 Starting credit note decryption for receipt: {}", encryptedCreditNote.getCheqiReceiptId());
        logger.info("🔍 Public key (first 100 chars): {}...", 
                   encryptedCreditNote.getPublicKey().substring(0, Math.min(100, encryptedCreditNote.getPublicKey().length())));
        logger.info("🔍 Encrypted credit note length: {} characters", 
                   encryptedCreditNote.getEncryptedCreditNoteInitiationRequest().length());
        logger.info("🔍 Encrypted symmetric key length: {} characters", 
                   encryptedCreditNote.getEncryptedSymmetricKey().length());
        
        try {
            // Step 1: Decrypt the AES key using RSA private key
            logger.info("🔐 Step 1: Decrypting AES key with RSA-OAEP...");
            SecretKey aesKey = rsaKeyDecryptor.decryptKey(
                    encryptedCreditNote.getEncryptedSymmetricKey(),
                    privateKeyBase64
            );
            logger.info("✅ Step 1 complete: AES key decrypted");
            
            // Step 2: Decrypt the credit note content using the AES key
            logger.info("🔐 Step 2: Decrypting credit note content with AES-GCM...");
            String decryptedCreditNoteContent = aesDecryptor.decrypt(
                    encryptedCreditNote.getEncryptedCreditNoteInitiationRequest(),
                    aesKey
            );
            logger.info("✅ Step 2 complete: Credit note content decrypted ({} characters)", 
                       decryptedCreditNoteContent.length());
            
            logger.info("✅ Successfully decrypted credit note for receipt: {}", 
                    encryptedCreditNote.getCheqiReceiptId());
            logger.info("📊 Decryption summary: content={} chars", decryptedCreditNoteContent.length());
            
            return new DecryptedCreditNote(decryptedCreditNoteContent);
        } catch (Exception e) {
            logger.error("❌ Failed to decrypt credit note for receipt {}: {}", 
                        encryptedCreditNote.getCheqiReceiptId(), e.getMessage(), e);
            throw new DecryptionException("Failed to decrypt credit note", e);
        }
    }

    /**
     * Checks if the encrypted receipt contains customer details.
     */
    private boolean hasCustomerDetails(EncryptedReceipt encryptedReceipt) {
        return encryptedReceipt.getEncryptedCustomerDetails() != null &&
               !encryptedReceipt.getEncryptedCustomerDetails().trim().isEmpty() &&
               encryptedReceipt.getEncryptedCustomerAesKey() != null;
    }
}