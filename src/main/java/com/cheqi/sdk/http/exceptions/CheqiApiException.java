package com.cheqi.sdk.http.exceptions;

/**
 * Exception thrown when Cheqi API communication fails.
 *
 * This exception provides detailed information about API failures including:
 * - HTTP status codes
 * - Error codes for programmatic handling
 * - Correlation IDs for debugging
 * - Detailed error messages
 * - Retry information
 */
public class CheqiApiException extends Exception {

    private final int httpStatusCode;
    private final String errorCode;
    private final String correlationId;
    private final boolean retryable;
    private final long timestamp;

    /**
     * Creates a CheqiApiException with full error details.
     *
     * @param message Human-readable error message
     * @param httpStatusCode HTTP status code from the API response
     * @param errorCode Machine-readable error code for programmatic handling
     * @param correlationId Correlation ID for request tracing
     */
    public CheqiApiException(String message, int httpStatusCode, String errorCode, String correlationId) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.correlationId = correlationId;
        this.retryable = isRetryableStatus(httpStatusCode);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a CheqiApiException with cause and full error details.
     *
     * @param message Human-readable error message
     * @param cause The underlying cause of the exception
     * @param httpStatusCode HTTP status code from the API response
     * @param errorCode Machine-readable error code for programmatic handling
     * @param correlationId Correlation ID for request tracing
     */
    public CheqiApiException(String message, Throwable cause, int httpStatusCode, String errorCode, String correlationId) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.correlationId = correlationId;
        this.retryable = isRetryableStatus(httpStatusCode);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a CheqiApiException for general failures without HTTP details.
     *
     * @param message Human-readable error message
     */
    public CheqiApiException(String message) {
        super(message);
        this.httpStatusCode = 0;
        this.errorCode = "UNKNOWN_ERROR";
        this.correlationId = null;
        this.retryable = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a CheqiApiException for general failures with cause.
     *
     * @param message Human-readable error message
     * @param cause The underlying cause of the exception
     */
    public CheqiApiException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = 0;
        this.errorCode = "UNKNOWN_ERROR";
        this.correlationId = null;
        this.retryable = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the HTTP status code from the API response.
     *
     * @return HTTP status code, or 0 if not applicable
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets the machine-readable error code for programmatic handling.
     *
     * @return Error code string
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the correlation ID for request tracing and debugging.
     *
     * @return Correlation ID, or null if not available
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Indicates whether this error is potentially retryable.
     *
     * @return true if the operation might succeed on retry, false otherwise
     */
    public boolean isRetryable() {
        return retryable;
    }

    /**
     * Gets the timestamp when this exception was created.
     *
     * @return Timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if this is an authentication/authorization error.
     *
     * @return true if this is a 401 or 403 error
     */
    public boolean isAuthenticationError() {
        return httpStatusCode == 401 || httpStatusCode == 403;
    }

    /**
     * Checks if this is a client error (4xx status codes).
     *
     * @return true if this is a client error
     */
    public boolean isClientError() {
        return httpStatusCode >= 400 && httpStatusCode < 500;
    }

    /**
     * Checks if this is a server error (5xx status codes).
     *
     * @return true if this is a server error
     */
    public boolean isServerError() {
        return httpStatusCode >= 500 && httpStatusCode < 600;
    }

    /**
     * Checks if this is a rate limiting error.
     *
     * @return true if this is a 429 (Too Many Requests) error
     */
    public boolean isRateLimitError() {
        return httpStatusCode == 429;
    }

    /**
     * Creates a detailed string representation of the exception.
     *
     * @return Formatted error details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CheqiApiException{");
        sb.append("message='").append(getMessage()).append('\'');

        if (httpStatusCode > 0) {
            sb.append(", httpStatusCode=").append(httpStatusCode);
        }

        if (errorCode != null) {
            sb.append(", errorCode='").append(errorCode).append('\'');
        }

        if (correlationId != null) {
            sb.append(", correlationId='").append(correlationId).append('\'');
        }

        sb.append(", retryable=").append(retryable);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');

        return sb.toString();
    }

    /**
     * Determines if an HTTP status code indicates a retryable error.
     *
     * @param statusCode HTTP status code
     * @return true if the error is potentially retryable
     */
    private static boolean isRetryableStatus(int statusCode) {
        // Server errors (5xx) are generally retryable
        if (statusCode >= 500 && statusCode < 600) {
            return true;
        }

        // Rate limiting is retryable with backoff
        if (statusCode == 429) {
            return true;
        }

        // Request timeout is retryable
        if (statusCode == 408) {
            return true;
        }

        // Client errors (4xx) are generally not retryable
        // Authentication errors (401, 403) are not retryable
        // Bad request (400) is not retryable
        // Not found (404) is not retryable
        return false;
    }

    /**
     * Common error codes used throughout the SDK.
     */
    public static final class ErrorCodes {
        public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
        public static final String AUTHORIZATION_FAILED = "AUTHORIZATION_FAILED";
        public static final String TEMPLATE_GENERATION_FAILED = "TEMPLATE_GENERATION_FAILED";
        public static final String INVALID_REQUEST = "INVALID_REQUEST";
        public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
        public static final String SERVER_ERROR = "SERVER_ERROR";
        public static final String NETWORK_ERROR = "NETWORK_ERROR";
        public static final String TIMEOUT_ERROR = "TIMEOUT_ERROR";
        public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
        public static final String NULL_TEMPLATE = "NULL_TEMPLATE";
        public static final String INVALID_RESPONSE = "INVALID_RESPONSE";

        private ErrorCodes() {
            // Utility class - prevent instantiation
        }
    }
}
