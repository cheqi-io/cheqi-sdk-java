package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.UBL.Party;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.exceptions.ReceiptProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Processes encrypted receipts by decrypting and parsing them into PurchaseReceipt objects.
 * 
 * This processor handles:
 * - Decryption of encrypted receipt content
 * - Parsing JSON into UBL PurchaseReceipt objects
 * - Injecting customer details when available
 */
public class ReceiptProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptProcessor.class);
    
    private final DecryptionService decryptionService;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new ReceiptProcessor.
     * 
     * @param decryptionService Service for decrypting receipts
     * @throws NullPointerException if decryptionService is null
     */
    public ReceiptProcessor(DecryptionService decryptionService) {
        this.decryptionService = Objects.requireNonNull(decryptionService, "decryptionService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
        logger.info("ReceiptProcessor initialized");
    }

    /**
     * Processes an encrypted receipt by decrypting and parsing it.
     * 
     * @param encryptedReceipt The encrypted receipt DTO
     * @param privateKey The private key for decryption (Base64 PKCS#8 format)
     * @return Fully parsed PurchaseReceipt with customer details if available
     * @throws ReceiptProcessingException if decryption or parsing fails
     */
    public PurchaseReceipt processEncryptedReceipt(
            EncryptedReceiptDto encryptedReceipt,
            String privateKey) {
        
        logger.debug("Processing encrypted receipt for recipient: {}", encryptedReceipt.getRecipientId());

        // Step 1: Decrypt receipt and customer details
        DecryptedReceipt decrypted = decryptionService.decryptReceipt(encryptedReceipt, privateKey);

        // Step 2: Parse receipt JSON into PurchaseReceipt object
        PurchaseReceipt receipt;
        try {
            receipt = parseReceiptFromJson(decrypted.getReceiptContentJson());
            logger.debug("Successfully parsed receipt content");
        } catch (Exception e) {
            logger.error("Failed to parse receipt JSON: {}", e.getMessage());
            throw new ReceiptProcessingException("Failed to parse receipt JSON", e);
        }

        // Step 3: Inject customer party if available
        if (decrypted.hasCustomerDetails()) {
            try {
                Party customerParty = parsePartyFromJson(decrypted.getCustomerDetails());
                receipt.setAccountingCustomerParty(customerParty);
                logger.debug("Successfully injected customer details");
            } catch (Exception e) {
                logger.error("Failed to parse customer details JSON: {}", e.getMessage());
                throw new ReceiptProcessingException("Failed to parse customer details JSON", e);
            }
        }

        logger.info("Successfully processed encrypted receipt for recipient: {}", encryptedReceipt.getRecipientId());
        return receipt;
    }

    /**
     * Parses PurchaseReceipt from JSON string.
     */
    private PurchaseReceipt parseReceiptFromJson(String receiptJson) throws Exception {
        return objectMapper.readValue(receiptJson, PurchaseReceipt.class);
    }

    /**
     * Parses Party from JSON string.
     */
    private Party parsePartyFromJson(String partyJson) throws Exception {
        return objectMapper.readValue(partyJson, Party.class);
    }


}
