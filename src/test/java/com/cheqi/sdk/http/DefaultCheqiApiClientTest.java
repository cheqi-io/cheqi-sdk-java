package com.cheqi.sdk.http;

import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultCheqiApiClientTest {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfig.getInstance();

    @Test
    void inviteUsersPostsEmailsAndReturnsOutcomes() throws Exception {
        var companyId = java.util.UUID.randomUUID();
        AtomicReference<String> body = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = httpServer("/company/" + companyId + "/invite/employees", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            body.set(new String(exchange.getRequestBody().readAllBytes()));
            send(exchange, 200, "{\"invitedEmails\":[\"employee@example.com\"],\"pendingEmails\":[],\"existingMembers\":[]}");
        });
        try {
            InviteEmployeesResponse result = new DefaultCheqiApiClient(configFor(server))
                    .inviteUsers(companyId, List.of("employee@example.com"), true, "oauth-token");
            assertEquals("Bearer oauth-token", authorization.get());
            assertEquals("employee@example.com", OBJECT_MAPPER.readTree(body.get()).at("/emails/0").asText());
            assertEquals(true, OBJECT_MAPPER.readTree(body.get()).at("/resendPending").asBoolean());
            assertEquals(List.of("employee@example.com"), result.getInvitedEmails());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void receiptDestinationAndWebhookMethodsUseProductionRoutes() throws Exception {
        var destinationId = java.util.UUID.randomUUID();
        var subscriptionId = java.util.UUID.randomUUID();
        List<String> calls = new ArrayList<>();
        List<String> bodies = new ArrayList<>();
        HttpServer server = httpServer("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            calls.add(exchange.getRequestMethod() + " " + path);
            bodies.add(new String(exchange.getRequestBody().readAllBytes()));
            String response = calls.size() == 1 || calls.size() == 3 ? "[]" : "{}";
            send(exchange, 200, response);
        });
        try {
            var client = new DefaultCheqiApiClient(configFor(server));
            assertEquals(List.of(), client.listReceiptDestinations("oauth-token"));
            client.registerReceiptDestination(new RegisterRequest().name("ERP").publicKey("key").keyAlgorithm("RSA"), "oauth-token");
            assertEquals(List.of(), client.getPendingReceipts(destinationId, "oauth-token"));
            client.acknowledgeReceipts(destinationId, List.of("receipt-1"), "oauth-token");
            client.deactivateReceiptDestination(destinationId, "oauth-token");
            client.updateWebhookSubscriptionUrl(subscriptionId, "https://example.com/hook", "oauth-token");
            assertEquals(List.of(
                    "GET /company/receipt-destinations",
                    "POST /company/receipt-destinations",
                    "GET /company/receipt-destinations/" + destinationId + "/receipts/queue",
                    "POST /company/receipt-destinations/" + destinationId + "/receipts/queue/acknowledge",
                    "DELETE /company/receipt-destinations/" + destinationId,
                    "PATCH /webhook/subscription/" + subscriptionId
            ), calls);
            assertEquals("receipt-1", OBJECT_MAPPER.readTree(bodies.get(3)).at("/receiptIds/0").asText());
            assertEquals("https://example.com/hook", OBJECT_MAPPER.readTree(bodies.get(5)).at("/notificationUrl").asText());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void submitEncryptedReceipt_postsGeneratedEnvelopeToSingularReceiptRoute() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        HttpServer server = httpServer("/receipt/encrypted", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            path.set(exchange.getRequestURI().getPath());
            requestBody.set(new String(exchange.getRequestBody().readAllBytes()));
            send(exchange, 202, "{\"cheqiReceiptId\":\"CHQ-123\",\"matchId\":\"match-123\",\"status\":\"PENDING\"}");
        });
        try {
            DefaultCheqiApiClient client = new DefaultCheqiApiClient(configFor(server));
            ReceiptSubmissionResponse response = client.submitEncryptedReceipt(
                    new EncryptedReceiptEnvelope()
                            .matchId("match-123")
                            .deviceDeliveries(List.of(new EncryptedReceiptPayload()
                                    .deviceRecipientId("device-1")
                                    .encryptedContent("ciphertext")
                                    .encryptedAesKey("encrypted-key")))
            );

            assertEquals("Bearer sk_test_123", authorization.get());
            assertEquals("/receipt/encrypted", path.get());
            assertEquals("match-123", OBJECT_MAPPER.readTree(requestBody.get()).get("matchId").asText());
            assertEquals("device-1", OBJECT_MAPPER.readTree(requestBody.get())
                    .at("/deviceDeliveries/0/deviceRecipientId").asText());
            assertEquals("CHQ-123", response.getCheqiReceiptId());
            assertEquals(ReceiptSubmissionResponse.StatusEnum.PENDING, response.getStatus());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void matchCustomer_sendsBearerTokenAndParsesResponse() throws Exception {
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        HttpServer server = httpServer("/recipient/resolve", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            path.set(exchange.getRequestURI().getPath());
            send(exchange, 200, "{\"routeFound\":true,\"matchId\":\"match-123\",\"recipients\":[]}");
        });
        try {
            DefaultCheqiApiClient client = new DefaultCheqiApiClient(configFor(server));
            IdentificationDetails request;
            request = new IdentificationDetails()
                    .paymentType(PaymentType.CARD_PAYMENT)
                    .recipientEmail("customer@example.com");

            var response = client.matchCustomer(request, "token-abc");
            assertEquals("Bearer token-abc", authorization.get());
            assertEquals("/recipient/resolve", path.get());
            assertEquals("match-123", response.getMatchId());
            assertEquals(Boolean.TRUE, response.getRouteFound());
            assertNotNull(response.getRecipients());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void uploadEncryptedDownloadReceipt_postsContractBody() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = httpServer("/receipt/download", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes()));
            send(exchange, 201, "{\"cheqiReceiptId\":\"CHQ-JAVA-1\"}");
        });
        try {
            DefaultCheqiApiClient client = new DefaultCheqiApiClient(configFor(server));
            ClientReceiptDownloadResponse response = client.uploadEncryptedDownloadReceipt(
                    new ClientReceiptDownloadRequest()
                            .downloadId("Zk9qYx3vT1KpN8wL2sRd_g")
                            .ciphertext("AAAA")
                            .templateHash("hash-1"));

            assertEquals("Bearer sk_test_123", authorization.get());
            assertEquals("Zk9qYx3vT1KpN8wL2sRd_g", OBJECT_MAPPER.readTree(requestBody.get()).get("downloadId").asText());
            assertEquals("CHQ-JAVA-1", response.getCheqiReceiptId());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void retrievesMatchWithApiKeyOrDelegatedTokenWithoutMatchingIdentifiers() throws Exception {
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<String> method = new AtomicReference<>();
        HttpServer server = httpServer("/recipient/matches/match_opaque", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            method.set(exchange.getRequestMethod());
            send(exchange, 200, "{\"state\":\"MATCHED\",\"matchId\":\"match_opaque\",\"route\":\"DIGITAL\","
                    + "\"recipients\":[{\"id\":\"rcpt_original\",\"publicKey\":\"key\"}]}");
        });
        try {
            var client = new DefaultCheqiApiClient(configFor(server));
            var match = client.getMatch("match_opaque");
            assertEquals(MatchState.MATCHED, match.getState());
            assertEquals("rcpt_original", match.getRecipients().get(0).getId());
            assertEquals("GET", method.get());
            assertEquals("Bearer sk_test_123", authorization.get());
            client.getMatch("match_opaque", "rotated-token");
            assertEquals("Bearer rotated-token", authorization.get());
        } finally { server.stop(0); }
    }

    @Test
    void inProgressSubmissionIsNotDeserializedAsSuccessfulReceipt() throws Exception {
        HttpServer server = httpServer("/receipt/encrypted", exchange ->
                send(exchange, 202, "{\"state\":\"IN_PROGRESS\",\"matchId\":\"match-123\",\"retryAfterSeconds\":2}"));
        try {
            var client = new DefaultCheqiApiClient(configFor(server));
            var error = org.junit.jupiter.api.Assertions.assertThrows(
                    com.cheqi.sdk.http.exceptions.SubmissionInProgressException.class,
                    () -> client.submitEncryptedReceipt(testEnvelope()));
            assertEquals(2, error.getRetryAfterSeconds());
            org.junit.jupiter.api.Assertions.assertTrue(error.isRetryable());
        } finally { server.stop(0); }
    }

    @Test
    void zeroMaxRetriesDisablesExistingHttpRetries() throws Exception {
        var count = new java.util.concurrent.atomic.AtomicInteger();
        HttpServer server = httpServer("/", exchange -> {
            count.incrementAndGet();
            send(exchange, 503, "{\"message\":\"Temporary outage\"}");
        });
        try {
            var config = CheqiSDKConfig.builder().apiKey("sk_test_123")
                    .customApiEndpoint("http://127.0.0.1:" + server.getAddress().getPort())
                    .timeoutSeconds(5).maxRetries(0).build();
            var client = new DefaultCheqiApiClient(config);
            org.junit.jupiter.api.Assertions.assertThrows(com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.matchCustomer(new IdentificationDetails().recipientEmail("buyer@example.com")));
            org.junit.jupiter.api.Assertions.assertThrows(com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.submitEncryptedReceipt(testEnvelope()));
            org.junit.jupiter.api.Assertions.assertThrows(com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.getMatch("match-123"));
            org.junit.jupiter.api.Assertions.assertThrows(com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.uploadEncryptedDownloadReceipt(new ClientReceiptDownloadRequest()
                            .downloadId("Zk9qYx3vT1KpN8wL2sRd_g").ciphertext("encrypted")));
            assertEquals(4, count.get());
        } finally { server.stop(0); }
    }

    @Test
    void malformedSubmissionAndExpiredOrUnauthorizedMatchesRemainErrors() throws Exception {
        HttpServer server = httpServer("/", exchange -> {
            if (exchange.getRequestURI().getPath().startsWith("/recipient/matches")) {
                send(exchange, 404, "{\"message\":\"Match not found\"}");
            } else {
                send(exchange, 202, "{}");
            }
        });
        try {
            var client = new DefaultCheqiApiClient(configFor(server));
            var invalid = org.junit.jupiter.api.Assertions.assertThrows(
                    com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.submitEncryptedReceipt(testEnvelope()));
            assertEquals("INVALID_RESPONSE", invalid.getErrorCode());
            var absent = org.junit.jupiter.api.Assertions.assertThrows(
                    com.cheqi.sdk.http.exceptions.CheqiApiException.class,
                    () -> client.getMatch("another-requester-match"));
            assertEquals(404, absent.getHttpStatusCode());
        } finally { server.stop(0); }
    }


    @Test
    void lostSubmissionResponseIsRecoveredThroughStatusWithoutAnotherSubmission() throws Exception {
        var matches = new java.util.concurrent.atomic.AtomicInteger();
        var submissions = new java.util.concurrent.atomic.AtomicInteger();
        var committed = new java.util.concurrent.atomic.AtomicBoolean();
        HttpServer server = httpServer("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if ("/recipient/resolve".equals(path)) {
                matches.incrementAndGet();
                send(exchange, 200, "{\"routeFound\":true,\"matchId\":\"match-123\",\"deliveryRouteType\":\"DIGITAL\","
                        + "\"recipients\":[{\"id\":\"device-1\",\"publicKey\":\"original-key\"}]}");
            } else if ("/receipt/encrypted".equals(path)) {
                submissions.incrementAndGet();
                committed.set(true);
                // Acceptance committed, but the connection loses the acknowledgement body.
                exchange.sendResponseHeaders(202, 2048);
                exchange.getResponseBody().write("{\"cheqiReceiptId\":".getBytes());
                exchange.close();
            } else if ("/recipient/matches/match-123".equals(path) && committed.get()) {
                send(exchange, 200, "{\"state\":\"SUBMITTED\",\"matchId\":\"match-123\",\"submission\":"
                        + "{\"cheqiReceiptId\":\"CHQ-original\",\"matchId\":\"match-123\",\"status\":\"PENDING\","
                        + "\"createdAt\":\"2026-09-27T09:00:00Z\"}}");
            } else {
                send(exchange, 404, "{\"message\":\"Not found\"}");
            }
        });
        try {
            var client = new DefaultCheqiApiClient(configFor(server));
            var encryptions = new java.util.concurrent.atomic.AtomicInteger();
            var encryption = new com.cheqi.sdk.encryption.EncryptionService() {
                @Override
                public EncryptedReceiptPayload encryptReceiptForRecipient(String json, MatchedRecipient recipient) {
                    encryptions.incrementAndGet();
                    return testEnvelope().getDeviceDeliveries().get(0);
                }
            };
            var service = new com.cheqi.sdk.receipt.ReceiptService(client, encryption,
                    new com.cheqi.sdk.matching.MatchingService(client));
            var payload = new ReceiptPayload().documentNumber("R-100").currency("EUR")
                    .issueDate(java.time.OffsetDateTime.parse("2026-09-27T08:00:00Z"))
                    .receiptSubtotal(java.math.BigDecimal.TEN).totalBeforeTax(java.math.BigDecimal.TEN)
                    .totalTaxAmount(java.math.BigDecimal.ZERO).totalAmount(java.math.BigDecimal.TEN)
                    .taxesApplied(false).products(List.of(new Product().name("Coffee")));
            var pending = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), payload);
            assertEquals(com.cheqi.sdk.receipt.ReceiptIssueState.DIGITAL_PENDING, pending.getState());
            assertNotNull(pending.getDownloadUrl());
            var recovered = service.resumeReceipt(pending.getMatchId(), payload);
            assertEquals(com.cheqi.sdk.receipt.ReceiptIssueState.DIGITAL_SUBMITTED, recovered.getState());
            assertEquals("CHQ-original", recovered.getSubmission().getCheqiReceiptId());
            assertEquals(java.time.OffsetDateTime.parse("2026-09-27T09:00:00Z"), recovered.getCreatedAt());
            assertEquals(1, matches.get());
            assertEquals(1, submissions.get());
            assertEquals(1, encryptions.get());
        } finally { server.stop(0); }
    }

    @Test
    void emptyAndMalformedSuccessfulSubmissionsRemainPendingWithQr() throws Exception {
        for (String responseBody : List.of("", "{", "null", "{}", "[]")) {
            HttpServer server = httpServer("/", exchange -> {
                if ("/recipient/resolve".equals(exchange.getRequestURI().getPath())) {
                    send(exchange, 200, "{\"routeFound\":true,\"matchId\":\"match-123\",\"deliveryRouteType\":\"DIGITAL\","
                            + "\"recipients\":[{\"id\":\"device-1\",\"publicKey\":\"key\"}]}");
                } else {
                    send(exchange, 202, responseBody);
                }
            });
            try {
                var client = new DefaultCheqiApiClient(configFor(server));
                var encryption = new com.cheqi.sdk.encryption.EncryptionService() {
                    @Override
                    public EncryptedReceiptPayload encryptReceiptForRecipient(String json, MatchedRecipient recipient) {
                        return testEnvelope().getDeviceDeliveries().get(0);
                    }
                };
                var service = new com.cheqi.sdk.receipt.ReceiptService(client, encryption,
                        new com.cheqi.sdk.matching.MatchingService(client));
                var payload = new ReceiptPayload().documentNumber("R-100").currency("EUR")
                        .issueDate(java.time.OffsetDateTime.parse("2026-09-27T08:00:00Z"))
                        .receiptSubtotal(java.math.BigDecimal.TEN).totalBeforeTax(java.math.BigDecimal.TEN)
                        .totalTaxAmount(java.math.BigDecimal.ZERO).totalAmount(java.math.BigDecimal.TEN)
                        .taxesApplied(false).products(List.of(new Product().name("Coffee")));
                var result = service.issueReceipt(new IdentificationDetails().recipientEmail("buyer@example.com"), payload);
                assertEquals(com.cheqi.sdk.receipt.ReceiptIssueState.DIGITAL_PENDING, result.getState());
                assertEquals("match-123", result.getMatchId());
                assertNotNull(result.getPreparedDownload());
                assertNotNull(result.getDownloadUrl());
            } finally { server.stop(0); }
        }
    }

    private static EncryptedReceiptEnvelope testEnvelope() {
        return new EncryptedReceiptEnvelope().matchId("match-123").deviceDeliveries(
                List.of(new EncryptedReceiptPayload().deviceRecipientId("device-1")
                        .encryptedContent("ciphertext").encryptedAesKey("wrapped-key")));
    }

    private static CheqiSDKConfig configFor(HttpServer server) {
        return CheqiSDKConfig.builder()
                .customApiEndpoint("http://127.0.0.1:" + server.getAddress().getPort())
                .apiKey("sk_test_123")
                .timeoutSeconds(5)
                .maxRetries(0)
                .build();
    }

    private static HttpServer httpServer(String path, com.sun.net.httpserver.HttpHandler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(path, handler);
        server.start();
        return server;
    }

    private static void send(com.sun.net.httpserver.HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        } finally {
            exchange.close();
        }
    }
}
