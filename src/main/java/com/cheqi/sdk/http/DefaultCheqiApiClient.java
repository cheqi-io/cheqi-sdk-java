package com.cheqi.sdk.http;

import com.cheqi.commons.DTOs.*;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DefaultCheqiApiClient implements CheqiApiClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCheqiApiClient.class);

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String TEMPLATE_ENDPOINT = "/receipt/template";
    private static final String CUSTOMER_MATCH_ENDPOINT = "/recipient/resolve";
    private static final String ENCRYPTED_RECEIPT_ENDPOINT = "/receipt/encrypted";
    private static final String USER_AGENT = "CheqiSDK/1.0";

    private final CheqiSDKConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DefaultCheqiApiClient(CheqiSDKConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        // Register JDK8 module to handle Optional fields properly
        this.objectMapper.registerModule(new Jdk8Module());
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);

        this.httpClient = createHttpClient();
    }

    @Override
    public String generateReceiptTemplate(ReceiptTemplateRequestDto request, String accessToken) throws CheqiApiException {
        logger.info("Generating receipt template for receipt ID: {}", request.getReceiptId());

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for template generation",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Template request JSON: {}", requestJson);

            // Build HTTP request - Accept XML since backend now returns XML string
            RequestBody body = RequestBody.create(requestJson, JSON);
                Request httpRequest = new Request.Builder()
                        .url(config.getApiEndpoint() + TEMPLATE_ENDPOINT)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/xml")
                        .addHeader("User-Agent", "CheqiSDK/1.0")
                        .build();

            // Execute request directly - let applications handle retries
            Response response = executeRequest(httpRequest);

            try (ResponseBody responseBody = response.body()) {
                String responseJSON = responseBody != null ? responseBody.string() : "";
                String correlationId = response.header("X-Correlation-ID");

                if (response.isSuccessful()) {
                    logger.info("Template generation successful for receipt ID: {}", request.getReceiptId());

                    if (responseJSON.trim().isEmpty()) {
                        throw new CheqiApiException(
                                "Backend returned empty JSON response",
                                response.code(),
                                CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                                correlationId
                        );
                    }
                    return responseJSON;
                } else {
                    logger.error("Template generation failed. Status: {}, Response: {}", response.code(), responseJSON);

                    // Handle authentication errors specifically
                    if (response.code() == 401) {
                        throw new CheqiApiException(
                                "Invalid or expired access token",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED,
                                correlationId
                        );
                    }

                    if (response.code() == 403) {
                        throw new CheqiApiException(
                                "Access token does not have required permissions for template generation",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED,
                                correlationId
                        );
                    }

                    // Determine appropriate error code based on status
                    String errorCode = determineErrorCode(response.code());

                    // Try to parse error response for detailed message
                    String errorMessage = parseErrorMessage(responseJSON, "Template generation failed");

                    throw new CheqiApiException(
                            errorMessage,
                            response.code(),
                            errorCode,
                            correlationId
                    );
                }
            }

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during template generation", e);
            throw new CheqiApiException(
                    "Network error during template generation: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during template generation", e);
            throw new CheqiApiException(
                    "Template generation failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public RecipientResolutionResponse matchCustomer(PaymentDetails request, String accessToken) throws CheqiApiException {
        logger.info("Matching customer with payment identifiers");

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for customer matching",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (request == null) {
            throw new CheqiApiException(
                    "RecipientResolutionRequest is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(request);
            logger.info("Customer match request JSON: {}", requestJson);

            // Build HTTP request
            RequestBody body = RequestBody.create(requestJson, JSON);
            Request httpRequest = new Request.Builder()
                    .url(config.getApiEndpoint() + CUSTOMER_MATCH_ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "CheqiSDK/1.0")
                    .build();

            // Execute request
            Response response = executeRequest(httpRequest);

            try (ResponseBody responseBody = response.body()) {
                String responseJson = responseBody != null ? responseBody.string() : "";
                String correlationId = response.header("X-Correlation-ID");

                if (response.isSuccessful()) {
                    logger.info("Customer matching completed successfully");

                    if (responseJson.trim().isEmpty()) {
                        throw new CheqiApiException(
                                "Backend returned empty response",
                                response.code(),
                                CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                                correlationId
                        );
                    }

                    RecipientResolutionResponse matchResponse = objectMapper.readValue(responseJson, RecipientResolutionResponse.class);

                    if (matchResponse == null) {
                        throw new CheqiApiException(
                                "Backend returned null RecipientResolutionResponse",
                                response.code(),
                                CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                                correlationId
                        );
                    }

                    return matchResponse;
                } else {
                    logger.error("Customer matching failed. Status: {}, Response: {}", response.code(), responseJson);

                    // Handle authentication errors specifically
                    if (response.code() == 401) {
                        throw new CheqiApiException(
                                "Invalid or expired access token",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED,
                                correlationId
                        );
                    }

                    if (response.code() == 403) {
                        throw new CheqiApiException(
                                "Access token does not have required permissions for customer matching",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED,
                                correlationId
                        );
                    }

                    // Determine appropriate error code based on status
                    String errorCode = determineErrorCode(response.code());

                    // Try to parse error response for detailed message
                    String errorMessage = parseErrorMessage(responseJson, "Customer matching failed");

                    throw new CheqiApiException(
                            errorMessage,
                            response.code(),
                            errorCode,
                            correlationId
                    );
                }
            }

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during customer matching", e);
            throw new CheqiApiException(
                    "Network error during customer matching: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during customer matching", e);
            throw new CheqiApiException(
                    "Customer matching failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public void sendEncryptedReceipts(Set<EncryptedReceiptDto> encryptedReceipts, String templateHash, String accessToken) throws CheqiApiException {
        logger.info("Sending {} encrypted receipts for customer: ", encryptedReceipts.size());

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for sending encrypted receipts",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (encryptedReceipts == null || encryptedReceipts.isEmpty()) {
            throw new CheqiApiException(
                    "Encrypted receipts set cannot be null or empty",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            EncryptedReceiptsRequest request = new EncryptedReceiptsRequest(encryptedReceipts, templateHash);
            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Encrypted receipts request JSON: {}", requestJson);

            // Build HTTP requests
            RequestBody body = RequestBody.create(requestJson, JSON);
            Request httpRequest = new Request.Builder()
                    .url(config.getApiEndpoint() + ENCRYPTED_RECEIPT_ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "CheqiSDK/1.0")
                    .build();

            // Execute request
            Response response = executeRequest(httpRequest);

            try (ResponseBody responseBody = response.body()) {
                String responseJson = responseBody != null ? responseBody.string() : "";
                String correlationId = response.header("X-Correlation-ID");

                if (response.isSuccessful()) {
                    logger.info("Successfully sent {} encrypted receipts for customer", encryptedReceipts.size());
                    // Backend returns 201 Created with no body for successful submission
                    return;
                } else {
                    logger.error("Failed to send encrypted receipts. Status: {}, Response: {}", response.code(), responseJson);

                    // Handle authentication errors specifically
                    if (response.code() == 401) {
                        throw new CheqiApiException(
                                "Invalid or expired access token",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED,
                                correlationId
                        );
                    }

                    if (response.code() == 403) {
                        throw new CheqiApiException(
                                "Access token does not have required 'write_receipts' scope",
                                response.code(),
                                CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED,
                                correlationId
                        );
                    }

                    // Determine appropriate error code based on status
                    String errorCode = determineErrorCode(response.code());

                    // Try to parse error response for detailed message
                    String errorMessage = parseErrorMessage(responseJson, "Failed to send encrypted receipts");

                    throw new CheqiApiException(
                            errorMessage,
                            response.code(),
                            errorCode,
                            correlationId
                    );
                }
            }

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during encrypted receipt submission", e);
            throw new CheqiApiException(
                    "Network error during encrypted receipt submission: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during encrypted receipt submission", e);
            throw new CheqiApiException(
                    "Encrypted receipt submission failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    /**
     * Executes HTTP request directly without retry logic.
     * Applications should implement their own retry strategies based on business needs.
     */
    private Response executeRequest(Request request) throws CheqiApiException {
        try {
            return httpClient.newCall(request).execute();
        } catch (IOException e) {
            logger.error("Network error during HTTP request", e);
            throw new CheqiApiException(
                    "Network error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        }
    }

    /**
     * Determines appropriate error code based on HTTP status code.
     */
    private String determineErrorCode(int statusCode) {
        switch (statusCode) {
            case 400:
                return CheqiApiException.ErrorCodes.INVALID_REQUEST;
            case 401:
                return CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED;
            case 403:
                return CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED;
            case 408:
                return CheqiApiException.ErrorCodes.TIMEOUT_ERROR;
            case 429:
                return CheqiApiException.ErrorCodes.RATE_LIMIT_EXCEEDED;
            case 500:
            case 502:
            case 503:
            case 504:
                return CheqiApiException.ErrorCodes.SERVER_ERROR;
            default:
                return CheqiApiException.ErrorCodes.UNKNOWN_ERROR;
        }
    }

    /**
     * Attempts to parse error message from API response JSON.
     */
    private String parseErrorMessage(String responseJson, String defaultMessage) {
        try {
            if (responseJson != null && !responseJson.trim().isEmpty()) {
                var errorResponse = objectMapper.readTree(responseJson);
                if (errorResponse.has("message")) {
                    return errorResponse.get("message").asText();
                }
                if (errorResponse.has("error")) {
                    return errorResponse.get("error").asText();
                }
                if (errorResponse.has("error_description")) {
                    return errorResponse.get("error_description").asText();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to parse error message from response: {}", e.getMessage());
        }
        return defaultMessage;
    }

    /**
     * Creates configured OkHttpClient with timeouts and connection pooling.
     */
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(false) // We handle retries manually
                .build();
    }
}
