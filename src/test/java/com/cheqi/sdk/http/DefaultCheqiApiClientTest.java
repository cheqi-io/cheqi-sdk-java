package com.cheqi.sdk.http;

import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.Product;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.ReceiptFormat;
import com.cheqi.sdk.models.generated.ReceiptTemplateGenerationRequest;
import com.cheqi.sdk.models.generated.UnitCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultCheqiApiClientTest {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfig.getInstance();

    @Test
    void generateReceiptTemplate_usesSharedObjectMapperForRequestSerialization() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = httpServer("/receipt/template", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes()));
            send(exchange, 200, "{\"ubl\":\"<Receipt/>\"}");
        });
        try {
            DefaultCheqiApiClient client = new DefaultCheqiApiClient(configFor(server));
            client.generateReceiptTemplate(generationRequest(), List.of(ReceiptFormat.UBL_PURCHASE_RECEIPT));

            String body = requestBody.get();
            assertEquals("Bearer sk_test_123", authorization.get());
            assertFalse(body.contains("100.00"));
            assertFalse(body.contains("21.00"));
            assertFalse(body.contains("121.00"));
            assertEquals("100", OBJECT_MAPPER.readTree(body).at("/receiptTemplateRequest/receiptSubtotal").asText());
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
            send(exchange, 200, "{\"customerFound\":true,\"matchId\":\"match-123\",\"recipients\":[]}");
        });
        try {
            DefaultCheqiApiClient client = new DefaultCheqiApiClient(configFor(server));
            IdentificationDetails request = new IdentificationDetails()
                    .paymentType(IdentificationDetails.PaymentTypeEnum.CARD_PAYMENT)
                    .recipientEmail("customer@example.com");

            var response = client.matchCustomer(request, "token-abc");
            assertEquals("Bearer token-abc", authorization.get());
            assertEquals("/recipient/resolve", path.get());
            assertEquals("match-123", response.getMatchId());
            assertEquals(Boolean.TRUE, response.getCustomerFound());
            assertNotNull(response.getRecipients());
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

    private static ReceiptTemplateGenerationRequest generationRequest() {
        ReceiptTemplateGenerationRequest request = new ReceiptTemplateGenerationRequest();
        request.setReceiptTemplateRequest(
                ReceiptTemplateRequest.builder()
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
                        .build()
        );
        request.setFormats(List.of(ReceiptFormat.UBL_PURCHASE_RECEIPT));
        return request;
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
