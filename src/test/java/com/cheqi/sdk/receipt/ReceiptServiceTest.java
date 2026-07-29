package com.cheqi.sdk.receipt;

import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptPayload;
import com.cheqi.sdk.models.generated.EncryptedReceiptEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import com.cheqi.sdk.models.generated.UnitCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReceiptServiceTest {
    private CheqiApiClient apiClient;
    private StubEncryptionService encryptionService;
    private MatchingService matchingService;
    private ReceiptService service;

    @BeforeEach
    void setUp() {
        apiClient = mock(CheqiApiClient.class);
        encryptionService = new StubEncryptionService();
        matchingService = new MatchingService(apiClient);
        service = new ReceiptService(apiClient, encryptionService, matchingService);
    }

    @Test
    void issuesDefinitivePayloadUnchangedToEveryResolvedDevice() throws Exception {
        MatchedRecipient first = new MatchedRecipient().id("device-1").publicKey("key-1");
        MatchedRecipient second = new MatchedRecipient().id("device-2").publicKey("key-2");
        RecipientResolutionResponse resolution = new RecipientResolutionResponse()
                .routeFound(true)
                .matchId("match-123")
                .recipients(List.of(first, second));
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenReturn(resolution);

        EncryptedReceiptPayload firstDelivery = encrypted("device-1", "cipher-1");
        EncryptedReceiptPayload secondDelivery = encrypted("device-2", "cipher-2");
        encryptionService.deliveries = List.of(firstDelivery, secondDelivery);

        OffsetDateTime createdAt = OffsetDateTime.parse("2026-07-29T10:15:30Z");
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                .thenReturn(new ReceiptSubmissionResponse()
                        .cheqiReceiptId("CHQ-123")
                        .matchId("match-123")
                        .status(ReceiptSubmissionResponse.StatusEnum.PENDING)
                        .createdAt(createdAt));

        UUID storeId = UUID.randomUUID();
        ReceiptResult result = service.issueReceipt(
                new IdentificationDetails().recipientEmail("buyer@example.com"),
                receipt(),
                storeId
        );

        assertTrue(result.isAccepted());
        assertEquals("CHQ-123", result.getCheqiReceiptId());
        assertEquals("match-123", result.getMatchId());
        assertEquals(ReceiptSubmissionResponse.StatusEnum.PENDING, result.getStatus());
        assertEquals(createdAt, result.getCreatedAt());

        assertEquals(List.of(first, second), encryptionService.recipients);
        assertEquals(encryptionService.plaintexts.get(0), encryptionService.plaintexts.get(1));
        String json = encryptionService.plaintexts.get(1);
        assertTrue(json.contains("\"documentNumber\":\"R-100\""));
        assertEquals("10.1", ObjectMapperConfig.getInstance().readTree(json)
                .get("receiptSubtotal").asText());
        assertEquals("12.34", ObjectMapperConfig.getInstance().readTree(json)
                .get("totalAmount").asText());
        assertTrue(!json.contains("receiptTemplateRequest"));

        ArgumentCaptor<EncryptedReceiptEnvelope> envelope =
                ArgumentCaptor.forClass(EncryptedReceiptEnvelope.class);
        verify(apiClient).submitEncryptedReceipt(envelope.capture());
        assertEquals("match-123", envelope.getValue().getMatchId());
        assertEquals(storeId, envelope.getValue().getStoreId());
        assertEquals(List.of(firstDelivery, secondDelivery), envelope.getValue().getDeviceDeliveries());
    }

    @Test
    void doesNotEncryptOrSubmitWhenNoOwnerDeviceRouteExists() throws Exception {
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenReturn(new RecipientResolutionResponse().routeFound(false));

        assertThrows(
                com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.issueReceipt(
                        new IdentificationDetails().recipientEmail("buyer@example.com"), receipt())
        );

        assertTrue(encryptionService.plaintexts.isEmpty());
    }

    private static EncryptedReceiptPayload encrypted(String recipientId, String content) {
        return new EncryptedReceiptPayload()
                .deviceRecipientId(recipientId)
                .encryptedContent(content)
                .encryptedAesKey("encrypted-key-" + recipientId);
    }

    private static ReceiptPayload receipt() {
        return ReceiptPayload.builder()
                .documentNumber("R-100")
                .issueDate(OffsetDateTime.parse("2026-07-29T10:00:00Z"))
                .currency("EUR")
                .receiptSubtotal("10.10")
                .totalBeforeTax("10.10")
                .totalTaxAmount("2.24")
                .totalAmount("12.34")
                .taxesApplied(true)
                .addProduct(Product.builder()
                        .name("Coffee")
                        .identifier("COFFEE-1")
                        .quantity(1.0)
                        .unitCode(UnitCode.C62)
                        .unitPrice("10.10")
                        .subtotal("10.10")
                        .total("12.34")
                        .build())
                .build();
    }

    private static final class StubEncryptionService extends EncryptionService {
        private final List<String> plaintexts = new ArrayList<>();
        private final List<MatchedRecipient> recipients = new ArrayList<>();
        private List<EncryptedReceiptPayload> deliveries = List.of();

        @Override
        public EncryptedReceiptPayload encryptReceiptForRecipient(
                String receiptPayloadJson,
                MatchedRecipient recipient
        ) {
            plaintexts.add(receiptPayloadJson);
            recipients.add(recipient);
            return deliveries.get(plaintexts.size() - 1);
        }
    }
}
