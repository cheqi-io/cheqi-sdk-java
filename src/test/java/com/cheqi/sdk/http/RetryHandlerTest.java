package com.cheqi.sdk.http;

import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.sun.net.httpserver.HttpServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryHandlerTest {

    @Test
    void executeWithRetry_retriesTransientServerError() throws Exception {
        AtomicInteger requestCount = new AtomicInteger();
        HttpServer server = httpServer(exchange -> {
            int attempt = requestCount.incrementAndGet();
            if (attempt == 1) {
                send(exchange, 500, "{\"message\":\"temporary\"}");
            } else {
                send(exchange, 200, "{\"ok\":true}");
            }
        });
        try {
            RetryHandler retryHandler = new RetryHandler(new OkHttpClient(), 1);
            Request request = new Request.Builder().url("http://127.0.0.1:" + server.getAddress().getPort() + "/retry").build();

            try (Response response = retryHandler.executeWithRetry(request, "retry-test")) {
                assertEquals(200, response.code());
            }
            assertEquals(2, requestCount.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void executeWithRetry_throwsAfterExhaustingRetries() throws Exception {
        AtomicInteger requestCount = new AtomicInteger();
        HttpServer server = httpServer(exchange -> {
            requestCount.incrementAndGet();
            send(exchange, 503, "{\"message\":\"unavailable\"}");
        });
        try {
            RetryHandler retryHandler = new RetryHandler(new OkHttpClient(), 1);
            Request request = new Request.Builder().url("http://127.0.0.1:" + server.getAddress().getPort() + "/retry").build();

            try (Response response = retryHandler.executeWithRetry(request, "retry-test")) {
                assertEquals(503, response.code());
            }
            assertEquals(2, requestCount.get());
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer httpServer(com.sun.net.httpserver.HttpHandler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/retry", handler);
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
