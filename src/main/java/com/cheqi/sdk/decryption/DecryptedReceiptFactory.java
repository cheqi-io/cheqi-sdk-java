package com.cheqi.sdk.decryption;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.CustomerContextEnvelope;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating {@link DecryptedReceipt} instances from raw decrypted JSON strings.
 * Handles deserialization of the receipt envelope and optional customer envelope.
 */
public class DecryptedReceiptFactory {
    private static final Logger logger = LoggerFactory.getLogger(DecryptedReceiptFactory.class);
    private final ObjectMapper objectMapper = ObjectMapperConfig.getInstance();

    /**
     * Creates a {@link DecryptedReceipt} from raw decrypted JSON strings.
     *
     * @param receiptEnvelopeJson the decrypted receipt envelope JSON
     * @param customerEnvelopeJson the decrypted customer envelope JSON, or null if not present
     * @return a fully constructed DecryptedReceipt
     * @throws DecryptionException if JSON deserialization fails
     */
    public DecryptedReceipt create(String receiptEnvelopeJson, String customerEnvelopeJson) {
        try {
            ReceiptEnvelope receiptEnvelope = objectMapper.readValue(receiptEnvelopeJson, ReceiptEnvelope.class);
            logger.debug("Deserialized receipt envelope");

            CustomerContextEnvelope customerContextEnvelope = null;
            if (customerEnvelopeJson != null && !customerEnvelopeJson.trim().isEmpty()) {
                customerContextEnvelope = objectMapper.readValue(customerEnvelopeJson, CustomerContextEnvelope.class);
                logger.debug("Deserialized customer context envelope");
            }

            return new DecryptedReceipt(receiptEnvelope, customerContextEnvelope);
        } catch (Exception e) {
            throw new DecryptionException("Failed to deserialize decrypted receipt envelopes", e);
        }
    }
}
