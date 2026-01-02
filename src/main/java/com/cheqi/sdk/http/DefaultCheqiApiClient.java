package com.cheqi.sdk.http;

import com.cheqi.commons.DTOs.*;
import com.cheqi.commons.UBL.PurchaseReceipt;
import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.creditNote.CreditNoteTemplateRequest;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.IdentificationDetails;
import com.cheqi.sdk.models.ReceiptTemplateRequest;
import com.cheqi.sdk.models.company.CreateStoreRequest;
import com.cheqi.sdk.models.company.ProvisionCompanyRequest;
import com.cheqi.sdk.models.company.ProvisionCompanyResponse;
import com.cheqi.sdk.models.company.Store;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        logger.info("Generating receipt template for receipt ID: {}", request.getDocumentNumber());

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
    public String generateReceiptTemplate(ReceiptTemplateRequest request, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Generating receipt template with API credentials for receipt ID: {}", request.getDocumentNumber());

        validateApiCredentials(clientId, clientSecret);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Template request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.TEMPLATE_ENDPOINT, requestJson, clientId, clientSecret);
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
    public String generateCreditNoteTemplate(CreditNoteTemplateRequest request, String accessToken) throws CheqiApiException {
        logger.info("Generating credit note template for credit note ID: {}", request.getDocumentNumber());

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for template generation",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Credit note template request JSON: {}", requestJson);

            Request httpRequest = buildPostRequest(Endpoints.CREDIT_NOTE_TEMPLATE_ENDPOINT, requestJson, accessToken, "application/json");
            Response response = retryHandler.executeWithRetry(httpRequest, "generateCreditNoteTemplate");
            return responseHandler.handleStringResponse(response, "Credit note template generation");

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during credit note template generation", e);
            throw new CheqiApiException(
                    "Network error during credit note template generation: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during credit note template generation", e);
            throw new CheqiApiException(
                    "Credit note template generation failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public String generateCreditNoteTemplate(CreditNoteTemplateRequest request, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Generating credit note template with API credentials for credit note ID: {}", request.getDocumentNumber());

        validateApiCredentials(clientId, clientSecret);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Credit note template request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.CREDIT_NOTE_TEMPLATE_ENDPOINT, requestJson, clientId, clientSecret);
            Response response = retryHandler.executeWithRetry(httpRequest, "generateCreditNoteTemplate");
            return responseHandler.handleStringResponse(response, "Credit note template generation");

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during credit note template generation", e);
            throw new CheqiApiException(
                    "Network error during credit note template generation: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during credit note template generation", e);
            throw new CheqiApiException(
                    "Credit note template generation failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public RecipientResolutionResponse matchCustomer(IdentificationDetails request, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Matching customer with API credentials");

        validateApiCredentials(clientId, clientSecret);

        if (request == null) {
            throw new CheqiApiException(
                    "RecipientResolutionRequest is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            logger.info("Customer match request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.CUSTOMER_MATCH_ENDPOINT, requestJson, clientId, clientSecret);
            Response response = retryHandler.executeWithRetry(httpRequest, "matchCustomer");

            RecipientResolutionResponse result = responseHandler.handleJsonResponse(response, RecipientResolutionResponse.class, "Customer matching");
            logger.info("Customer match successful: customerFound={}", result.isCustomerFound());
            return result;
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
    public RecipientResolutionResponse matchCustomer(IdentificationDetails request, String accessToken) throws CheqiApiException {
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

            RecipientResolutionResponse result = responseHandler.handleJsonResponse(response, RecipientResolutionResponse.class, "Customer matching");

            logger.info("Customer match successful: customerFound={}", result.isCustomerFound());
            return result;
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
    public void sendEncryptedReceipts(Set<EncryptedReceiptDto> encryptedReceipts, String templateHash, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Sending {} encrypted receipts with API credentials", encryptedReceipts.size());

        validateApiCredentials(clientId, clientSecret);

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
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Encrypted receipts request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.ENCRYPTED_RECEIPT_ENDPOINT, requestJson, clientId, clientSecret);
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
    public void sendEncryptedCreditNotes(Set<EncryptedCreditNoteDto> encryptedCreditNotes, String templateHash, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Sending {} encrypted receipts with API credentials", encryptedCreditNotes.size());

        validateApiCredentials(clientId, clientSecret);

        if (encryptedCreditNotes == null || encryptedCreditNotes.isEmpty()) {
            throw new CheqiApiException(
                    "Encrypted receipts set cannot be null or empty",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            EncryptedCreditNotesRequest request = new EncryptedCreditNotesRequest(encryptedCreditNotes, templateHash);
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Encrypted receipts request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.ENCRYPTED_RECEIPT_ENDPOINT, requestJson, clientId, clientSecret);
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
    public void sendEncryptedCreditNotes(Set<EncryptedCreditNoteDto> encryptedCreditNotes, String templateHash, String accessToken) throws CheqiApiException {
        logger.info("Sending {} encrypted credit notes for customer: ", encryptedCreditNotes.size());

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required for sending encrypted credit notes",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (encryptedCreditNotes == null || encryptedCreditNotes.isEmpty()) {
            throw new CheqiApiException(
                    "Encrypted credit notes set cannot be null or empty",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            EncryptedCreditNotesRequest request = new EncryptedCreditNotesRequest(encryptedCreditNotes, templateHash);
            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Encrypted credit notes request JSON: {}", requestJson);

            // Build HTTP request
            Request httpRequest = buildJsonPostRequest(Endpoints.ENCRYPTED_RECEIPT_ENDPOINT, requestJson, accessToken);

            // Execute request with retry logic
            Response response = retryHandler.executeWithRetry(httpRequest, "sendEncryptedCreditNotes");
            responseHandler.handleVoidResponse(response, "Send encrypted credit notes");

        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during encrypted credit notes submission", e);
            throw new CheqiApiException(
                    "Network error during encrypted credit notes submission: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during encrypted credit notes submission", e);
            throw new CheqiApiException(
                    "Encrypted credit notes submission failed due to unexpected error: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    private void validateApiCredentials(String clientId, String clientSecret) throws CheqiApiException {
        if (clientId == null || clientId.trim().isEmpty() || clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Client ID and client secret are required for API authentication",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
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

    @Override
    public void sendReceiptViaEmail(String customerEmail, PurchaseReceipt purchaseReceipt, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Sending receipt via email to: {}", customerEmail);

        validateApiCredentials(clientId, clientSecret);

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
            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.EMAIL_RECEIPT_ENDPOINT, requestJson, clientId, clientSecret);

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

    private Request buildPostRequestWithBasicAuth(Endpoints endpoint, String requestBody, String clientId, String clientSecret) {
        // Encode clientId:clientSecret in Base64 for Basic Auth
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        
        RequestBody body = RequestBody.create(requestBody, JSON);
        return new Request.Builder()
                .url(config.getApiEndpoint() + endpoint.getPath())
                .post(body)
                .addHeader("Authorization", basicAuth)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
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

    @Override
    public ProvisionCompanyResponse provisionCompany(ProvisionCompanyRequest request, String clientId, String clientSecret) throws CheqiApiException {
        logger.info("Provisioning company: {}", request.getCompany().getCompanyName());

        if (clientId.trim().isEmpty() || clientSecret.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Client application token is required for company provisioning",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            logger.debug("Provision company request JSON: {}", requestJson);

            Request httpRequest = buildPostRequestWithBasicAuth(Endpoints.COMPANY_PROVISION_ENDPOINT, requestJson, clientId, clientSecret);
            Response response = retryHandler.executeWithRetry(httpRequest, "provisionCompany");

            return responseHandler.handleJsonResponse(response, ProvisionCompanyResponse.class, "Company provisioning");
        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Network error during company provisioning", e);
            throw new CheqiApiException(
                    "Network error during company provisioning: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.NETWORK_ERROR,
                    null
            );
        } catch (Exception e) {
            logger.error("Unexpected error during company provisioning", e);
            throw new CheqiApiException(
                    "Company provisioning failed: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public Store createStore(UUID companyId, CreateStoreRequest request, String accessToken) throws CheqiApiException {
        logger.info("Creating store for company: {}", companyId);
        validateAccessToken(accessToken);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId);

            Request httpRequest = buildJsonPostRequest(url, requestJson, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "createStore");

            return responseHandler.handleJsonResponse(response, Store.class, "Create store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create store", e);
            throw new CheqiApiException("Failed to create store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public List<Store> getStores(UUID companyId, Boolean activeOnly, String accessToken) throws CheqiApiException {
        logger.debug("Getting stores for company: {}", companyId);
        validateAccessToken(accessToken);

        try {
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId);
            if (activeOnly != null && activeOnly) {
                url += "?active=true";
            }

            Request httpRequest = buildGetRequest(url, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "getStores");

            return responseHandler.handleJsonListResponse(response, Store.class, "Get stores");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get stores", e);
            throw new CheqiApiException("Failed to get stores: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public Store getStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException {
        logger.debug("Getting store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId);

            Request httpRequest = buildGetRequest(url, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "getStore");

            return responseHandler.handleJsonResponse(response, Store.class, "Get store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get store", e);
            throw new CheqiApiException("Failed to get store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public Store updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, String accessToken) throws CheqiApiException {
        logger.info("Updating store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId);

            Request httpRequest = buildPutRequest(url, requestJson, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "updateStore");

            return responseHandler.handleJsonResponse(response, Store.class, "Update store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update store", e);
            throw new CheqiApiException("Failed to update store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void deleteStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException {
        logger.info("Deleting store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId);

            Request httpRequest = buildDeleteRequest(url, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "deleteStore");

            responseHandler.handleVoidResponse(response, "Delete store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete store", e);
            throw new CheqiApiException("Failed to delete store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void activateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException {
        logger.info("Activating store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORE_ACTIVATE_ENDPOINT.getPath(companyId, storeId);

            Request httpRequest = buildPatchRequest(url, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "activateStore");

            responseHandler.handleVoidResponse(response, "Activate store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to activate store", e);
            throw new CheqiApiException("Failed to activate store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void deactivateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException {
        logger.info("Deactivating store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String url = config.getApiEndpoint() + Endpoints.COMPANY_STORE_DEACTIVATE_ENDPOINT.getPath(companyId, storeId);

            Request httpRequest = buildPatchRequest(url, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "deactivateStore");

            responseHandler.handleVoidResponse(response, "Deactivate store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to deactivate store", e);
            throw new CheqiApiException("Failed to deactivate store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    private void validateAccessToken(String accessToken) throws CheqiApiException {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Access token is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }
    }

    private Request buildGetRequest(String url, String accessToken) {
        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    private Request buildJsonPostRequest(String url, String requestBody, String accessToken) {
        RequestBody body = RequestBody.create(requestBody, JSON);
        return new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    private Request buildPutRequest(String url, String requestBody, String accessToken) {
        RequestBody body = RequestBody.create(requestBody, JSON);
        return new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    private Request buildDeleteRequest(String url, String accessToken) {
        return new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    private Request buildPatchRequest(String url, String accessToken) {
        RequestBody body = RequestBody.create("", JSON);
        return new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }
}
