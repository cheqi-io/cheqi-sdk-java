package com.cheqi.sdk.exceptions;

/**
 * Main exception class for Cheqi SDK operations.
 * 
 * This exception is thrown when SDK operations fail due to:
 * - Invalid input parameters or configuration
 * - Network connectivity issues
 * - Backend API errors
 * - Encryption/decryption failures
 * - Authentication or authorization problems
 * 
 * The exception provides detailed error information including:
 * - Human-readable error messages
 * - Error codes for programmatic handling
 * - HTTP status codes when applicable
 * - Correlation IDs for debugging
 * - Underlying cause exceptions
 */
public class CheqiSDKException extends Exception {
    
    private final String errorCode;
    private final int httpStatusCode;
    private final String correlationId;

    /**
     * Creates a new CheqiSDKException with a message.
     *
     * @param message Human-readable error message
     */
    public CheqiSDKException(String message) {
        super(message);
        this.errorCode = ErrorCodes.UNKNOWN_ERROR;
        this.httpStatusCode = 0;
        this.correlationId = null;
    }

    /**
     * Creates a new CheqiSDKException with a message and cause.
     *
     * @param message Human-readable error message
     * @param cause Underlying exception that caused this error
     */
    public CheqiSDKException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCodes.UNKNOWN_ERROR;
        this.httpStatusCode = 0;
        this.correlationId = null;
    }

    /**
     * Creates a new CheqiSDKException with detailed error information.
     *
     * @param message Human-readable error message
     * @param errorCode SDK-specific error code for programmatic handling
     * @param httpStatusCode HTTP status code if this error originated from an API call
     * @param correlationId Correlation ID for debugging and tracing
     */
    public CheqiSDKException(String message, String errorCode, int httpStatusCode, String correlationId) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.correlationId = correlationId;
    }

    /**
     * Creates a new CheqiSDKException with detailed error information and cause.
     *
     * @param message Human-readable error message
     * @param cause Underlying exception that caused this error
     * @param errorCode SDK-specific error code for programmatic handling
     * @param httpStatusCode HTTP status code if this error originated from an API call
     * @param correlationId Correlation ID for debugging and tracing
     */
    public CheqiSDKException(String message, Throwable cause, String errorCode, int httpStatusCode, String correlationId) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.correlationId = correlationId;
    }

    /**
     * Gets the SDK-specific error code for programmatic error handling.
     *
     * @return Error code string, never null
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the HTTP status code if this error originated from an API call.
     *
     * @return HTTP status code, or 0 if not applicable
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets the correlation ID for debugging and request tracing.
     *
     * @return Correlation ID string, or null if not available
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Checks if this exception has an HTTP status code.
     *
     * @return true if httpStatusCode > 0
     */
    public boolean hasHttpStatusCode() {
        return httpStatusCode > 0;
    }

    /**
     * Checks if this exception has a correlation ID.
     *
     * @return true if correlationId is not null and not empty
     */
    public boolean hasCorrelationId() {
        return correlationId != null && !correlationId.trim().isEmpty();
    }

    /**
     * Checks if this is a client error (4xx HTTP status code).
     *
     * @return true if HTTP status code is between 400-499
     */
    public boolean isClientError() {
        return httpStatusCode >= 400 && httpStatusCode < 500;
    }

    /**
     * Checks if this is a server error (5xx HTTP status code).
     *
     * @return true if HTTP status code is between 500-599
     */
    public boolean isServerError() {
        return httpStatusCode >= 500 && httpStatusCode < 600;
    }

    /**
     * Checks if this is an authentication error.
     *
     * @return true if error code indicates authentication failure
     */
    public boolean isAuthenticationError() {
        return ErrorCodes.AUTHENTICATION_FAILED.equals(errorCode) || 
               ErrorCodes.INVALID_ACCESS_TOKEN.equals(errorCode);
    }

    /**
     * Checks if this is an authorization error.
     *
     * @return true if error code indicates authorization failure
     */
    public boolean isAuthorizationError() {
        return ErrorCodes.AUTHORIZATION_FAILED.equals(errorCode) || 
               ErrorCodes.INSUFFICIENT_PERMISSIONS.equals(errorCode);
    }

    /**
     * Checks if this is a validation error.
     *
     * @return true if error code indicates validation failure
     */
    public boolean isValidationError() {
        return ErrorCodes.VALIDATION_ERROR.equals(errorCode) || 
               ErrorCodes.INVALID_REQUEST.equals(errorCode);
    }

    /**
     * Checks if this is a network-related error.
     *
     * @return true if error code indicates network issues
     */
    public boolean isNetworkError() {
        return ErrorCodes.NETWORK_ERROR.equals(errorCode) || 
               ErrorCodes.TIMEOUT_ERROR.equals(errorCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CheqiSDKException{");
        sb.append("message='").append(getMessage()).append('\'');
        sb.append(", errorCode='").append(errorCode).append('\'');
        if (httpStatusCode > 0) {
            sb.append(", httpStatusCode=").append(httpStatusCode);
        }
        if (hasCorrelationId()) {
            sb.append(", correlationId='").append(correlationId).append('\'');
        }
        if (getCause() != null) {
            sb.append(", cause=").append(getCause().getClass().getSimpleName());
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Standard error codes used throughout the Cheqi SDK.
     */
    public static class ErrorCodes {
        // General errors
        public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
        public static final String INVALID_REQUEST = "INVALID_REQUEST";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String CONFIGURATION_ERROR = "CONFIGURATION_ERROR";

        // Authentication and authorization
        public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
        public static final String AUTHORIZATION_FAILED = "AUTHORIZATION_FAILED";
        public static final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
        public static final String INSUFFICIENT_PERMISSIONS = "INSUFFICIENT_PERMISSIONS";

        // Network and connectivity
        public static final String NETWORK_ERROR = "NETWORK_ERROR";
        public static final String TIMEOUT_ERROR = "TIMEOUT_ERROR";
        public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";

        // API and server errors
        public static final String SERVER_ERROR = "SERVER_ERROR";
        public static final String INVALID_RESPONSE = "INVALID_RESPONSE";
        public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";

        // SDK-specific errors
        public static final String ENCRYPTION_ERROR = "ENCRYPTION_ERROR";
        public static final String DECRYPTION_ERROR = "DECRYPTION_ERROR";
        public static final String KEY_GENERATION_ERROR = "KEY_GENERATION_ERROR";
        public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";
        public static final String RECEIPT_GENERATION_ERROR = "RECEIPT_GENERATION_ERROR";
        public static final String TEMPLATE_PROCESSING_ERROR = "TEMPLATE_PROCESSING_ERROR";

        // Data validation errors
        public static final String MISSING_REQUIRED_FIELD = "MISSING_REQUIRED_FIELD";
        public static final String INVALID_FIELD_VALUE = "INVALID_FIELD_VALUE";
        public static final String INVALID_CURRENCY_CODE = "INVALID_CURRENCY_CODE";
        public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
        public static final String INVALID_DATE_FORMAT = "INVALID_DATE_FORMAT";

        private ErrorCodes() {
            // Utility class - prevent instantiation
        }
    }
}
