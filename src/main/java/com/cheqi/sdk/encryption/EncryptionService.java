package com.cheqi.sdk.encryption;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.DTOs.Recipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core encryption service providing hybrid encryption for end-to-end security.
 *
 * This service orchestrates:
 * - AES-256-GCM encryption for receipt data
 * - RSA-OAEP encryption for AES keys
 * - Secure key generation and management
 *
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
     * Encrypts a receipt template for multiple recipients using hybrid encryption.
     * Each recipient gets a unique AES key encrypted with their RSA public key.
     *
     * @param purchaseReceipt The JSON receipt template to encrypt
     * @param recipients List of recipients with their public keys
     * @param supplierPartyId Supplier party identifier
     * @return Set of encrypted receipt DTOs, one per recipient
     * @throws EncryptionException if encryption fails for any recipient
     */
    public Set<EncryptedReceiptDto> encryptReceiptForRecipients(
            String purchaseReceipt,
            List<Recipient> recipients,
            UUID supplierPartyId) {
        
        logger.debug("Encrypting receipt for {} recipients", recipients.size());
        
        try {
            Set<EncryptedReceiptDto> encryptedReceipts = recipients.stream()
                    .map(recipient -> encryptReceiptForRecipient(recipient, purchaseReceipt, supplierPartyId))
                    .collect(Collectors.toSet());
            
            logger.info("Successfully encrypted receipt for {} recipients", encryptedReceipts.size());
            return encryptedReceipts;
            
        } catch (Exception e) {
            logger.error("Failed to encrypt receipt for recipients: {}", e.getMessage());
            throw new EncryptionException("Failed to encrypt receipt for recipients", e);
        }
    }

    /**
     * Encrypts a receipt for a single recipient using hybrid encryption.
     */
    private EncryptedReceiptDto encryptReceiptForRecipient(
            Recipient recipient,
            String purchaseReceipt,
            UUID supplierPartyId) {
        
        try {
            logger.debug("Encrypting receipt for recipient: {}", recipient.getId());
            
            // Step 1: Generate unique AES-256 key for this receipt
            SecretKey aesKey = keyGenerator.generateKey();
            
            // Step 2: Encrypt receipt data with AES-GCM
            EncryptedData encryptedData = aesEncryptor.encrypt(purchaseReceipt, aesKey);
            String encryptedReceiptBase64 = encryptedData.toBase64String();
            
            // Step 3: Encrypt AES key with recipient's RSA public key
            String encryptedSymmetricKey = rsaKeyEncryptor.encryptKey(aesKey, recipient.getPublicKey());
            
            // Step 4: Build encrypted receipt DTO with pre-encrypted customer details
            return EncryptedReceiptDto.builder()
                    .recipientId(recipient.getId())
                    .receiverType(recipient.getReceiverType())
                    .encryptedReceipt(encryptedReceiptBase64)
                    .encryptedSymmetricKey(encryptedSymmetricKey)
                    .encryptedCustomerAesKey(recipient.getEncryptedAesKey())
                    .encryptedCustomerDetails(recipient.getEncryptedCustomerDetails())
                    .supplierPartyId(supplierPartyId)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Failed to encrypt receipt for recipient {}: {}", recipient.getId(), e.getMessage());
            throw new EncryptionException("Failed to encrypt receipt for recipient: " + recipient.getId(), e);
        }
    }


}