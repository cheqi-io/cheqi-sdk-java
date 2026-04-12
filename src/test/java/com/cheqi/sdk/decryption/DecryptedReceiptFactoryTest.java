package com.cheqi.sdk.decryption;

import com.cheqi.sdk.models.generated.BuyerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecryptedReceiptFactoryTest {

    @Test
    void create_acceptsLegacyVatMetaDataFieldName() {
        DecryptedReceiptFactory factory = new DecryptedReceiptFactory();
        String receiptEnvelopeJson = "{"
                + "\"ublPurchaseReceipt\":\"<Receipt/>\","
                + "\"vatMetaData\":{"
                + "\"buyerType\":\"BUSINESS\","
                + "\"taxesApplied\":true"
                + "}"
                + "}";

        DecryptedReceipt decryptedReceipt = factory.create(receiptEnvelopeJson, null);

        assertEquals("<Receipt/>", decryptedReceipt.getReceiptEnvelope().getUblPurchaseReceipt());
        assertNotNull(decryptedReceipt.getReceiptEnvelope().getVatMetaData());
        assertEquals(BuyerType.BUSINESS, decryptedReceipt.getReceiptEnvelope().getVatMetaData().getBuyerType());
        assertTrue(decryptedReceipt.getReceiptEnvelope().getVatMetaData().getTaxesApplied());
    }

    @Test
    void create_ignoresUnknownFieldsInCustomerEnvelope() {
        DecryptedReceiptFactory factory = new DecryptedReceiptFactory();
        String receiptEnvelopeJson = "{\"ublPurchaseReceipt\":\"<Receipt/>\"}";
        String customerEnvelopeJson = "{"
                + "\"receivingParty\":{"
                + "\"xmlReceivingParty\":\"<cac:AccountingCustomerParty/>\","
                + "\"unknownField\":\"ignored\""
                + "},"
                + "\"paymentMeans\":{"
                + "\"paymentMeansCode\":\"48\","
                + "\"description\":\"Card payment\","
                + "\"cardProvider\":\"MASTERCARD\","
                + "\"cardLastFour\":\"7257\","
                + "\"xmlPaymentMeans\":\"<cac:PaymentMeans/>\""
                + "}"
                + "}";

        DecryptedReceipt decryptedReceipt = factory.create(receiptEnvelopeJson, customerEnvelopeJson);

        assertNotNull(decryptedReceipt.getReceiptContextEnvelope());
        assertEquals("<cac:AccountingCustomerParty/>", decryptedReceipt.getReceiptContextEnvelope().getReceivingParty().getXmlReceivingParty());
        assertNull(decryptedReceipt.getReceiptContextEnvelope().getReceivingParty().getReceivingParty());
        assertNotNull(decryptedReceipt.getReceiptContextEnvelope().getPaymentMeans());
        assertEquals("48", decryptedReceipt.getReceiptContextEnvelope().getPaymentMeans().getPaymentMeansCode());
        assertEquals("MASTERCARD", decryptedReceipt.getReceiptContextEnvelope().getPaymentMeans().getCardProvider());
        assertEquals("7257", decryptedReceipt.getReceiptContextEnvelope().getPaymentMeans().getCardLastFour());
    }
}
