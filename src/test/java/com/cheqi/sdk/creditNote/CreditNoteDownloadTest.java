package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.download.DownloadService;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.receipt.PreparedReceiptDownload;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditNoteDownloadTest {
    @Test void ordinaryDownloadKeepsReferenceInsideCiphertextAndNeedsNoOriginalId() throws Exception {
        var api = mock(CheqiApiClient.class);
        var service = new CreditNoteService(api, new EncryptionService(), new MatchingService(api));
        var payload = Map.of("documentNumber", "CN-123", "originatorDocumentReference", " INV-123 ",
                "creditNoteSubtotal", "12.123456789012345678");
        var prepared = service.prepareDownload(Map.of("taxesApplied", false, "creditNoteTemplateRequest", payload));
        verifyNoInteractions(api);
        var mapper = ObjectMapperConfig.getInstance();
        var restored = mapper.readValue(mapper.writeValueAsString(prepared), PreparedReceiptDownload.class);
        var downloads = new DownloadService();
        var link = downloads.parseDownloadUrl(restored.getDownloadUrl());
        var envelope = downloads.decryptDownloadEnvelope(restored.getCiphertext(), link.getContentKey());
        var body = mapper.readTree(envelope.getDocuments().get("CREDIT_NOTE_PAYLOAD").getContent());
        assertEquals(" INV-123 ", body.path("originatorDocumentReference").asText());
        assertEquals("12.123456789012345678", body.path("creditNoteSubtotal").asText());
        assertFalse(body.path("taxesApplied").asBoolean());
        assertEquals(prepared.getCiphertext(), restored.getCiphertext());
        String wire = mapper.writeValueAsString(restored.getUploadRequest());
        assertFalse(wire.contains("INV-123")); assertFalse(wire.contains(link.getContentKey()));
        assertFalse(wire.contains("parent")); assertFalse(wire.contains("documentReferenceHash"));
        assertThrows(Exception.class, () -> service.prepareDownload(Map.of("documentNumber", "CN", "taxesApplied", true)));
    }
}
