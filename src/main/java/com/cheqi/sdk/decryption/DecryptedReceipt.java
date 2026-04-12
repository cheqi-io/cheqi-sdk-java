package com.cheqi.sdk.decryption;

import com.cheqi.sdk.models.generated.*;

/**
 * Represents a decrypted receipt containing multiple format options.
 * The receipt envelope contains different format representations of the same receipt.
 * The receipt context envelope contains the recipient-specific context (if present).
 *
 * This is a pure data holder. Use {@link DecryptedReceiptFactory} to create instances
 * and {@link com.cheqi.sdk.receipt.ReceiptMergeService} to merge receipt context into the receipt formats.
 */
public class DecryptedReceipt {
    private ReceiptEnvelope receiptEnvelope;
    private ReceiptContextEnvelope receiptContextEnvelope;

    public DecryptedReceipt(ReceiptEnvelope receiptEnvelope, ReceiptContextEnvelope receiptContextEnvelope) {
        this.receiptEnvelope = receiptEnvelope;
        this.receiptContextEnvelope = receiptContextEnvelope;
    }

    public ReceiptEnvelope getReceiptEnvelope() {
        return receiptEnvelope;
    }

    public void setReceiptEnvelope(ReceiptEnvelope receiptEnvelope) {
        this.receiptEnvelope = receiptEnvelope;
    }

    public ReceiptContextEnvelope getReceiptContextEnvelope() {
        return receiptContextEnvelope;
    }

    public void setReceiptContextEnvelope(ReceiptContextEnvelope receiptContextEnvelope) {
        this.receiptContextEnvelope = receiptContextEnvelope;
    }
}
