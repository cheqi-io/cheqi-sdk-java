package com.cheqi.sdk.receipt;

import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.download.DownloadLink;
import com.cheqi.sdk.download.DownloadService;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptPayload;
import com.cheqi.sdk.models.generated.CardDetails;
import com.cheqi.sdk.models.generated.ClientReceiptDownloadRequest;
import com.cheqi.sdk.models.generated.ClientReceiptDownloadResponse;
import com.cheqi.sdk.models.generated.EncryptedReceiptEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.PaymentType;
import com.cheqi.sdk.models.generated.ReceiptEnvelope;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    @Test
    void completesDownloadFallbackWithOriginalIdentificationDetailsIncludingPar() throws Exception {
        RecipientResolutionResponse resolution = new RecipientResolutionResponse()
                .routeFound(true)
                .deliveryRouteType(RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK)
                .matchId("match-download");
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenReturn(resolution);
        when(apiClient.uploadEncryptedDownloadReceipt(
                org.mockito.ArgumentMatchers.any(ClientReceiptDownloadRequest.class)
        )).thenReturn(new ClientReceiptDownloadResponse().cheqiReceiptId("CHQ-DL"));

        DownloadService downloads = new DownloadService();
        service = new ReceiptService(
                apiClient,
                encryptionService,
                matchingService,
                downloads,
                "https://receipt.example"
        );
        IdentificationDetails identification = new IdentificationDetails()
                .paymentType(PaymentType.CARD_PAYMENT)
                .cardDetails(new CardDetails()
                        .cardProvider(CardDetails.CardProviderEnum.VISA)
                        .paymentAccountReference("PAR-123")
                        .lastFourDigits("4242"));

        ReceiptResult result = service.issueReceipt(identification, receipt());

        assertTrue(result.isAccepted());
        assertEquals("CHQ-DL", result.getCheqiReceiptId());
        assertEquals(
                RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK,
                result.getDeliveryRouteType()
        );
        DownloadLink link = downloads.parseDownloadUrl(result.getDownloadUrl());
        ArgumentCaptor<ClientReceiptDownloadRequest> upload =
                ArgumentCaptor.forClass(ClientReceiptDownloadRequest.class);
        verify(apiClient).uploadEncryptedDownloadReceipt(upload.capture());
        ReceiptEnvelope envelope = downloads.decryptDownloadEnvelope(
                upload.getValue().getCiphertext(),
                link.getContentKey()
        );
        assertEquals(2, envelope.getEnvelopeVersion());
        String document = envelope.getDocuments().get("RECEIPT_PAYLOAD").getContent();
        String identificationDocument = envelope.getDocuments().get("IDENTIFICATION_DETAILS").getContent();
        assertEquals("R-100", ObjectMapperConfig.getInstance().readTree(document)
                .get("documentNumber").asText());
        assertFalse(ObjectMapperConfig.getInstance().readTree(document).has("identificationDetails"));
        assertEquals("CARD_PAYMENT", ObjectMapperConfig.getInstance().readTree(identificationDocument)
                .at("/paymentType").asText());
        assertEquals("PAR-123", ObjectMapperConfig.getInstance().readTree(identificationDocument)
                .at("/cardDetails/paymentAccountReference").asText());
        assertEquals("4242", ObjectMapperConfig.getInstance().readTree(identificationDocument)
                .at("/cardDetails/lastFourDigits").asText());
        assertTrue(upload.getValue().getTemplateHash() != null);
    }

    @Test
    void explicitCashDownloadDoesNotAttemptCustomerMatching() throws Exception {
        when(apiClient.uploadEncryptedDownloadReceipt(
                org.mockito.ArgumentMatchers.any(ClientReceiptDownloadRequest.class),
                org.mockito.ArgumentMatchers.eq("company-token")
        )).thenReturn(new ClientReceiptDownloadResponse().cheqiReceiptId("CHQ-CASH"));
        DownloadService downloads = new DownloadService();
        service = new ReceiptService(
                apiClient,
                encryptionService,
                matchingService,
                downloads,
                "https://receipt.example"
        );

        ReceiptResult result = service.issueDownloadReceipt(
                new IdentificationDetails().paymentType(PaymentType.CASH),
                receipt(),
                "company-token"
        );

        assertTrue(result.isAccepted());
        assertEquals("CHQ-CASH", result.getCheqiReceiptId());
        assertEquals(
                RecipientResolutionResponse.DeliveryRouteTypeEnum.DOWNLOAD_FALLBACK,
                result.getDeliveryRouteType()
        );
        verify(apiClient, never()).matchCustomer(
                org.mockito.ArgumentMatchers.any(IdentificationDetails.class),
                org.mockito.ArgumentMatchers.anyString()
        );
        ArgumentCaptor<ClientReceiptDownloadRequest> upload =
                ArgumentCaptor.forClass(ClientReceiptDownloadRequest.class);
        verify(apiClient).uploadEncryptedDownloadReceipt(upload.capture(),
                org.mockito.ArgumentMatchers.eq("company-token"));
        DownloadLink link = downloads.parseDownloadUrl(result.getDownloadUrl());
        ReceiptEnvelope envelope = downloads.decryptDownloadEnvelope(
                upload.getValue().getCiphertext(),
                link.getContentKey()
        );
        String document = envelope.getDocuments().get("IDENTIFICATION_DETAILS").getContent();
        assertEquals("CASH", ObjectMapperConfig.getInstance().readTree(document)
                .at("/paymentType").asText());
    }

    private void digitalMatch() throws Exception {
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenReturn(new RecipientResolutionResponse().routeFound(true).matchId("match-123")
                        .deliveryRouteType(RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL)
                        .recipients(List.of(new MatchedRecipient().id("device-1").publicKey("key-1"))));
        encryptionService.deliveries = List.of(encrypted("device-1", "new-ciphertext"));
    }

    private static com.cheqi.sdk.http.exceptions.CheqiApiException networkError() {
        return new com.cheqi.sdk.http.exceptions.CheqiApiException("timeout",
                new java.net.SocketTimeoutException("timeout"), 0, "NETWORK_ERROR", null);
    }

    @Test
    void submissionTimeoutReturnsDigitalPendingWithDurableFallbackAndNoMatchingIdentifiers() throws Exception {
        digitalMatch();
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                .thenThrow(networkError());
        var identification = new IdentificationDetails().paymentType(PaymentType.CARD_PAYMENT)
                .cardDetails(new CardDetails().paymentAccountReference("PAR-not-retained"));
        var result = service.issueReceipt(identification, receipt());
        assertEquals(ReceiptIssueState.DIGITAL_PENDING, result.getState());
        assertEquals("match-123", result.getMatchId());
        assertEquals(null, result.getSubmission());
        assertTrue(result.isRetryable());
        assertTrue(!result.isAccepted());
        var prepared = result.getPreparedDownload();
        assertEquals(result.getDownloadUrl(), prepared.getDownloadUrl());
        var downloads = new DownloadService();
        var link = downloads.parseDownloadUrl(prepared.getDownloadUrl());
        var decoded = downloads.decryptDownloadEnvelope(prepared.getCiphertext(), link.getContentKey());
        assertEquals(2, decoded.getEnvelopeVersion());
        assertFalse(decoded.getDocuments().containsKey("IDENTIFICATION_DETAILS"));
        var json = decoded.getDocuments().get("RECEIPT_PAYLOAD").getContent();
        assertTrue(!json.contains("PAR-not-retained"));
        assertTrue(!json.contains("identificationDetails"));
        assertEquals("R-100", ObjectMapperConfig.getInstance().readTree(json).get("documentNumber").asText());
        verify(apiClient, never()).uploadEncryptedDownloadReceipt(
                org.mockito.ArgumentMatchers.any(ClientReceiptDownloadRequest.class));
    }

    @Test
    void unreachableMatchingReturnsLocalDownloadWithoutAMatchOrBlockingUpload() throws Exception {
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenThrow(networkError());
        var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt());
        assertEquals(ReceiptIssueState.DOWNLOAD_FALLBACK, result.getState());
        assertEquals(null, result.getMatchId());
        assertTrue(result.getDownloadUrl() != null);
        assertTrue(result.isRetryable());
        assertTrue(encryptionService.plaintexts.isEmpty());
        verify(apiClient, never()).uploadEncryptedDownloadReceipt(
                org.mockito.ArgumentMatchers.any(ClientReceiptDownloadRequest.class));
    }

    @Test
    void inProgressSubmissionReturnsPendingAndRetryDelay() throws Exception {
        digitalMatch();
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                .thenThrow(new com.cheqi.sdk.http.exceptions.SubmissionInProgressException(2));
        var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt());
        assertEquals(ReceiptIssueState.DIGITAL_PENDING, result.getState());
        assertEquals(2, result.getRetryAfterSeconds());
    }

    @Test
    void permanentMatchingAndSubmissionErrorsStillThrowWithApiDetails() throws Exception {
        for (int status : List.of(400, 401, 403)) {
            when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                    .thenThrow(new com.cheqi.sdk.http.exceptions.CheqiApiException("permanent", status, "API_ERROR", "correlation"));
            var error = assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                    () -> service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt()));
            assertEquals(status, error.getHttpStatusCode());
            assertEquals("API_ERROR", error.getErrorCode());
            assertEquals("correlation", error.getCorrelationId());
        }
        org.mockito.Mockito.reset(apiClient);
        digitalMatch();
        for (int status : List.of(400, 401, 403, 404)) {
            encryptionService.plaintexts.clear();
            when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                    .thenThrow(new com.cheqi.sdk.http.exceptions.CheqiApiException("permanent", status, "API_ERROR", null));
            var error = assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                    () -> service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt()));
            assertEquals(status, error.getHttpStatusCode());
        }
    }

    @Test
    void resumeRecoversOriginalSubmissionWithoutMatchingOrEncryption() throws Exception {
        var submitted = new ReceiptSubmissionResponse().cheqiReceiptId("CHQ-original").matchId("match-123")
                .status(ReceiptSubmissionResponse.StatusEnum.PENDING);
        when(apiClient.getMatch("match-123")).thenReturn(new com.cheqi.sdk.models.generated.MatchStatusResponse()
                .matchId("match-123").state(com.cheqi.sdk.models.generated.MatchState.SUBMITTED).submission(submitted));
        var result = service.resumeReceipt("match-123", receipt());
        assertEquals(ReceiptIssueState.DIGITAL_SUBMITTED, result.getState());
        assertEquals(submitted, result.getSubmission());
        assertTrue(!result.isRetryable());
        assertTrue(encryptionService.plaintexts.isEmpty());
        verify(apiClient, never()).matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class));
        verify(apiClient, never()).submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class));
    }

    @Test
    void resumeMatchedRegeneratesCiphertextUsingOnlyMatchIdAndPayloadWithCompanyToken() throws Exception {
        var recipient = new MatchedRecipient().id("original-device").publicKey("original-key");
        when(apiClient.getMatch("match-123", "rotated-token"))
                .thenReturn(new com.cheqi.sdk.models.generated.MatchStatusResponse().matchId("match-123")
                        .state(com.cheqi.sdk.models.generated.MatchState.MATCHED)
                        .route(com.cheqi.sdk.models.generated.MatchStatusResponse.RouteEnum.DIGITAL)
                        .recipients(List.of(recipient)));
        encryptionService.deliveries = List.of(encrypted("original-device", "regenerated-ciphertext"));
        var submitted = new ReceiptSubmissionResponse().matchId("match-123").cheqiReceiptId("CHQ-recovered");
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class),
                org.mockito.ArgumentMatchers.eq("rotated-token"))).thenReturn(submitted);
        var storeId = UUID.randomUUID();
        var result = service.resumeReceipt("match-123", receipt(), storeId, "rotated-token");
        assertEquals(ReceiptIssueState.DIGITAL_SUBMITTED, result.getState());
        assertEquals(List.of(recipient), encryptionService.recipients);
        var captor = ArgumentCaptor.forClass(EncryptedReceiptEnvelope.class);
        verify(apiClient).submitEncryptedReceipt(captor.capture(), org.mockito.ArgumentMatchers.eq("rotated-token"));
        assertEquals(storeId, captor.getValue().getStoreId());
        assertEquals("match-123", captor.getValue().getMatchId());
        assertEquals("regenerated-ciphertext", captor.getValue().getDeviceDeliveries().get(0).getEncryptedContent());
        assertTrue(!encryptionService.plaintexts.get(0).contains("identificationDetails"));
        verify(apiClient, never()).matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void resumeInProgressAndNetworkFailureReturnPendingWithoutNewQr() throws Exception {
        when(apiClient.getMatch("match-123")).thenReturn(new com.cheqi.sdk.models.generated.MatchStatusResponse()
                .matchId("match-123").state(com.cheqi.sdk.models.generated.MatchState.IN_PROGRESS).retryAfterSeconds(2));
        var result = service.resumeReceipt("match-123", receipt());
        assertEquals(ReceiptIssueState.DIGITAL_PENDING, result.getState());
        assertEquals(2, result.getRetryAfterSeconds());
        assertEquals(null, result.getDownloadUrl());
        when(apiClient.getMatch("match-123")).thenThrow(networkError());
        assertEquals(ReceiptIssueState.DIGITAL_PENDING, service.resumeReceipt("match-123", receipt()).getState());
        assertTrue(encryptionService.plaintexts.isEmpty());
    }

    @Test
    void resumeExpiredIsTypedAndUnauthorizedMatchStillThrows() throws Exception {
        when(apiClient.getMatch("match-123")).thenReturn(new com.cheqi.sdk.models.generated.MatchStatusResponse()
                .matchId("match-123").state(com.cheqi.sdk.models.generated.MatchState.EXPIRED));
        var expired = service.resumeReceipt("match-123", receipt());
        assertEquals(ReceiptIssueState.DIGITAL_EXPIRED, expired.getState());
        assertEquals("match-123", expired.getMatchId());
        assertTrue(!expired.isRetryable());
        assertTrue(!expired.isAccepted());
        assertTrue(encryptionService.plaintexts.isEmpty());
        verify(apiClient, never()).submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class));
        when(apiClient.getMatch("match-123")).thenThrow(
                new com.cheqi.sdk.http.exceptions.CheqiApiException("Match not found", 404, "NOT_FOUND", null));
        assertEquals(404, assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.resumeReceipt("match-123", receipt())).getHttpStatusCode());
        verify(apiClient, never()).matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class));
    }

    @Test
    void preparedDownloadCanBePersistedAndUploadedRepeatedlyWithoutChangingQrOrCiphertext() throws Exception {
        when(apiClient.matchCustomer(org.mockito.ArgumentMatchers.any(IdentificationDetails.class)))
                .thenThrow(networkError());
        var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt());
        var mapper = ObjectMapperConfig.getInstance();
        var original = result.getPreparedDownload();
        var restored = mapper.readValue(mapper.writeValueAsString(original), PreparedReceiptDownload.class);
        assertEquals(original.getDownloadUrl(), restored.getDownloadUrl());
        assertEquals(original.getCiphertext(), restored.getCiphertext());
        when(apiClient.uploadEncryptedDownloadReceipt(org.mockito.ArgumentMatchers.any(ClientReceiptDownloadRequest.class)))
                .thenThrow(networkError()).thenReturn(new ClientReceiptDownloadResponse().cheqiReceiptId("CHQ-DL"));
        assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class, () -> service.uploadPreparedDownload(restored));
        assertEquals("CHQ-DL", service.uploadPreparedDownload(restored).getCheqiReceiptId());
        var requests = ArgumentCaptor.forClass(ClientReceiptDownloadRequest.class);
        verify(apiClient, org.mockito.Mockito.times(2)).uploadEncryptedDownloadReceipt(requests.capture());
        for (var request : requests.getAllValues()) {
            assertEquals(original.getDownloadId(), request.getDownloadId());
            assertEquals(original.getCiphertext(), request.getCiphertext());
            assertEquals(original.getTemplateHash(), request.getTemplateHash());
        }
        assertTrue(!original.toString().contains(original.getDownloadUrl()));
    }

    @Test
    void invalidPayloadFailsBeforeMatchingEvenWhenBackendIsOffline() {
        assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"),
                        new com.cheqi.sdk.models.generated.ReceiptPayload()));
        org.mockito.Mockito.verifyNoInteractions(apiClient);
    }

    @Test
    void publicEnvelopeGenerationUsesGeneratedMatchesWithoutNetworkCalls() throws Exception {
        var recipient = new MatchedRecipient().id("device-1").publicKey("key-1");
        encryptionService.deliveries = List.of(encrypted("device-1", "ciphertext"));
        var storeId = UUID.randomUUID();
        var resolution = new RecipientResolutionResponse().routeFound(true).matchId("match-123")
                .deliveryRouteType(RecipientResolutionResponse.DeliveryRouteTypeEnum.DIGITAL)
                .recipients(List.of(recipient));
        var envelope = service.generateEncryptedReceiptEnvelope(resolution, receipt(), storeId);
        assertEquals("match-123", envelope.getMatchId());
        assertEquals(storeId, envelope.getStoreId());
        assertEquals("ciphertext", envelope.getDeviceDeliveries().get(0).getEncryptedContent());
        var retrieved = new com.cheqi.sdk.models.generated.MatchStatusResponse().matchId("match-123")
                .state(com.cheqi.sdk.models.generated.MatchState.MATCHED)
                .route(com.cheqi.sdk.models.generated.MatchStatusResponse.RouteEnum.DIGITAL)
                .recipients(List.of(recipient));
        encryptionService.plaintexts.clear();
        assertEquals(envelope, service.generateEncryptedReceiptEnvelope(retrieved, receipt(), storeId));
        org.mockito.Mockito.verifyNoInteractions(apiClient);
        for (var state : List.of(com.cheqi.sdk.models.generated.MatchState.SUBMITTED,
                com.cheqi.sdk.models.generated.MatchState.EXPIRED,
                com.cheqi.sdk.models.generated.MatchState.IN_PROGRESS)) {
            retrieved.setState(state);
            assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                    () -> service.generateEncryptedReceiptEnvelope(retrieved, receipt(), storeId));
        }
    }

    @Test
    void invalidSuccessfulSubmissionProducesPendingQrAndPreservesMatchId() throws Exception {
        digitalMatch();
        for (int status : List.of(0, 202)) {
            encryptionService.plaintexts.clear();
            when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                    .thenThrow(new com.cheqi.sdk.http.exceptions.CheqiApiException("Invalid response", status,
                            com.cheqi.sdk.http.exceptions.CheqiApiException.ErrorCodes.INVALID_RESPONSE, null));
            var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt());
            assertEquals(ReceiptIssueState.DIGITAL_PENDING, result.getState());
            assertEquals("match-123", result.getMatchId());
            assertTrue(result.isRetryable());
            assertTrue(result.getDownloadUrl() != null);
            assertTrue(result.getPreparedDownload() != null);
        }
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                .thenReturn(null);
        encryptionService.plaintexts.clear();
        assertEquals(ReceiptIssueState.DIGITAL_PENDING,
                service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt()).getState());
    }

    @Test
    void submissionExpiryRaceReturnsTypedNonRetryableOutcome() throws Exception {
        digitalMatch();
        when(apiClient.submitEncryptedReceipt(org.mockito.ArgumentMatchers.any(EncryptedReceiptEnvelope.class)))
                .thenThrow(new com.cheqi.sdk.http.exceptions.CheqiApiException("Expired", 410, "MATCH_EXPIRED", null));
        var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), receipt());
        assertEquals(ReceiptIssueState.DIGITAL_EXPIRED, result.getState());
        assertEquals("match-123", result.getMatchId());
        assertTrue(!result.isRetryable());
        assertTrue(!result.isAccepted());
        when(apiClient.getMatch("match-123")).thenReturn(new com.cheqi.sdk.models.generated.MatchStatusResponse()
                .matchId("match-123").state(com.cheqi.sdk.models.generated.MatchState.MATCHED)
                .route(com.cheqi.sdk.models.generated.MatchStatusResponse.RouteEnum.DIGITAL)
                .recipients(List.of(new MatchedRecipient().id("device-1").publicKey("key-1"))));
        encryptionService.plaintexts.clear();
        assertEquals(ReceiptIssueState.DIGITAL_EXPIRED, service.resumeReceipt("match-123", receipt()).getState());
    }

    @Test
    void lowLevelSubmissionPreservesApiDetailsAndRetryDelay() throws Exception {
        var envelope = new EncryptedReceiptEnvelope().matchId("match-123")
                .deviceDeliveries(List.of(encrypted("device-1", "ciphertext")));
        var apiError = new com.cheqi.sdk.http.exceptions.CheqiApiException(
                "Unauthorized", 401, "AUTHENTICATION_FAILED", "correlation-123");
        when(apiClient.submitEncryptedReceipt(envelope)).thenThrow(apiError);
        var error = assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.submitEncryptedReceipt(envelope));
        assertEquals("AUTHENTICATION_FAILED", error.getErrorCode());
        assertEquals(401, error.getHttpStatusCode());
        assertEquals("correlation-123", error.getCorrelationId());
        assertEquals(apiError, error.getCause());
        assertEquals(null, error.getRetryAfterSeconds());

        var inProgress = new com.cheqi.sdk.http.exceptions.SubmissionInProgressException(7);
        when(apiClient.submitEncryptedReceipt(envelope, "token")).thenThrow(inProgress);
        error = assertThrows(com.cheqi.sdk.exceptions.CheqiSDKException.class,
                () -> service.submitEncryptedReceipt(envelope, "token"));
        assertEquals("SUBMISSION_IN_PROGRESS", error.getErrorCode());
        assertEquals(202, error.getHttpStatusCode());
        assertEquals(7, error.getRetryAfterSeconds());
        assertEquals(inProgress, error.getCause());
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
