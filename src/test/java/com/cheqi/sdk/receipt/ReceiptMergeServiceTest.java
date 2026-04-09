package com.cheqi.sdk.receipt;

import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.models.generated.CustomerContextEnvelope;
import com.cheqi.sdk.models.generated.PaymentMeansEnvelope;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiptMergeServiceTest {

    private static final String PLACEHOLDER = "<cac:AccountingCustomerParty/>";
    private static final String XML_PARTY = "<cac:AccountingCustomerParty><cac:Party/></cac:AccountingCustomerParty>";
    private static final String XML_PAYMENT_MEANS = "<cac:PaymentMeans><cbc:PaymentMeansCode>48</cbc:PaymentMeansCode></cac:PaymentMeans>";

    @Test
    void merge_replacesCustomerPartyInBothUblPayloads() {
        ReceiptEnvelope receiptEnvelope = new ReceiptEnvelope();
        receiptEnvelope.setUblPurchaseReceipt("<PurchaseReceipt>" + PLACEHOLDER + "</PurchaseReceipt>");
        receiptEnvelope.setUblInvoice("<Invoice>" + PLACEHOLDER + "</Invoice>");

        CustomerContextEnvelope customerContextEnvelope = new CustomerContextEnvelope();
        customerContextEnvelope.setXmlParty(XML_PARTY);

        DecryptedReceipt decryptedReceipt = new DecryptedReceipt(receiptEnvelope, customerContextEnvelope);

        new ReceiptMergeService().merge(decryptedReceipt);

        assertEquals("<PurchaseReceipt>" + XML_PARTY + "</PurchaseReceipt>", decryptedReceipt.getReceiptEnvelope().getUblPurchaseReceipt());
        assertEquals("<Invoice>" + XML_PARTY + "</Invoice>", decryptedReceipt.getReceiptEnvelope().getUblInvoice());
    }

    @Test
    void merge_leavesUblInvoiceUntouchedWhenPlaceholderMissing() {
        ReceiptEnvelope receiptEnvelope = new ReceiptEnvelope();
        receiptEnvelope.setUblInvoice("<Invoice><cbc:ID>123</cbc:ID></Invoice>");

        CustomerContextEnvelope customerContextEnvelope = new CustomerContextEnvelope();
        customerContextEnvelope.setXmlParty(XML_PARTY);

        DecryptedReceipt decryptedReceipt = new DecryptedReceipt(receiptEnvelope, customerContextEnvelope);

        new ReceiptMergeService().merge(decryptedReceipt);

        assertEquals("<Invoice><cbc:ID>123</cbc:ID></Invoice>", decryptedReceipt.getReceiptEnvelope().getUblInvoice());
    }

    @Test
    void merge_injectsPaymentMeansAfterAccountingCustomerPartyInBothUblPayloads() {
        ReceiptEnvelope receiptEnvelope = new ReceiptEnvelope();
        receiptEnvelope.setUblPurchaseReceipt("<PurchaseReceipt>" + PLACEHOLDER + "</PurchaseReceipt>");
        receiptEnvelope.setUblInvoice("<Invoice>" + PLACEHOLDER + "</Invoice>");

        CustomerContextEnvelope customerContextEnvelope = new CustomerContextEnvelope();
        customerContextEnvelope.setXmlParty(XML_PARTY);
        customerContextEnvelope.setPaymentMeans(new PaymentMeansEnvelope().xmlPaymentMeans(XML_PAYMENT_MEANS));

        DecryptedReceipt decryptedReceipt = new DecryptedReceipt(receiptEnvelope, customerContextEnvelope);

        new ReceiptMergeService().merge(decryptedReceipt);

        assertEquals(
                "<PurchaseReceipt>" + XML_PARTY + XML_PAYMENT_MEANS + "</PurchaseReceipt>",
                decryptedReceipt.getReceiptEnvelope().getUblPurchaseReceipt()
        );
        assertEquals(
                "<Invoice>" + XML_PARTY + XML_PAYMENT_MEANS + "</Invoice>",
                decryptedReceipt.getReceiptEnvelope().getUblInvoice()
        );
    }
}
