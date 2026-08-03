package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreditNoteServiceTest {
    @Test
    void submitsCreditNoteThroughItsOwnGeneratedEnvelope() throws Exception {
        CheqiApiClient apiClient = mock(CheqiApiClient.class);
        StubEncryptionService encryptionService = new StubEncryptionService();
        MatchingService matchingService = new MatchingService(apiClient);
        CreditNoteService service = new CreditNoteService(apiClient, encryptionService, matchingService);

        MatchedRecipient recipient = new MatchedRecipient().id("device-1").publicKey("public-key");
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenReturn(new RecipientResolutionResponse()
                        .routeFound(true)
                        .matchId("match-credit")
                        .recipients(List.of(recipient)));
        EncryptedReceiptPayload encrypted = new EncryptedReceiptPayload()
                .deviceRecipientId("device-1")
                .encryptedContent("ciphertext")
                .encryptedAesKey("encrypted-key");
        encryptionService.delivery = encrypted;
        when(apiClient.submitEncryptedCreditNote(
                org.mockito.ArgumentMatchers.any(EncryptedCreditNoteEnvelope.class)))
                .thenReturn(new ReceiptSubmissionResponse()
                        .cheqiReceiptId("CHQ-CN-1")
                        .matchId("match-credit")
                        .status(ReceiptSubmissionResponse.StatusEnum.PENDING));

        CreditNoteResult result = service.issueCreditNote(
                new IdentificationDetails().cheqiReceiptId("CHQ-PARENT"),
                "CHQ-PARENT",
                Map.of("documentNumber", "CN-1", "totalAmount", "5.00")
        );

        assertTrue(result.isAccepted());
        assertEquals("CHQ-CN-1", result.getCheqiReceiptId());
        assertEquals("CHQ-PARENT", result.getParentCheqiReceiptId());

        assertEquals(List.of(recipient), encryptionService.recipients);
        assertTrue(encryptionService.plaintexts.get(0)
                .contains("\"documentNumber\":\"CN-1\""));

        ArgumentCaptor<EncryptedCreditNoteEnvelope> envelope =
                ArgumentCaptor.forClass(EncryptedCreditNoteEnvelope.class);
        verify(apiClient).submitEncryptedCreditNote(envelope.capture());
        assertEquals("match-credit", envelope.getValue().getMatchId());
        assertEquals("CHQ-PARENT", envelope.getValue().getParentCheqiReceiptId());
        assertEquals(List.of(encrypted), envelope.getValue().getDeviceDeliveries());
    }

    private static final class StubEncryptionService extends EncryptionService {
        private final List<String> plaintexts = new ArrayList<>();
        private final List<MatchedRecipient> recipients = new ArrayList<>();
        private EncryptedReceiptPayload delivery;

        @Override
        public EncryptedReceiptPayload encryptCreditNoteForRecipient(
                String creditNotePayloadJson,
                MatchedRecipient recipient
        ) {
            plaintexts.add(creditNotePayloadJson);
            recipients.add(recipient);
            return delivery;
        }
    }
}
