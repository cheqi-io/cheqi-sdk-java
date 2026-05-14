package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.*;
import com.cheqi.sdk.utils.HashUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditNoteServiceTest {

    @Mock
    private CheqiApiClient apiClient;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private MatchingService matchingService;

    @Test
    void processCompleteCreditNote_deliversEncryptedCreditNoteForMatchedCustomer() throws Exception {
        CreditNoteService service = new CreditNoteService(apiClient, encryptionService, matchingService);
        RecipientResolutionResponse matchResponse = matchedRecipientResponse();
        CreditNoteTemplateResponse templateResponse = new CreditNoteTemplateResponse().ublCreditNote("<CreditNote>ok</CreditNote>");
        CreditNoteCreatedResponse createdResponse = new CreditNoteCreatedResponse()
                .cheqiReceiptId("credit-note-123")
                .parentCheqiReceiptId("receipt-123")
                .templateHash("hash-456")
                .createdAt(OffsetDateTime.parse("2024-01-15T10:30:00Z"));
        EncryptedCreditNote encryptedCreditNote = new EncryptedCreditNote()
                .recipientId("recipient-1")
                .encryptedCreditNote("ciphertext")
                .encryptedSymmetricKey("wrapped-key");

        when(matchingService.matchCustomer(any(IdentificationDetails.class))).thenReturn(matchResponse);
        when(apiClient.generateCreditNoteTemplate(any(CreditNoteTemplateGenerationRequest.class))).thenReturn(templateResponse);
        when(encryptionService.encryptCreditNoteForRecipient(eq("{\"ublCreditNote\":\"<CreditNote>ok</CreditNote>\"}"), any(MatchedRecipient.class)))
                .thenReturn(encryptedCreditNote);
        when(apiClient.sendEncryptedCreditNotes(eq("match-123"), eq("receipt-123"), anySet(), any(String.class))).thenReturn(createdResponse);

        CreditNoteResult result = service.processCompleteCreditNote(identificationDetails(), creditNoteTemplateRequest());

        assertTrue(result.isSuccess());
        assertEquals("credit-note-123", result.getCheqiReceiptId());
        assertEquals("receipt-123", result.getParentCheqiReceiptId());

        ArgumentCaptor<String> templateHashCaptor = ArgumentCaptor.forClass(String.class);
        verify(apiClient).sendEncryptedCreditNotes(eq("match-123"), eq("receipt-123"), anySet(), templateHashCaptor.capture());
        assertEquals(HashUtils.sha256Hex("<CreditNote>ok</CreditNote>"), templateHashCaptor.getValue());
    }

    private static RecipientResolutionResponse matchedRecipientResponse() {
        MatchedRecipient recipient = new MatchedRecipient()
                .id("recipient-1")
                .publicKey("public-key")
                .acceptedFormats(List.of(ReceiptFormat.UBL_CREDIT_NOTE));

        return new RecipientResolutionResponse()
                .routeFound(true)
                .matchId("match-123")
                .buyerCountryCode("NL")
                .recipients(List.of(recipient));
    }

    private static IdentificationDetails identificationDetails() {
        return new IdentificationDetails()
                .paymentType(PaymentType.CARD_PAYMENT)
                .cheqiReceiptId("receipt-123");
    }

    private static CreditNoteTemplateRequest creditNoteTemplateRequest() {
        return new CreditNoteTemplateRequest()
                .documentNumber("CN-001");
    }
}
