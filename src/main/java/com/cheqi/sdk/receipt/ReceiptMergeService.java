package com.cheqi.sdk.receipt;

import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.models.generated.ReceiptContextEnvelope;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merges receipt context from the {@link ReceiptContextEnvelope} into the receipt formats
 * contained in the {@link ReceiptEnvelope}.
 *
 * Two merges are performed:
 * <ul>
 *   <li>UBL XML: the {@code <cac:AccountingCustomerParty/>} placeholder is replaced with the xmlReceivingParty fragment</li>
 *   <li>UBL XML: xmlPaymentMeans is injected immediately after {@code AccountingCustomerParty} when present</li>
 *   <li>CheqiReceipt: the receivingParty is injected from the receipt context envelope</li>
 * </ul>
 *
 * The {@link DecryptedReceipt} is mutated in place — after calling {@link #merge(DecryptedReceipt)},
 * its receipt envelope contains the complete, merged versions.
 */
public class ReceiptMergeService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptMergeService.class);
    private static final String CUSTOMER_PARTY_PLACEHOLDER = "<cac:AccountingCustomerParty/>";
    private static final String CUSTOMER_PARTY_CLOSING_TAG = "</cac:AccountingCustomerParty>";

    /**
     * Merges receipt context into both receipt formats of the given {@link DecryptedReceipt}.
     * Does nothing if no receipt context is present.
     *
     * @param decryptedReceipt the decrypted receipt to merge receipt context into
     */
    public void merge(DecryptedReceipt decryptedReceipt) {
        ReceiptContextEnvelope envelope = decryptedReceipt.getReceiptContextEnvelope();
        if (envelope == null) {
            return;
        }

        ReceiptEnvelope receiptEnvelope = decryptedReceipt.getReceiptEnvelope();

        mergeXmlParty(envelope, receiptEnvelope);
        mergeXmlPaymentMeans(envelope, receiptEnvelope);
        mergeReceivingParty(envelope, receiptEnvelope);
    }

    /**
     * Replaces the {@code <cac:AccountingCustomerParty/>} placeholder in the UBL XML
     * with the xmlReceivingParty fragment from the receipt context envelope.
     */
    private void mergeXmlParty(ReceiptContextEnvelope envelope, ReceiptEnvelope receiptEnvelope) {
        if (envelope.getReceivingParty() == null || envelope.getReceivingParty().getXmlReceivingParty() == null) {
            return;
        }

        String xmlReceivingParty = envelope.getReceivingParty().getXmlReceivingParty();
        receiptEnvelope.setUblPurchaseReceipt(
                mergeXmlPayload(receiptEnvelope.getUblPurchaseReceipt(), xmlReceivingParty, "ublPurchaseReceipt"));
        receiptEnvelope.setUblInvoice(
                mergeXmlPayload(receiptEnvelope.getUblInvoice(), xmlReceivingParty, "ublInvoice"));
    }

    private String mergeXmlPayload(String xml, String xmlReceivingParty, String fieldName) {
        if (xml == null) {
            return null;
        }
        if (!xml.contains(CUSTOMER_PARTY_PLACEHOLDER)) {
            logger.warn("{} does not contain AccountingCustomerParty placeholder, skipping XML merge", fieldName);
            return xml;
        }

        logger.debug("Merged xmlReceivingParty into {}, replacing AccountingCustomerParty placeholder", fieldName);
        return xml.replace(CUSTOMER_PARTY_PLACEHOLDER, xmlReceivingParty);
    }

    /**
     * Injects the xmlPaymentMeans fragment from the receipt context envelope immediately after
     * the AccountingCustomerParty element in the UBL XML.
     */
    private void mergeXmlPaymentMeans(ReceiptContextEnvelope envelope, ReceiptEnvelope receiptEnvelope) {
        if (envelope.getPaymentMeans() == null || envelope.getPaymentMeans().getXmlPaymentMeans() == null) {
            return;
        }

        String xmlPaymentMeans = envelope.getPaymentMeans().getXmlPaymentMeans();
        receiptEnvelope.setUblPurchaseReceipt(
                injectPaymentMeans(receiptEnvelope.getUblPurchaseReceipt(), xmlPaymentMeans, "ublPurchaseReceipt"));
        receiptEnvelope.setUblInvoice(
                injectPaymentMeans(receiptEnvelope.getUblInvoice(), xmlPaymentMeans, "ublInvoice"));
    }

    private String injectPaymentMeans(String xml, String xmlPaymentMeans, String fieldName) {
        if (xml == null) {
            return null;
        }

        if (xml.contains(CUSTOMER_PARTY_CLOSING_TAG)) {
            logger.debug("Injected xmlPaymentMeans into {} after AccountingCustomerParty", fieldName);
            return xml.replace(CUSTOMER_PARTY_CLOSING_TAG, CUSTOMER_PARTY_CLOSING_TAG + xmlPaymentMeans);
        }

        if (xml.contains(CUSTOMER_PARTY_PLACEHOLDER)) {
            logger.debug("Injected xmlPaymentMeans into {} after AccountingCustomerParty placeholder", fieldName);
            return xml.replace(CUSTOMER_PARTY_PLACEHOLDER, CUSTOMER_PARTY_PLACEHOLDER + xmlPaymentMeans);
        }

        logger.warn("{} does not contain AccountingCustomerParty element, skipping PaymentMeans merge", fieldName);
        return xml;
    }

    /**
     * Injects the receivingParty from the receipt context envelope into the CheqiReceipt.
     */
    private void mergeReceivingParty(ReceiptContextEnvelope envelope, ReceiptEnvelope receiptEnvelope) {
        if (envelope.getReceivingParty() == null
                || envelope.getReceivingParty().getReceivingParty() == null
                || receiptEnvelope.getCheqi() == null) {
            return;
        }

        receiptEnvelope.getCheqi().setReceivingParty(envelope.getReceivingParty().getReceivingParty());
        logger.debug("Injected receivingParty into CheqiReceipt");
    }
}
