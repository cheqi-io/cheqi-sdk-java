package com.cheqi.sdk.decryption;

import com.cheqi.sdk.models.ConsumingPartyEnvelope;
import com.cheqi.sdk.models.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.CheqiReceipt;
import com.cheqi.sdk.models.generated.ReceivingParty;

/**
 * Represents a decrypted receipt containing multiple format options.
 * The receipt envelope contains different format representations of the same receipt.
 * The consuming party envelope contains the customer details (if present).
 *
 * This is a pure data holder. Use {@link DecryptedReceiptFactory} to create instances
 * and {@link com.cheqi.sdk.receipt.ReceiptMergeService} to merge customer details into the receipt formats.
 */
public class DecryptedReceipt {
    private final ReceiptEnvelope receiptEnvelope;
    private final ConsumingPartyEnvelope consumingPartyEnvelope;

    public DecryptedReceipt(ReceiptEnvelope receiptEnvelope, ConsumingPartyEnvelope consumingPartyEnvelope) {
        this.receiptEnvelope = receiptEnvelope;
        this.consumingPartyEnvelope = consumingPartyEnvelope;
    }

    /**
     * Gets the UBL XML party fragment for the consuming party.
     * @return XML party string, or null if no customer details
     */
    public String getXmlParty() {
        return consumingPartyEnvelope != null ? consumingPartyEnvelope.getXmlParty() : null;
    }

    /**
     * Gets the receiving party from the consuming party envelope.
     * @return ReceivingParty, or null if no customer details
     */
    public ReceivingParty getReceivingParty() {
        return consumingPartyEnvelope != null ? consumingPartyEnvelope.getReceivingParty() : null;
    }

    /**
     * Gets the receipt in Cheqi format.
     * @return CheqiReceipt object, or null if not available
     */
    public CheqiReceipt getCheqiReceipt() {
        return receiptEnvelope.getCheqiReceipt();
    }

    /**
     * Gets the receipt content in UBL XML format.
     * @return UBL XML string, or null if not available
     */
    public String getUblXml() {
        return receiptEnvelope.getUblXml();
    }

    /**
     * Gets the receipt envelope containing all available formats.
     * @return ReceiptEnvelope with all format options
     */
    public ReceiptEnvelope getReceiptEnvelope() {
        return receiptEnvelope;
    }

    /**
     * Gets the consuming party envelope containing customer details.
     * @return ConsumingPartyEnvelope, or null if no customer details
     */
    public ConsumingPartyEnvelope getConsumingPartyEnvelope() {
        return consumingPartyEnvelope;
    }

    /**
     * Checks if customer details are present.
     */
    public boolean hasCustomerDetails() {
        return consumingPartyEnvelope != null;
    }
}
