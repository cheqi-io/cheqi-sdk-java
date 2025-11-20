package com.cheqi.sdk.http;

import com.cheqi.sdk.http.exceptions.CheqiApiException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Handles HTTP request retry logic with exponential backoff.
 *
 * Features:
 * - Automatic retry for transient failures (429, 408, 5xx)
 * - Exponential backoff with jitter to prevent thundering herd
 * - Respects Retry-After header from server
 * - Configurable max retry attempts
 */
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

    private final OkHttpClient httpClient;
    private final int maxRetries;

    public RetryHandler(OkHttpClient httpClient, int maxRetries) {
        this.httpClient = httpClient;
        this.maxRetries = maxRetries;
    }

    /**
     * Executes HTTP request with automatic retry logic for transient failures.
     */
    public Response executeWithRetry(Request request, String operationName) throws CheqiApiException {
        int attempt = 0;
        CheqiApiException lastException = null;
        Instant startTime = Instant.now();

        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    logger.debug("Retry attempt {} for {}", attempt, operationName);
                }

                Response response = httpClient.newCall(request).execute();

                if (shouldRetry(response, attempt)) {
                    int statusCode = response.code();
                    String retryAfter = response.header("Retry-After");
                    response.close();

                    long delayMs = calculateBackoff(attempt, statusCode, retryAfter);
                    logger.warn("{} failed with status {} (attempt {}/{}), retrying in {}ms",
                            operationName, statusCode, attempt + 1, maxRetries + 1, delayMs);

                    Thread.sleep(delayMs);
                    attempt++;
                    continue;
                }

                long durationMs = Duration.between(startTime, Instant.now()).toMillis();
                logger.debug("{} completed in {}ms (attempt {})", operationName, durationMs, attempt + 1);

                return response;

            } catch (IOException e) {
                attempt++;
                lastException = new CheqiApiException(
                        "Network error: " + e.getMessage(),
                        e,
                        0,
                        CheqiApiException.ErrorCodes.NETWORK_ERROR,
                        null
                );

                if (attempt <= maxRetries) {
                    long delayMs = calculateBackoff(attempt, 0, null);
                    logger.warn("{} network error (attempt {}/{}), retrying in {}ms: {}",
                            operationName, attempt, maxRetries + 1, delayMs, e.getMessage());
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw lastException;
                    }
                } else {
                    logger.error("{} failed after {} attempts", operationName, attempt);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CheqiApiException(
                        "Request interrupted: " + e.getMessage(),
                        e,
                        0,
                        CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                        null
                );
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        throw new CheqiApiException(
                operationName + " failed after " + (maxRetries + 1) + " attempts",
                0,
                CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                null
        );
    }

    private boolean shouldRetry(Response response, int attempt) {
        if (attempt >= maxRetries) {
            return false;
        }

        int statusCode = response.code();
        return statusCode == 429 || statusCode == 408 || (statusCode >= 500 && statusCode < 600);
    }

    private long calculateBackoff(int attempt, int statusCode, String retryAfter) {
        if (retryAfter != null && !retryAfter.isEmpty()) {
            try {
                return Long.parseLong(retryAfter) * 1000;
            } catch (NumberFormatException e) {
                logger.debug("Failed to parse Retry-After header: {}", retryAfter);
            }
        }

        long baseDelayMs = (statusCode == 429) ? 5000 : 1000;
        long exponentialDelay = baseDelayMs * (1L << attempt);
        double jitter = 0.75 + (Math.random() * 0.5);
        long delayWithJitter = (long) (exponentialDelay * jitter);

        return Math.min(delayWithJitter, 30000);
    }
}
