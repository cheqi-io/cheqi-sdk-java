package com.cheqi.sdk.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RequestLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final long SLOW_REQUEST_THRESHOLD_MS = 5000;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Instant startTime = Instant.now();

        if (logger.isDebugEnabled()) {
            logger.debug("→ {} {}", request.method(), request.url());
        }

        Response response;
        try {
            response = chain.proceed(request);
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("← {} {} ({}ms)", response.code(), request.url(), durationMs);
            }

            if (durationMs > SLOW_REQUEST_THRESHOLD_MS) {
                logger.warn("Slow request: {} {} took {}ms", request.method(), request.url(), durationMs);
            }

            return response;
        } catch (IOException e) {
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();
            logger.error("✗ {} {} failed after {}ms: {}", request.method(), request.url(), durationMs, e.getMessage());
            throw e;
        }
    }
}
