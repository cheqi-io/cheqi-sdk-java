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

        assertEquals("<Receipt/>", decryptedReceipt.getUblPurchaseReceipt());
        assertNotNull(decryptedReceipt.getReceiptEnvelope().getVatMetaData());
        assertEquals(BuyerType.BUSINESS, decryptedReceipt.getReceiptEnvelope().getVatMetaData().getBuyerType());
        assertTrue(decryptedReceipt.getReceiptEnvelope().getVatMetaData().getTaxesApplied());
    }

    @Test
    void create_ignoresUnknownFieldsInCustomerEnvelope() {
        DecryptedReceiptFactory factory = new DecryptedReceiptFactory();
        String receiptEnvelopeJson = "{\"ublPurchaseReceipt\":\"<Receipt/>\"}";
        String customerEnvelopeJson = "{"
                + "\"xmlParty\":\"<cac:AccountingCustomerParty/>\","
                + "\"paymentMeans\":{"
                + "\"paymentMeansCode\":\"48\","
                + "\"description\":\"Card payment\","
                + "\"cardProvider\":\"MASTERCARD\","
                + "\"cardLastFour\":\"7257\","
                + "\"xmlPaymentMeans\":\"<cac:PaymentMeans/>\""
                + "}"
                + "}";

        DecryptedReceipt decryptedReceipt = factory.create(receiptEnvelopeJson, customerEnvelopeJson);

        assertNotNull(decryptedReceipt.getCustomerContextEnvelope());
        assertEquals("<cac:AccountingCustomerParty/>", decryptedReceipt.getXmlParty());
        assertNull(decryptedReceipt.getReceivingParty());
        assertNotNull(decryptedReceipt.getPaymentMeans());
        assertEquals("48", decryptedReceipt.getPaymentMeans().getPaymentMeansCode());
        assertEquals("MASTERCARD", decryptedReceipt.getPaymentMeans().getCardProvider());
        assertEquals("7257", decryptedReceipt.getPaymentMeans().getCardLastFour());
    }
}
