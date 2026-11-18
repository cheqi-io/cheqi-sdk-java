package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.*;
import com.cheqi.sdk.receipt.DocumentReferences;
import com.cheqi.sdk.receipt.PreparedReceiptDownload;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditNoteReferenceTest {
    final CheqiApiClient api = mock(CheqiApiClient.class);
    final MatchingService matching = new MatchingService(api);
    final CreditNoteService service = new CreditNoteService(api, new EncryptionService(), matching);

    @Test void exactDocumentNumbersHaveStableDomainSeparatedHashes() throws Exception {
        String expected = "7d4b8ece79c805d20d5451ad72f019580fe0dc893f7aa16eedd94992dbaebd32";
        assertEquals(expected, DocumentReferences.hash("INV-123"));
        assertNotEquals(expected, DocumentReferences.hash("inv-123"));
        assertNotEquals(expected, DocumentReferences.hash(" INV-123"));
        assertThrows(IllegalArgumentException.class, () -> DocumentReferences.hash(" "));
    }
    @Test void preparesFromReferenceAndReplaysTheSameEncryptedDownload() throws Exception {
        var envelope = new ReceiptEnvelope().envelopeVersion(2);
        String publicKey = DownloadCreditNoteRecipient.addTo(envelope);
        when(api.resolveCreditNoteReference(any(), any())).thenReturn(new CreditNoteReferenceResolution()
                .deliveryRoute("DOWNLOAD").parentCheqiReceiptId("original").creditNotePublicKey(publicKey));
        var prepared = service.prepareCreditNote(Map.of("originatorDocumentReference", "INV-123", "documentNumber", "CN-123"));
        var mapper = ObjectMapperConfig.getInstance();
        String saved = mapper.writeValueAsString(prepared);
        assertFalse(saved.contains("INV-123")); assertFalse(saved.contains("CN-123"));
        assertFalse(saved.contains("privateKey")); assertFalse(saved.contains("contentKey"));
        var restored = mapper.readValue(saved, PreparedCreditNote.class);
        when(api.uploadEncryptedDownloadReceipt(any(), any())).thenReturn(new ClientReceiptDownloadResponse().cheqiReceiptId("credit"));
        assertTrue(service.submitPreparedCreditNote(restored).isAccepted());
        service.submitPreparedCreditNote(restored);
        verify(api).resolveCreditNoteReference(argThat(r -> DocumentReferences.hash("INV-123").equals(r.getDocumentReferenceHash())), any());
        verify(api, times(2)).uploadEncryptedDownloadReceipt(eq(prepared.getDownloadSubmission()), any());
        verify(api, never()).matchCustomer(any(), any());
        assertEquals(prepared.getDownloadSubmission().getCiphertext(), restored.getDownloadSubmission().getCiphertext());
    }
    @Test void missingOrUnresolvableReferenceNeverCreatesAFallback() throws Exception {
        assertThrows(Exception.class, () -> service.prepareCreditNote(Map.of("documentNumber", "CN-1")));
        verifyNoInteractions(api);
        when(api.resolveCreditNoteReference(any(), any())).thenThrow(new IllegalStateException("ambiguous"));
        assertThrows(Exception.class, () -> service.prepareCreditNote(Map.of("originatorDocumentReference", "INV-1")));
        verify(api, never()).uploadEncryptedDownloadReceipt(any(), any());
        verify(api, never()).matchCustomer(any(), any());
    }
    @Test void preservesReferenceFailureStatusForMerchantHandling() throws Exception {
        when(api.resolveCreditNoteReference(any(), any())).thenThrow(
                new com.cheqi.sdk.http.exceptions.CheqiApiException("Original document number is ambiguous", 409, "CONFLICT", "correlation"));
        var error = assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.prepareCreditNote(Map.of("originatorDocumentReference", "INV-1")));
        assertEquals(409, error.getHttpStatusCode());
        assertTrue(error.getMessage().contains("ambiguous"));
        verify(api, never()).uploadEncryptedDownloadReceipt(any(), any());
    }
    @Test void digitalReferenceUsesOriginalStoreAndOwnerMatching() throws Exception {
        var store = java.util.UUID.randomUUID();
        var receiver = new ReceiptEnvelope().envelopeVersion(2);
        String publicKey = DownloadCreditNoteRecipient.addTo(receiver);
        when(api.resolveCreditNoteReference(any(), any())).thenReturn(new CreditNoteReferenceResolution()
                .deliveryRoute("DIGITAL").parentCheqiReceiptId("original").storeId(store));
        when(api.matchCustomer(any(), any())).thenReturn(new RecipientResolutionResponse().routeFound(true).matchId("credit-match")
                .deliveryRouteType(RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL)
                .recipients(java.util.List.of(new MatchedRecipient().id("device").publicKey(publicKey))));
        var prepared = service.prepareCreditNote(Map.of("originatorDocumentReference", "INV-1", "documentNumber", "CN-1", "taxesApplied", true));
        var mapper = ObjectMapperConfig.getInstance();
        String privateKey = mapper.readTree(receiver.getDocuments().get("DOWNLOAD_RECIPIENT_KEY").getContent()).path("privateKey").asText();
        var delivery = prepared.getDeviceSubmission().getDeviceDeliveries().get(0);
        var key = new com.cheqi.sdk.decryption.RSAKeyDecryptor().decryptKey(delivery.getEncryptedAesKey(), privateKey);
        var plaintext = mapper.readTree(new com.cheqi.sdk.decryption.AESDecryptor().decrypt(delivery.getEncryptedContent(), key));
        assertTrue(plaintext.path("taxesApplied").asBoolean());
        assertEquals("INV-1", plaintext.path("creditNoteTemplateRequest").path("originatorDocumentReference").asText());
        assertEquals(store, prepared.getDeviceSubmission().getStoreId());
        assertEquals("original", prepared.getDeviceSubmission().getParentCheqiReceiptId());
        verify(api).matchCustomer(argThat(i -> "original".equals(i.getCheqiReceiptId())), any());
        assertNull(prepared.getDownloadSubmission());
    }
    @Test void preparedInvoicePreservesReceivingMetadataAcrossPersistence() throws Exception {
        var upload = new ClientReceiptDownloadRequest().downloadId("abcdefghijklmnopqrstuv").ciphertext("opaque")
                .creditNotePublicKey("public").documentReferenceHash(DocumentReferences.hash("INV-1"));
        var mapper = ObjectMapperConfig.getInstance();
        var restored = mapper.readValue(mapper.writeValueAsString(new PreparedReceiptDownload("url", upload)), PreparedReceiptDownload.class);
        assertEquals(upload, restored.getUploadRequest());
    }
}
