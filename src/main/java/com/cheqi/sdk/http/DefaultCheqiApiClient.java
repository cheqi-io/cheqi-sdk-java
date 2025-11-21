package com.cheqi.sdk.http;

import com.cheqi.commons.DTOs.*;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.PaymentDetails;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
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
    private static final String USER_AGENT = "CheqiSDK/1.0";

    private final CheqiSDKConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RetryHandler retryHandler;
    private final ResponseHandler responseHandler;

    public DefaultCheqiApiClient(CheqiSDKConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        // Register JDK8 module to handle Optional fields properly
        this.objectMapper.registerModule(new Jdk8Module());
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);

        this.httpClient = createHttpClient();
        this.retryHandler = new RetryHandler(httpClient, config.getMaxRetries());
        this.responseHandler = new ResponseHandler(objectMapper);
        
        logger.info("CheqiApiClient initialized with endpoint: {}, maxRetries: {}, timeout: {}s", 
                config.getApiEndpoint(), config.getMaxRetries(), config.getTimeoutSeconds());
    }

    @Override
    public String generateReceiptTemplate(ReceiptTemplateRequest request, String accessToken) throws CheqiApiException {
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
            Request httpRequest = buildPostRequest(Endpoints.TEMPLATE_ENDPOINT, requestJson, accessToken, "application/xml");

            // Execute request with retry logic
            Response response = retryHandler.executeWithRetry(httpRequest, "generateReceiptTemplate");
            return responseHandler.handleStringResponse(response, "Template generation");

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
            Request httpRequest = buildJsonPostRequest(Endpoints.CUSTOMER_MATCH_ENDPOINT, requestJson, accessToken);

            // Execute request with retry logic
            Response response = retryHandler.executeWithRetry(httpRequest, "matchCustomer");
            return responseHandler.handleJsonResponse(response, RecipientResolutionResponse.class, "Customer matching");

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

            // Build HTTP request
            Request httpRequest = buildJsonPostRequest(Endpoints.ENCRYPTED_RECEIPT_ENDPOINT, requestJson, accessToken);

            // Execute request with retry logic
            Response response = retryHandler.executeWithRetry(httpRequest, "sendEncryptedReceipts");
            responseHandler.handleVoidResponse(response, "Send encrypted receipts");

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

    @Override
    public void sendReceiptViaEmail(String customerEmail, PurchaseReceipt purchaseReceipt, String accessToken) throws CheqiApiException {
        logger.info("Sending receipt via email to: {}", customerEmail);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for sending receipt via email",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Customer email is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (purchaseReceipt == null) {
            throw new CheqiApiException(
                    "Purchase receipt is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            // Create request DTO with email and receipt
            var emailRequest = new EmailReceiptRequest(customerEmail, purchaseReceipt);
            
            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(emailRequest);
            logger.debug("Email receipt request JSON: {}", requestJson);

            // Build HTTP request
            Request httpRequest = buildJsonPostRequest(Endpoints.EMAIL_RECEIPT_ENDPOINT, requestJson, accessToken);

            // Execute request with retry logic
            Response response = retryHandler.executeWithRetry(httpRequest, "sendReceiptViaEmail");
            responseHandler.handleVoidResponse(response, "Send receipt via email");

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during email receipt submission", e);
            throw new CheqiApiException(
                    "Network error during email receipt submission: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during email receipt submission", e);
            throw new CheqiApiException(
                    "Email receipt submission failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }


    /**
     * Builds a POST request with common headers.
     */
    private Request buildPostRequest(Endpoints endpoint, String requestBody, String accessToken, String acceptHeader) {
        RequestBody body = RequestBody.create(requestBody, JSON);
        return new Request.Builder()
                .url(config.getApiEndpoint() + endpoint.getPath())
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", acceptHeader)
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    /**
     * Builds a POST request with JSON accept header.
     */
    private Request buildJsonPostRequest(Endpoints endpoint, String requestBody, String accessToken) {
        return buildPostRequest(endpoint, requestBody, accessToken, "application/json");
    }

    /**
     * Creates configured OkHttpClient with timeouts, connection pooling, and interceptors.
     */
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(false) // We handle retries manually with backoff
                .addInterceptor(new RequestLoggingInterceptor())
                .build();
    }
}
