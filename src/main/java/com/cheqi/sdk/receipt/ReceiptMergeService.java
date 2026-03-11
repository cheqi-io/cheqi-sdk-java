package com.cheqi.sdk.receipt;

import com.cheqi.sdk.decryption.DecryptedReceipt;
import com.cheqi.sdk.models.ConsumingPartyEnvelope;
import com.cheqi.sdk.models.ReceiptEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merges customer details from the {@link ConsumingPartyEnvelope} into the receipt formats
 * contained in the {@link ReceiptEnvelope}.
 *
 * Two merges are performed:
 * <ul>
 *   <li>UBL XML: the {@code <cac:AccountingCustomerParty/>} placeholder is replaced with the xmlParty fragment</li>
 *   <li>CheqiReceipt: the receivingParty is injected from the ConsumingPartyEnvelope</li>
 * </ul>
 *
 * The {@link DecryptedReceipt} is mutated in place — after calling {@link #merge(DecryptedReceipt)},
 * its {@code getUblXml()} and {@code getCheqiReceipt()} return the complete, merged versions.
 */
public class ReceiptMergeService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptMergeService.class);
    private static final String CUSTOMER_PARTY_PLACEHOLDER = "<cac:AccountingCustomerParty/>";

    /**
     * Merges customer details into both receipt formats of the given {@link DecryptedReceipt}.
     * Does nothing if no customer details are present.
     *
     * @param decryptedReceipt the decrypted receipt to merge customer details into
     */
    public void merge(DecryptedReceipt decryptedReceipt) {
        if (!decryptedReceipt.hasCustomerDetails()) {
            return;
        }

        ConsumingPartyEnvelope envelope = decryptedReceipt.getConsumingPartyEnvelope();
        ReceiptEnvelope receiptEnvelope = decryptedReceipt.getReceiptEnvelope();

        mergeXmlParty(envelope, receiptEnvelope);
        mergeReceivingParty(envelope, receiptEnvelope);
    }

    /**
     * Replaces the {@code <cac:AccountingCustomerParty/>} placeholder in the UBL XML
     * with the xmlParty fragment from the ConsumingPartyEnvelope.
     */
    private void mergeXmlParty(ConsumingPartyEnvelope envelope, ReceiptEnvelope receiptEnvelope) {
        if (envelope.getXmlParty() == null || receiptEnvelope.getUblXml() == null) {
            return;
        }

        String ublXml = receiptEnvelope.getUblXml();
        if (ublXml.contains(CUSTOMER_PARTY_PLACEHOLDER)) {
            String mergedXml = ublXml.replace(CUSTOMER_PARTY_PLACEHOLDER, envelope.getXmlParty());
            receiptEnvelope.setUblXml(mergedXml);
            logger.debug("Merged xmlParty into UBL XML, replacing AccountingCustomerParty placeholder");
        } else {
            logger.warn("UBL XML does not contain AccountingCustomerParty placeholder, skipping XML merge");
        }
    }

    /**
     * Injects the receivingParty from the ConsumingPartyEnvelope into the CheqiReceipt.
     */
    private void mergeReceivingParty(ConsumingPartyEnvelope envelope, ReceiptEnvelope receiptEnvelope) {
        if (envelope.getReceivingParty() == null || receiptEnvelope.getCheqiReceipt() == null) {
            return;
        }

        receiptEnvelope.getCheqiReceipt().setReceivingParty(envelope.getReceivingParty());
        logger.debug("Injected receivingParty into CheqiReceipt");
    }
}
