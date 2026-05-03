package com.cheqi.sdk.receipt;

import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.models.generated.*;
import com.cheqi.sdk.utils.HashUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private CheqiApiClient apiClient;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private MatchingService matchingService;

    @Test
    void processCompleteReceipt_returnsCustomerNotFoundWithoutTemplateGeneration() throws Exception {
        ReceiptService service = new ReceiptService(apiClient, encryptionService, matchingService);
        RecipientResolutionResponse matchResponse = new RecipientResolutionResponse().customerFound(false);
        when(matchingService.matchCustomer(any(IdentificationDetails.class))).thenReturn(matchResponse);

        ReceiptResult result = service.processCompleteReceipt(identificationDetails(null), receiptRequest());

        assertTrue(result.isCustomerNotFound());
        verify(apiClient, never()).generateReceiptTemplate(any(ReceiptTemplateGenerationRequest.class), any());
    }

    @Test
    void processCompleteReceipt_deliversEncryptedReceiptForMatchedCustomer() throws Exception {
        ReceiptService service = new ReceiptService(apiClient, encryptionService, matchingService);
        RecipientResolutionResponse matchResponse = matchedRecipientResponse();
        ReceiptTemplateResponse templateResponse = new ReceiptTemplateResponse().ublPurchaseReceipt("<Receipt>ok</Receipt>");
        ReceiptCreatedResponse createdResponse = new ReceiptCreatedResponse()
                .cheqiReceiptId("receipt-123")
                .templateHash("hash-123")
                .createdAt(OffsetDateTime.parse("2024-01-15T10:30:00Z"));
        EncryptedReceiptRequest encrypted = new EncryptedReceiptRequest()
                .recipientId("recipient-1")
                .encryptedReceipt("ciphertext")
                .encryptedSymmetricKey("wrapped-key");

        when(matchingService.matchCustomer(any(IdentificationDetails.class))).thenReturn(matchResponse);
        when(apiClient.generateReceiptTemplate(any(ReceiptTemplateGenerationRequest.class), any())).thenReturn(templateResponse);
        when(encryptionService.encryptReceiptForRecipients(eq("{\"ublPurchaseReceipt\":\"<Receipt>ok</Receipt>\"}"), any(MatchedRecipient.class))).thenReturn(encrypted);
        when(apiClient.sendEncryptedReceipts(eq("match-123"), anySet(), any(String.class))).thenReturn(createdResponse);

        ReceiptResult result = service.processCompleteReceipt(identificationDetails(null), receiptRequest());

        assertTrue(result.isSuccess());
        assertEquals("receipt-123", result.getCheqiReceiptId());
        assertEquals("hash-123", result.getTemplateHash());

        ArgumentCaptor<String> templateHashCaptor = ArgumentCaptor.forClass(String.class);
        verify(apiClient).sendEncryptedReceipts(eq("match-123"), anySet(), templateHashCaptor.capture());
        assertEquals(HashUtils.sha256Hex("<Receipt>ok</Receipt>"), templateHashCaptor.getValue());
    }

    @Test
    void processCompleteReceipt_encryptsOnlyAcceptedFormatsForEachRecipient() throws Exception {
        ReceiptService service = new ReceiptService(apiClient, encryptionService, matchingService);
        MatchedRecipient ublRecipient = new MatchedRecipient()
                .id("ubl-recipient")
                .publicKey("ubl-public-key")
                .acceptedFormats(List.of(ReceiptFormat.UBL_PURCHASE_RECEIPT));
        MatchedRecipient invoiceRecipient = new MatchedRecipient()
                .id("invoice-recipient")
                .publicKey("invoice-public-key")
                .acceptedFormats(List.of(ReceiptFormat.UBL_INVOICE));
        RecipientResolutionResponse matchResponse = new RecipientResolutionResponse()
                .customerFound(true)
                .matchId("match-123")
                .recipients(List.of(ublRecipient, invoiceRecipient));
        ReceiptTemplateResponse templateResponse = new ReceiptTemplateResponse()
                .ublPurchaseReceipt("<Receipt>ok</Receipt>")
                .ublInvoice("<Invoice>ok</Invoice>");
        ReceiptCreatedResponse createdResponse = new ReceiptCreatedResponse()
                .cheqiReceiptId("receipt-123")
                .templateHash("hash-123")
                .createdAt(OffsetDateTime.parse("2024-01-15T10:30:00Z"));

        when(matchingService.matchCustomer(any(IdentificationDetails.class))).thenReturn(matchResponse);
        when(apiClient.generateReceiptTemplate(any(ReceiptTemplateGenerationRequest.class), any())).thenReturn(templateResponse);
        when(encryptionService.encryptReceiptForRecipients(any(String.class), any(MatchedRecipient.class)))
                .thenAnswer(invocation -> {
                    MatchedRecipient recipient = invocation.getArgument(1);
                    return new EncryptedReceiptRequest()
                            .recipientId(recipient.getId())
                            .encryptedReceipt("ciphertext-" + recipient.getId())
                            .encryptedSymmetricKey("wrapped-key-" + recipient.getId());
                });
        when(apiClient.sendEncryptedReceipts(eq("match-123"), anySet(), any(String.class))).thenReturn(createdResponse);

        ReceiptResult result = service.processCompleteReceipt(identificationDetails(null), receiptRequest());

        assertTrue(result.isSuccess());
        ArgumentCaptor<String> envelopeCaptor = ArgumentCaptor.forClass(String.class);
        verify(encryptionService, times(2)).encryptReceiptForRecipients(envelopeCaptor.capture(), any(MatchedRecipient.class));
        assertTrue(envelopeCaptor.getAllValues().contains("{\"ublPurchaseReceipt\":\"<Receipt>ok</Receipt>\"}"));
        assertTrue(envelopeCaptor.getAllValues().contains("{\"ublInvoice\":\"<Invoice>ok</Invoice>\"}"));
    }

    private static RecipientResolutionResponse matchedRecipientResponse() {
        MatchedRecipient recipient = new MatchedRecipient()
                .id("recipient-1")
                .publicKey("public-key")
                .acceptedFormats(List.of(ReceiptFormat.UBL_PURCHASE_RECEIPT));

        return new RecipientResolutionResponse()
                .customerFound(true)
                .matchId("match-123")
                .recipients(List.of(recipient));
    }

    private static IdentificationDetails identificationDetails(String email) {
        return new IdentificationDetails()
                .paymentType(PaymentType.CARD_PAYMENT)
                .recipientEmail(email);
    }

    private static ReceiptTemplateRequest receiptRequest() {
        return ReceiptTemplateRequest.builder()
                .documentNumber("INV-001")
                .issueDate(Instant.parse("2024-01-15T10:30:00Z"))
                .currency("EUR")
                .receiptSubtotal(new BigDecimal("100.00"))
                .totalBeforeTax(new BigDecimal("100.00"))
                .totalTaxAmount(new BigDecimal("21.00"))
                .totalAmount(new BigDecimal("121.00"))
                .addProduct(Product.builder()
                        .name("Widget")
                        .quantity(1.0)
                        .unitCode(UnitCode.C62)
                        .unitPrice("100.00")
                        .subtotal("100.00")
                        .total("121.00")
                        .addTax(21.0, "VAT", "100.00", "21.00")
                        .build())
                .addTax(21.0, "VAT", "100.00", "21.00")
                .build();
    }
}
