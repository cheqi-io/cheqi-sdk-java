package com.cheqi.sdk.download;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.generated.CheqiReceipt;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class DownloadServiceTest {

    @Test
    void generatesAndParsesContractV1Link() {
        DownloadService service = new DownloadService();
        DownloadLink link = service.generateDownloadLink(Environment.PRODUCTION);

        assertTrue(link.getDownloadId().matches("[A-Za-z0-9_-]{22}"));
        assertTrue(link.getContentKey().matches("[A-Za-z0-9_-]{43}"));
        assertEquals("https://receipt.cheqi.io/" + link.getDownloadId() + "#" + link.getContentKey(), link.getUrl());

        DownloadLink parsed = service.parseDownloadUrl(link.getUrl());
        assertEquals(link.getDownloadId(), parsed.getDownloadId());
        assertEquals(link.getContentKey(), parsed.getContentKey());
        assertFalse(link.toString().contains(link.getContentKey()));
    }

    @Test
    void apiEnvironmentsMapToCustomerFacingReceiptOrigins() {
        DownloadService service = new DownloadService();

        assertEquals("https://receipt.cheqi.io", DownloadService.PRODUCTION_BASE_URL);
        assertEquals("https://sandbox.receipt.cheqi.io", DownloadService.SANDBOX_BASE_URL);
        assertTrue(service.generateDownloadLink(Environment.PRODUCTION).getUrl()
                .startsWith("https://receipt.cheqi.io/"));
        assertTrue(service.generateDownloadLink(Environment.SANDBOX).getUrl()
                .startsWith("https://sandbox.receipt.cheqi.io/"));
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
