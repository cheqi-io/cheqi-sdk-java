package com.cheqi.sdk.http;

import com.cheqi.sdk.config.CheqiSDKConfig;
import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultCheqiApiClient implements CheqiApiClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCheqiApiClient.class);

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String USER_AGENT = "CheqiSDK/2.6.0";

    private final CheqiSDKConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RetryHandler retryHandler;
    private final ResponseHandler responseHandler;

    public DefaultCheqiApiClient(CheqiSDKConfig config) {
        this.config = config;
        this.objectMapper = ObjectMapperConfig.getInstance();

        this.httpClient = createHttpClient(config);
        this.retryHandler = new RetryHandler(httpClient, config.getMaxRetries());
        this.responseHandler = new ResponseHandler(objectMapper);
        
        logger.info("CheqiApiClient initialized with endpoint: {}, timeout: {}s",
                config.getApiEndpoint(), config.getTimeoutSeconds());
    }


    @Override
    public RecipientResolutionResponse matchCustomer(IdentificationDetails request) throws CheqiApiException {
        return matchCustomerInternal(request, null);
    }

    @Override
    public RecipientResolutionResponse matchCustomer(IdentificationDetails request, String accessToken)
            throws CheqiApiException {
        validateAccessToken(accessToken);
        return matchCustomerInternal(request, accessToken);
    }

    private RecipientResolutionResponse matchCustomerInternal(IdentificationDetails request,
                                                               String accessToken)
            throws CheqiApiException {
        if (request == null) {
            throw new CheqiApiException("Identification details are required", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        try {
            String json = objectMapper.writeValueAsString(request);
            Request httpRequest = accessToken == null
                    ? buildPostRequestWithApiKey(Endpoints.CUSTOMER_MATCH_ENDPOINT, json)
                    : buildJsonPostRequest(Endpoints.CUSTOMER_MATCH_ENDPOINT, json, accessToken);
            Response response = retryHandler.executeWithRetry(httpRequest, "apiRequest");
            return responseHandler.handleJsonResponse(response, RecipientResolutionResponse.class, "Customer matching");
        } catch (CheqiApiException exception) {
            throw exception;
        } catch (IOException exception) {
            throw networkError(exception);
        } catch (Exception exception) {
            throw new CheqiApiException("Customer matching failed", exception, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public MatchStatusResponse getMatch(String matchId) throws CheqiApiException {
        return getMatchInternal(matchId, null);
    }

    @Override
    public MatchStatusResponse getMatch(String matchId, String accessToken) throws CheqiApiException {
        validateAccessToken(accessToken);
        return getMatchInternal(matchId, accessToken);
    }

    private MatchStatusResponse getMatchInternal(String matchId, String accessToken)
            throws CheqiApiException {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new CheqiApiException("matchId is required", 400, CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        String credential = accessToken == null ? config.getApiKey() : accessToken;
        validateAccessToken(credential);
        try {
            String url = HttpUrl.get(buildUrl(Endpoints.MATCH_STATUS_ENDPOINT.getPath(""))).newBuilder()
                    .addPathSegment(matchId).build().toString();
            Response response = retryHandler.executeWithRetry(buildGetRequest(url, credential), "getMatch");
            return responseHandler.handleJsonResponse(response, MatchStatusResponse.class, "Match retrieval");
        } catch (CheqiApiException exception) {
            throw exception;
        }
    }

    private static CheqiApiException networkError(IOException cause) {
        if (cause instanceof com.fasterxml.jackson.core.JsonProcessingException) {
            return new CheqiApiException("Invalid API data", cause, 0,
                    CheqiApiException.ErrorCodes.INVALID_RESPONSE, null);
        }
        return new CheqiApiException("Cheqi could not be reached", cause, 0,
                CheqiApiException.ErrorCodes.NETWORK_ERROR, null);
    }

    @Override
    public ReceiptSubmissionResponse submitEncryptedReceipt(EncryptedReceiptEnvelope request)
            throws CheqiApiException {
        return submitEncryptedReceiptInternal(request, null);
    }

    @Override
    public ReceiptSubmissionResponse submitEncryptedReceipt(
            EncryptedReceiptEnvelope request,
            String accessToken
    ) throws CheqiApiException {
        validateAccessToken(accessToken);
        return submitEncryptedReceiptInternal(request, accessToken);
    }

    private ReceiptSubmissionResponse submitEncryptedReceiptInternal(
            EncryptedReceiptEnvelope request, String accessToken
    ) throws CheqiApiException {
        if (request == null
                || request.getMatchId() == null
                || request.getMatchId().trim().isEmpty()
                || request.getDeviceDeliveries() == null
                || request.getDeviceDeliveries().isEmpty()) {
            throw new CheqiApiException(
                    "Encrypted receipt envelope requires matchId and deviceDeliveries",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }
        return postSubmission(
                Endpoints.ENCRYPTED_RECEIPT_ENDPOINT,
                request,
                accessToken,
                "submitEncryptedReceipt"
        );
    }

    @Override
    public ReceiptSubmissionResponse submitEncryptedCreditNote(EncryptedCreditNoteEnvelope request)
            throws CheqiApiException {
        return submitEncryptedCreditNoteInternal(request, null);
    }

    @Override
    public ReceiptSubmissionResponse submitEncryptedCreditNote(
            EncryptedCreditNoteEnvelope request,
            String accessToken
    ) throws CheqiApiException {
        validateAccessToken(accessToken);
        return submitEncryptedCreditNoteInternal(request, accessToken);
    }

    private ReceiptSubmissionResponse submitEncryptedCreditNoteInternal(
            EncryptedCreditNoteEnvelope request,
            String accessToken
    ) throws CheqiApiException {
        if (request == null
                || request.getMatchId() == null
                || request.getMatchId().trim().isEmpty()
                || request.getParentCheqiReceiptId() == null
                || request.getParentCheqiReceiptId().trim().isEmpty()
                || request.getDeviceDeliveries() == null
                || request.getDeviceDeliveries().isEmpty()) {
            throw new CheqiApiException(
                    "Encrypted credit-note envelope requires matchId, parentCheqiReceiptId, "
                            + "and deviceDeliveries",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }
        return postSubmission(
                Endpoints.ENCRYPTED_CREDIT_NOTE_ENDPOINT,
                request,
                accessToken,
                "submitEncryptedCreditNote"
        );
    }

    private ReceiptSubmissionResponse postSubmission(
            Endpoints endpoint, Object request, String accessToken,
            String operation
    ) throws CheqiApiException {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            Request httpRequest = accessToken == null
                    ? buildPostRequestWithApiKey(endpoint, requestJson)
                    : buildJsonPostRequest(endpoint, requestJson, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();
            String json = responseHandler.handleStringResponse(response, operation);
            try {
                com.fasterxml.jackson.databind.JsonNode body = objectMapper.readTree(json);
                if (body == null || !body.isObject()) {
                    throw new CheqiApiException("Invalid receipt submission response", response.code(),
                            CheqiApiException.ErrorCodes.INVALID_RESPONSE, response.header("X-Correlation-ID"));
                }
                if ("IN_PROGRESS".equals(body.path("state").asText())) {
                    throw new com.cheqi.sdk.http.exceptions.SubmissionInProgressException(
                            Math.max(1, body.path("retryAfterSeconds").asInt(2)));
                }
                ReceiptSubmissionResponse submitted = objectMapper.treeToValue(body, ReceiptSubmissionResponse.class);
                if (submitted == null || submitted.getCheqiReceiptId() == null
                        || submitted.getCheqiReceiptId().trim().isEmpty()
                        || submitted.getMatchId() == null || submitted.getMatchId().trim().isEmpty()) {
                    throw new CheqiApiException("Invalid receipt submission response", 0,
                            CheqiApiException.ErrorCodes.INVALID_RESPONSE, null);
                }
                return submitted;
            } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException exception) {
                throw new CheqiApiException("Invalid receipt submission response", exception, response.code(),
                        CheqiApiException.ErrorCodes.INVALID_RESPONSE, response.header("X-Correlation-ID"));
            }
        } catch (CheqiApiException exception) {
            throw exception;
        } catch (IOException exception) {
            throw networkError(exception);
        } catch (Exception exception) {
            throw new CheqiApiException(
                    "Encrypted submission failed: " + exception.getMessage(),
                    exception,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    @Override
    public ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(ClientReceiptDownloadRequest request) throws CheqiApiException {
        return uploadEncryptedDownloadReceiptInternal(request, null);
    }

    @Override
    public ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(
            ClientReceiptDownloadRequest request, String accessToken) throws CheqiApiException {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiApiException("Access token is required for receipt download upload", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        return uploadEncryptedDownloadReceiptInternal(request, accessToken);
    }

    private ClientReceiptDownloadResponse uploadEncryptedDownloadReceiptInternal(
            ClientReceiptDownloadRequest request, String accessToken) throws CheqiApiException {
        if (request == null
                || request.getDownloadId() == null
                || !request.getDownloadId().matches("[A-Za-z0-9_-]{22,64}")) {
            throw new CheqiApiException("downloadId must be 22-64 base64url characters", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        if (request.getCiphertext() == null || request.getCiphertext().trim().isEmpty()) {
            throw new CheqiApiException("ciphertext is required", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            Request httpRequest = accessToken == null
                    ? buildPostRequestWithApiKey(Endpoints.CLIENT_RECEIPT_DOWNLOAD_ENDPOINT, requestJson)
                    : buildJsonPostRequest(Endpoints.CLIENT_RECEIPT_DOWNLOAD_ENDPOINT, requestJson, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();
            return responseHandler.handleJsonResponse(
                    response, ClientReceiptDownloadResponse.class, "Upload client-encrypted receipt");
        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            throw new CheqiApiException("Network error during client-encrypted receipt upload: " + e.getMessage(),
                    e, 0, CheqiApiException.ErrorCodes.NETWORK_ERROR, null);
        } catch (Exception e) {
            throw new CheqiApiException("Client-encrypted receipt upload failed: " + e.getMessage(),
                    e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }


    @Override
    public void sendReceiptViaEmail(String customerEmail, CheqiReceipt cheqiReceipt) throws CheqiApiException {
        logger.info("Sending receipt via email to: {}", customerEmail);

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Customer email is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (cheqiReceipt == null) {
            throw new CheqiApiException(
                    "Purchase receipt is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            // Create request DTO with email and receipt
            EmailReceiptRequest emailReceiptRequest = new EmailReceiptRequest();
            emailReceiptRequest.setCustomerEmail(customerEmail);
            emailReceiptRequest.setCheqiReceipt(cheqiReceipt);

            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(emailReceiptRequest);
            logger.debug("Serialized email receipt request");

            // Build HTTP request
            Request httpRequest = buildPostRequestWithApiKey(Endpoints.EMAIL_RECEIPT_ENDPOINT, requestJson);

            // Execute using the existing HTTP retry policy.
            Response response = httpClient.newCall(httpRequest).execute();
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
    public void sendReceiptViaEmail(String customerEmail, CheqiReceipt cheqiReceipt, String accessToken) throws CheqiApiException {
        logger.info("Sending receipt via email to: {}", customerEmail);

        validateAccessToken(accessToken);

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new CheqiApiException(
                    "Customer email is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        if (cheqiReceipt == null) {
            throw new CheqiApiException(
                    "Purchase receipt is required",
                    400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST,
                    null
            );
        }

        try {
            // Create request DTO with email and receipt
            EmailReceiptRequest emailReceiptRequest = new EmailReceiptRequest();
            emailReceiptRequest.setCustomerEmail(customerEmail);
            emailReceiptRequest.setCheqiReceipt(cheqiReceipt);

            // Serialize request to JSON
            String requestJson = objectMapper.writeValueAsString(emailReceiptRequest);
            logger.debug("Serialized email receipt request");

            // Build HTTP request
            Request httpRequest = buildJsonPostRequest(Endpoints.EMAIL_RECEIPT_ENDPOINT, requestJson, accessToken);

            // Execute one request; the integration owns retry policy.
            Response response = httpClient.newCall(httpRequest).execute();
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
                .url(buildUrl(endpoint.getPath()))
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", acceptHeader)
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }

    /**
     * Builds a POST request with API Key authentication (Bearer token).
     * Uses the API key from config for direct company access.
     */
    private Request buildPostRequestWithApiKey(Endpoints endpoint, String requestBody) {
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            throw new IllegalStateException("API key is not configured. Use .apiKey() when building SDK config.");
        }
        
        RequestBody body = RequestBody.create(requestBody, JSON);
        return new Request.Builder()
                .url(buildUrl(endpoint.getPath()))
                .post(body)
                .addHeader("Authorization", "Bearer " + config.getApiKey())
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

    private String buildUrl(String path) {
        String baseUrl = config.getApiEndpoint();
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    /**
     * Creates configured OkHttpClient with timeouts, connection pooling, and interceptors.
     */
    private OkHttpClient createHttpClient(CheqiSDKConfig config) {
        OkHttpClient.Builder builder = config.getHttpClient() != null
                ? config.getHttpClient().newBuilder()
                : new OkHttpClient.Builder();

        return builder
                .connectTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(config.getTimeoutSeconds(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(false) // We handle retries manually with backoff
                .addInterceptor(new RequestLoggingInterceptor())
                .build();
    }

    @Override
    public StoreDTO createStore(UUID companyId, CreateStoreRequest request, String accessToken) throws CheqiApiException {
        logger.info("Creating store for company: {}", companyId);
        validateAccessToken(accessToken);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = buildUrl(Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId));

            Request httpRequest = buildJsonPostRequest(url, requestJson, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Create store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create store", e);
            throw new CheqiApiException("Failed to create store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void inviteUsers(UUID companyId, List<String> emails, String accessToken) throws CheqiApiException {
        validateAccessToken(accessToken);
        try {
            String url = buildUrl(Endpoints.COMPANY_INVITE_EMPLOYEES_ENDPOINT.getPath(companyId));
            String requestJson = objectMapper.writeValueAsString(
                    new InviteEmployeeRequest().emails(new java.util.LinkedHashSet<>(emails)));
            Request request = buildJsonPostRequest(url, requestJson, accessToken);
            Response response = httpClient.newCall(request).execute();
            responseHandler.handleVoidResponse(response, "Invite employees");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to invite employees: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public List<StoreDTO> getStores(UUID companyId, Boolean activeOnly, String accessToken) throws CheqiApiException {
        logger.debug("Getting stores for company: {}", companyId);
        validateAccessToken(accessToken);

        try {
            String url = buildUrl(Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId));
            if (activeOnly != null && activeOnly) {
                url += "?active=true";
            }

            Request httpRequest = buildGetRequest(url, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

            return responseHandler.handleJsonListResponse(response, StoreDTO.class, "Get stores");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get stores", e);
            throw new CheqiApiException("Failed to get stores: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public StoreDTO getStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException {
        logger.debug("Getting store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String url = buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildGetRequest(url, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Get store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get store", e);
            throw new CheqiApiException("Failed to get store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, String accessToken) throws CheqiApiException {
        logger.info("Updating store {} for company {}", storeId, companyId);
        validateAccessToken(accessToken);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildPutRequest(url, requestJson, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Update store");
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
            String url = buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildDeleteRequest(url, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

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
            String url = buildUrl(Endpoints.COMPANY_STORE_ACTIVATE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildPatchRequest(url, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

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
            String url = buildUrl(Endpoints.COMPANY_STORE_DEACTIVATE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildPatchRequest(url, accessToken);
            Response response = httpClient.newCall(httpRequest).execute();

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
