package com.cheqi.sdk.decryption;

import com.cheqi.sdk.models.generated.*;

/**
 * Represents a decrypted receipt containing multiple format options.
 * The receipt envelope contains different format representations of the same receipt.
 * The customer context envelope contains the recipient-specific context (if present).
 *
 * This is a pure data holder. Use {@link DecryptedReceiptFactory} to create instances
 * and {@link com.cheqi.sdk.receipt.ReceiptMergeService} to merge customer details into the receipt formats.
 */
public class DecryptedReceipt {
    private final ReceiptEnvelope receiptEnvelope;
    private final CustomerContextEnvelope customerContextEnvelope;

    public DecryptedReceipt(ReceiptEnvelope receiptEnvelope, CustomerContextEnvelope customerContextEnvelope) {
        this.receiptEnvelope = receiptEnvelope;
        this.customerContextEnvelope = customerContextEnvelope;
    }

    /**
     * Gets the UBL XML party fragment from the customer context envelope.
     * @return XML party string, or null if no customer details
     */
    public String getXmlParty() {
        return customerContextEnvelope != null ? customerContextEnvelope.getXmlParty() : null;
    }

    /**
     * Gets the receiving party from the customer context envelope.
     * @return ReceivingParty, or null if no customer details
     */
    public ReceivingParty getReceivingParty() {
        return customerContextEnvelope != null ? customerContextEnvelope.getReceivingParty() : null;
    }

    /**
     * Gets payment means from the customer context envelope.
     * @return payment means list, or null if not available
     */
    public PaymentMeansEnvelope getPaymentMeans() {
        return customerContextEnvelope != null ? customerContextEnvelope.getPaymentMeans() : null;
    }

    /**
     * Gets the receipt in Cheqi format.
     * @return CheqiReceipt object, or null if not available
     */
    public CheqiReceipt getCheqiReceipt() {
        return receiptEnvelope.getCheqi();
    }

    /**
     * Gets the receipt content in UBL XML format.
     * @return UBL XML string, or null if not available
     */
    public String getUblPurchaseReceipt() {
        return receiptEnvelope.getUblPurchaseReceipt();
    }

    /**
     * Gets the receipt envelope containing all available formats.
     * @return ReceiptEnvelope with all format options
     */
    public ReceiptEnvelope getReceiptEnvelope() {
        return receiptEnvelope;
    }

    /**
     * Gets the customer context envelope containing customer details.
     * @return CustomerContextEnvelope, or null if no customer details
     */
    public CustomerContextEnvelope getCustomerContextEnvelope() {
        return customerContextEnvelope;
    }

    /**
     * Checks if customer details are present.
     */
    public boolean hasCustomerDetails() {
        return customerContextEnvelope != null;
    }
}
