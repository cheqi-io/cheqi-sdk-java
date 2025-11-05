package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.EncryptedReceiptDto;
import com.cheqi.commons.UBL.Party;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.exceptions.ReceiptProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReceiptProcessor {
    private final DecryptionService decryptionService;
    private final ObjectMapper objectMapper;

    public ReceiptProcessor(DecryptionService decryptionService) {
        this.decryptionService = decryptionService;
        this.objectMapper = new ObjectMapper();
    }

    public PurchaseReceipt processEncryptedReceipt(
            EncryptedReceiptDto encryptedReceipt,
            String privateKey) {

        // Decrypt everything
        DecryptedReceipt decrypted = decryptionService.decryptReceipt(encryptedReceipt, privateKey);

        // Parse receipt
        PurchaseReceipt receipt;
        try {
            receipt = parseReceiptFromJson(decrypted.getReceiptContentJson());
        } catch (Exception e) {
            throw new ReceiptProcessingException("Failed to parse receipt JSON", e);  // More descriptive
        }

        // Inject customer party if available
        if (decrypted.hasCustomerDetails()) {
            try {
                Party customerParty = parsePartyFromJson(decrypted.getCustomerDetails());
                receipt.setAccountingCustomerParty(customerParty);
            } catch (Exception e) {
                throw new ReceiptProcessingException("Failed to parse customer details JSON", e);  // More descriptive
            }
        }

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
