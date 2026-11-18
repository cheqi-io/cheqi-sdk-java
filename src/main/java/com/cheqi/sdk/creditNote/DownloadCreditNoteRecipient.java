package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.ReceiptEnvelopeDocument;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

/** Creates a receiving key; only its public half leaves the encrypted invoice envelope. */
public final class DownloadCreditNoteRecipient {
    private DownloadCreditNoteRecipient() {}
    public static String addTo(ReceiptEnvelope envelope) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        envelope.putDocumentsItem("DOWNLOAD_RECIPIENT_KEY", new ReceiptEnvelopeDocument()
                .mediaType(ReceiptEnvelopeDocument.MediaTypeEnum.APPLICATION_JSON)
                .content(ObjectMapperConfig.getInstance().writeValueAsString(Map.of(
                        "algorithm", "RSA-OAEP-256", "privateKey", Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded())))));
        return Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
    }
}
