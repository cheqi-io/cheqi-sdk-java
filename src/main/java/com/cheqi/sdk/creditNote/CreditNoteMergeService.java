package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.decryption.DecryptedDeliveredCreditNote;
import com.cheqi.sdk.models.generated.CreditNoteContextEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditNoteMergeService {
    private static final Logger logger = LoggerFactory.getLogger(CreditNoteMergeService.class);
    private static final String CUSTOMER_PARTY_PLACEHOLDER = "<cac:AccountingCustomerParty/>";
    private static final String CUSTOMER_PARTY_CLOSING_TAG = "</cac:AccountingCustomerParty>";

    public void merge(DecryptedDeliveredCreditNote decryptedCreditNote) {
        CreditNoteContextEnvelope envelope = decryptedCreditNote.getCreditNoteContextEnvelope();
        if (envelope == null) {
            return;
        }

        CreditNoteEnvelope creditNoteEnvelope = decryptedCreditNote.getCreditNoteEnvelope();
        mergeXmlParty(envelope, creditNoteEnvelope);
        mergeXmlPaymentMeans(envelope, creditNoteEnvelope);
        mergeReceivingParty(envelope, creditNoteEnvelope);
    }

    private void mergeXmlParty(CreditNoteContextEnvelope envelope, CreditNoteEnvelope creditNoteEnvelope) {
        if (envelope.getReceivingParty() == null || envelope.getReceivingParty().getXmlReceivingParty() == null) {
            return;
        }

        String xmlReceivingParty = envelope.getReceivingParty().getXmlReceivingParty();
        creditNoteEnvelope.setUblCreditNote(mergeXmlPayload(creditNoteEnvelope.getUblCreditNote(), xmlReceivingParty));
    }

    private String mergeXmlPayload(String xml, String xmlReceivingParty) {
        if (xml == null) {
            return null;
        }
        if (!xml.contains(CUSTOMER_PARTY_PLACEHOLDER)) {
            return xml;
        }

        logger.debug("Merged xmlReceivingParty into credit note XML");
        return xml.replace(CUSTOMER_PARTY_PLACEHOLDER, xmlReceivingParty);
    }

    private void mergeXmlPaymentMeans(CreditNoteContextEnvelope envelope, CreditNoteEnvelope creditNoteEnvelope) {
        if (envelope.getPaymentMeans() == null || envelope.getPaymentMeans().getXmlPaymentMeans() == null) {
            return;
        }

        creditNoteEnvelope.setUblCreditNote(
                injectPaymentMeans(creditNoteEnvelope.getUblCreditNote(), envelope.getPaymentMeans().getXmlPaymentMeans()));
    }

    private String injectPaymentMeans(String xml, String xmlPaymentMeans) {
        if (xml == null) {
            return null;
        }
        if (xml.contains(CUSTOMER_PARTY_CLOSING_TAG)) {
            return xml.replace(CUSTOMER_PARTY_CLOSING_TAG, CUSTOMER_PARTY_CLOSING_TAG + xmlPaymentMeans);
        }
        if (xml.contains(CUSTOMER_PARTY_PLACEHOLDER)) {
            return xml.replace(CUSTOMER_PARTY_PLACEHOLDER, CUSTOMER_PARTY_PLACEHOLDER + xmlPaymentMeans);
        }
        return xml;
    }

    private void mergeReceivingParty(CreditNoteContextEnvelope envelope, CreditNoteEnvelope creditNoteEnvelope) {
        if (envelope.getReceivingParty() == null
                || envelope.getReceivingParty().getReceivingParty() == null
                || creditNoteEnvelope.getCheqiCreditNote() == null) {
            return;
        }

        creditNoteEnvelope.getCheqiCreditNote().setReceivingParty(envelope.getReceivingParty().getReceivingParty());
    }
}
