package com.cheqi.sdk.download;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.CheqiReceipt;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import com.cheqi.sdk.models.generated.ReceiptTemplateResponse;
import com.cheqi.sdk.models.generated.VatMetadata;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class DownloadServiceTest {

    @Test
    void generatesAndParsesContractV1Link() {
        DownloadService service = new DownloadService();
        DownloadLink link = service.generateDownloadLink("https://receipt.cheqi.io/");

        assertTrue(link.getDownloadId().matches("[A-Za-z0-9_-]{22}"));
        assertTrue(link.getContentKey().matches("[A-Za-z0-9_-]{43}"));
        assertEquals("https://receipt.cheqi.io/" + link.getDownloadId() + "#" + link.getContentKey(), link.getUrl());

        DownloadLink parsed = service.parseDownloadUrl(link.getUrl());
        assertEquals(link.getDownloadId(), parsed.getDownloadId());
        assertEquals(link.getContentKey(), parsed.getContentKey());
        assertFalse(link.toString().contains(link.getContentKey()));
    }

    @Test
    void buildsCanonicalEnvelopeAndRoundTripsEncryption() {
        DownloadService service = new DownloadService();
        DownloadLink link = service.generateDownloadLink("https://receipt.cheqi.io");
        VatMetadata vat = new VatMetadata().taxesApplied(true);
        ReceiptTemplateResponse template = new ReceiptTemplateResponse()
                .cheqi(new CheqiReceipt().documentNumber("JAVA-1"))
                .ublPurchaseReceipt("<PurchaseReceipt/>")
                .ublInvoice("<Invoice/>")
                .vatMetadata(vat);

        ReceiptEnvelope envelope = service.buildDownloadEnvelope(template);
        String ciphertext = service.encryptDownloadEnvelope(envelope, link.getContentKey());
        ReceiptEnvelope decrypted = service.decryptDownloadEnvelope(ciphertext, link.getContentKey());

        assertEquals("JAVA-1", decrypted.getCheqi().getDocumentNumber());
        assertEquals("<PurchaseReceipt/>", decrypted.getUblPurchaseReceipt());
        assertEquals("<Invoice/>", decrypted.getUblInvoice());
        assertTrue(decrypted.getVatMetaData().getTaxesApplied());
    }

    @Test
    void encryptionUsesContractFraming() {
        SecureRandom fixedRandom = new SecureRandom() {
            @Override
            public void nextBytes(byte[] bytes) {
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) i;
                }
            }
        };
        DownloadService service = new DownloadService(fixedRandom, ObjectMapperConfig.getInstance());
        String key = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8";

        String ciphertext = service.encryptDownloadEnvelope(
                new ReceiptEnvelope().cheqi(new CheqiReceipt().documentNumber("INTEROP")), key);

        assertEquals("AAECAwQFBgcICQoLPCC1c6CUqzm3OrXv3ooNAOa483qFFj0ZSkXfp1QnVPdTX/7e0rxBB1BCAdhTN4LdhEsU+1LX", ciphertext);
    }
}
