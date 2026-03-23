package com.cheqi.sdk.receipt;

import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.decryption.DecryptionService;
import com.cheqi.sdk.models.EncryptedReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Processes encrypted receipts by decrypting and parsing them.
 * 
 * This processor handles:
 * - Decryption of encrypted receipt content and customer details
 * - Merging customer details into both receipt formats (UBL XML and CheqiReceipt)
 * 
 * The returned {@link DecryptedReceipt} provides access to:
 * - {@code getUblXml()} — complete UBL XML with AccountingCustomerParty merged in
 * - {@code getCheqiReceipt()} — CheqiReceipt with receivingParty injected
 */
public class ReceiptProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptProcessor.class);
    
    private final DecryptionService decryptionService;
    private final ReceiptMergeService mergeService;

    /**
     * Creates a new ReceiptProcessor.
     * 
     * @param decryptionService Service for decrypting receipts
     * @throws NullPointerException if decryptionService is null
     */
    public ReceiptProcessor(DecryptionService decryptionService) {
        this.decryptionService = Objects.requireNonNull(decryptionService, "decryptionService cannot be null");
        this.mergeService = new ReceiptMergeService();
        logger.info("ReceiptProcessor initialized");
    }

    /**
     * Processes an encrypted receipt by decrypting it and merging customer details.
     * 
     * Customer details from the {@link com.cheqi.sdk.models.ConsumingPartyEnvelope} are automatically merged:
     * <ul>
     *   <li>UBL XML: the {@code <cac:AccountingCustomerParty/>} placeholder is replaced with the xmlParty fragment</li>
     *   <li>CheqiReceipt: the receivingParty is injected from the ConsumingPartyEnvelope</li>
     * </ul>
     * 
     * @param encryptedReceipt The encrypted receipt DTO
     * @param privateKey The private key for decryption (Base64 PKCS#8 format)
     * @return DecryptedReceipt with merged customer details in both formats
     * @throws RuntimeException if decryption or parsing fails
     */
    public DecryptedReceipt processEncryptedReceipt(
            EncryptedReceipt encryptedReceipt,
            String privateKey) {

        DecryptedReceipt decrypted = decryptionService.decryptReceipt(encryptedReceipt, privateKey);
        mergeService.merge(decrypted);

        return decrypted;
    }
}
