package com.cheqi.sdk.download;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.config.Environment;
import com.cheqi.sdk.models.generated.CheqiReceipt;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.UUID;

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

        ReceiptEnvelope envelope = new ReceiptEnvelope()
                .cheqiReceiptId("INTEROP")
                .envelopeVersion(1)
                .receiptGeneratorVersion("test")
                .receiptUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        String ciphertext = service.encryptDownloadEnvelope(envelope, key);

        assertEquals("AAECAwQFBgcICQoLPCC1c6CUq0noIvLiwZ0xCaHspX2+LxoudzfHqT8Nb9F0fcuS27Iwog/ZU8/t6V5dgjYQ6Ayz0alW+EQ7ItLZzIJZpR+6oVImeTrPHI77YZpu77sRHww9Ssff6qkLhJuh6Yo8hcEouG7LaVfWak8DT4pXUpvT9RW3fc9n/a/WXj6LfzD2hOfmYzjReJEN6nzyBsTSF0hGbdwYV5ZWFVhKIWkRZmjW", ciphertext);
    }
}
