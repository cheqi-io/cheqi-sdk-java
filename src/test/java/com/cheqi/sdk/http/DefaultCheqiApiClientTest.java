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
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultCheqiApiClientTest {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfig.getInstance();

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
