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
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultCheqiApiClient implements CheqiApiClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCheqiApiClient.class);

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String USER_AGENT = "CheqiSDK/3.0.0";

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
    public OAuthTokenExchangeResponse requestAccessToken(ClientCredentialsTokenRequest request, RequestOptions options)
            throws CheqiApiException {
        if (request == null || isBlank(request.getClientId()) || isBlank(request.getClientSecret())
                || request.getGrantId() == null) {
            throw new CheqiApiException("Token request requires clientId, clientSecret and grantId", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        if (request.getGrantType() == null) {
            request.setGrantType(ClientCredentialsTokenRequest.GrantTypeEnum.CLIENT_CREDENTIALS);
        }
        options = RequestOptions.orDefaults(options);
        try {
            // Client credentials travel in the body; this call carries no bearer credential.
            Request httpRequest = new Request.Builder()
                    .url(buildUrl(Endpoints.OAUTH_TOKEN_ENDPOINT.getPath()))
                    .post(RequestBody.create(objectMapper.writeValueAsString(request), JSON))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", USER_AGENT)
                    .build();
            Response response = clientFor(options).newCall(httpRequest).execute();
            OAuthTokenExchangeResponse token = responseHandler.handleJsonResponse(
                    response, OAuthTokenExchangeResponse.class, "Access token request");
            if (token == null || isBlank(token.getAccessToken())) {
                throw new CheqiApiException("Token response did not include an access token", 0,
                        CheqiApiException.ErrorCodes.INVALID_RESPONSE, null);
            }
            return token;
        } catch (CheqiApiException exception) {
            throw exception;
        } catch (IOException exception) {
            throw networkError(exception);
        }
    }

    @Override
    public RecipientResolutionResponse matchCustomer(IdentificationDetails request, RequestOptions options)
            throws CheqiApiException {
        if (request == null) {
            throw new CheqiApiException("Identification details are required", 400,
                    CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        options = RequestOptions.orDefaults(options);
        try {
            String json = objectMapper.writeValueAsString(request);
            Request httpRequest = buildPostRequest(buildUrl(Endpoints.CUSTOMER_MATCH_ENDPOINT.getPath()), json, options);
            Response response = executeWithRetry(httpRequest, options, "apiRequest");
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
    public MatchStatusResponse getMatch(String matchId, RequestOptions options) throws CheqiApiException {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new CheqiApiException("matchId is required", 400, CheqiApiException.ErrorCodes.INVALID_REQUEST, null);
        }
        options = RequestOptions.orDefaults(options);
        String url = HttpUrl.get(buildUrl(Endpoints.MATCH_STATUS_ENDPOINT.getPath(""))).newBuilder()
                .addPathSegment(matchId).build().toString();
        Response response = executeWithRetry(buildGetRequest(url, options), options, "getMatch");
        return responseHandler.handleJsonResponse(response, MatchStatusResponse.class, "Match retrieval");
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
    public ReceiptSubmissionResponse submitEncryptedReceipt(EncryptedReceiptEnvelope request, RequestOptions options)
            throws CheqiApiException {
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
                RequestOptions.orDefaults(options),
                "submitEncryptedReceipt"
        );
    }

    @Override
    public ReceiptSubmissionResponse submitEncryptedCreditNote(EncryptedCreditNoteEnvelope request,
                                                               RequestOptions options) throws CheqiApiException {
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
                RequestOptions.orDefaults(options),
                "submitEncryptedCreditNote"
        );
    }

    private ReceiptSubmissionResponse postSubmission(
            Endpoints endpoint, Object request, RequestOptions options,
            String operation
    ) throws CheqiApiException {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            Request httpRequest = buildPostRequest(buildUrl(endpoint.getPath()), requestJson, options);
            // Submissions are never retried automatically; the integration owns that decision.
            Response response = clientFor(options).newCall(httpRequest).execute();
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
    public ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(
            ClientReceiptDownloadRequest request, RequestOptions options) throws CheqiApiException {
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
        options = RequestOptions.orDefaults(options);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            Request httpRequest = buildPostRequest(
                    buildUrl(Endpoints.CLIENT_RECEIPT_DOWNLOAD_ENDPOINT.getPath()), requestJson, options);
            Response response = clientFor(options).newCall(httpRequest).execute();
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
    public void sendReceiptViaEmail(String customerEmail, CheqiReceipt cheqiReceipt, RequestOptions options)
            throws CheqiApiException {
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
        options = RequestOptions.orDefaults(options);

        try {
            EmailReceiptRequest emailReceiptRequest = new EmailReceiptRequest();
            emailReceiptRequest.setCustomerEmail(customerEmail);
            emailReceiptRequest.setCheqiReceipt(cheqiReceipt);

            String requestJson = objectMapper.writeValueAsString(emailReceiptRequest);
            logger.debug("Serialized email receipt request");

            Request httpRequest = buildPostRequest(
                    buildUrl(Endpoints.EMAIL_RECEIPT_ENDPOINT.getPath()), requestJson, options);

            // Execute one request; the integration owns retry policy.
            Response response = clientFor(options).newCall(httpRequest).execute();
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

    /** Returns the shared client, or a derived one (same pool) when the call overrides the timeout. */
    private OkHttpClient clientFor(RequestOptions options) {
        Duration timeout = options.getTimeout();
        if (timeout == null) {
            return httpClient;
        }
        return httpClient.newBuilder()
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .build();
    }

    private Response executeWithRetry(Request request, RequestOptions options, String operation)
            throws CheqiApiException {
        int maxRetries = options.getMaxRetries() != null ? options.getMaxRetries() : config.getMaxRetries();
        return retryHandler.executeWithRetry(clientFor(options), request, operation, maxRetries);
    }

    @Override
    public StoreDTO createStore(UUID companyId, CreateStoreRequest request, RequestOptions options)
            throws CheqiApiException {
        logger.info("Creating store for company: {}", companyId);
        options = RequestOptions.orDefaults(options);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = buildUrl(Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId));

            Request httpRequest = buildPostRequest(url, requestJson, options);
            Response response = clientFor(options).newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Create store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create store", e);
            throw new CheqiApiException("Failed to create store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, Boolean resendPending,
                                               RequestOptions options) throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            String url = buildUrl(Endpoints.COMPANY_INVITE_EMPLOYEES_ENDPOINT.getPath(companyId));
            String requestJson = objectMapper.writeValueAsString(
                    new InviteEmployeeRequest().emails(new java.util.LinkedHashSet<>(emails)).resendPending(resendPending));
            Request request = buildPostRequest(url, requestJson, options);
            Response response = clientFor(options).newCall(request).execute();
            return responseHandler.handleJsonResponse(response, InviteEmployeesResponse.class, "Invite employees");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to invite employees: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public List<DestinationResponse> listReceiptDestinations(RequestOptions options) throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            Request request = buildGetRequest(buildUrl(Endpoints.RECEIPT_DESTINATIONS_ENDPOINT.getPath()), options);
            return responseHandler.handleJsonListResponse(clientFor(options).newCall(request).execute(), DestinationResponse.class, "List receipt destinations");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to list receipt destinations: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public RegisterResponse registerReceiptDestination(RegisterRequest destination, RequestOptions options)
            throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            String json = objectMapper.writeValueAsString(destination);
            Request request = buildPostRequest(buildUrl(Endpoints.RECEIPT_DESTINATIONS_ENDPOINT.getPath()), json, options);
            return responseHandler.handleJsonResponse(clientFor(options).newCall(request).execute(), RegisterResponse.class, "Register receipt destination");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to register receipt destination: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId, RequestOptions options)
            throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            Request request = buildGetRequest(buildUrl(Endpoints.RECEIPT_DESTINATION_QUEUE_ENDPOINT.getPath(destinationId)), options);
            return responseHandler.handleJsonListResponse(clientFor(options).newCall(request).execute(), WebhookReceiptEnvelope.class, "Get pending receipts");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to get pending receipts: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void acknowledgeReceipts(UUID destinationId, List<String> receiptIds, RequestOptions options)
            throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            String json = objectMapper.writeValueAsString(
                    new AcknowledgeRequest().receiptIds(new java.util.LinkedHashSet<>(receiptIds)));
            Request request = buildPostRequest(
                    buildUrl(Endpoints.RECEIPT_DESTINATION_ACKNOWLEDGE_ENDPOINT.getPath(destinationId)), json, options);
            responseHandler.handleVoidResponse(clientFor(options).newCall(request).execute(), "Acknowledge receipts");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to acknowledge receipts: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void deactivateReceiptDestination(UUID destinationId, RequestOptions options) throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            Request request = buildDeleteRequest(buildUrl(Endpoints.RECEIPT_DESTINATION_ENDPOINT.getPath(destinationId)), options);
            responseHandler.handleVoidResponse(clientFor(options).newCall(request).execute(), "Deactivate receipt destination");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to deactivate receipt destination: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public WebhookDTO updateWebhookSubscriptionUrl(UUID subscriptionId, String notificationUrl, RequestOptions options)
            throws CheqiApiException {
        options = RequestOptions.orDefaults(options);
        try {
            String json = objectMapper.writeValueAsString(new UpdateSubscriptionRequest().notificationUrl(notificationUrl));
            Request request = buildJsonPatchRequest(
                    buildUrl(Endpoints.WEBHOOK_SUBSCRIPTION_ENDPOINT.getPath(subscriptionId)), json, options);
            return responseHandler.handleJsonResponse(clientFor(options).newCall(request).execute(), WebhookDTO.class, "Update webhook subscription URL");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CheqiApiException("Failed to update webhook subscription URL: " + e.getMessage(), e, 0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public List<StoreDTO> getStores(UUID companyId, Boolean activeOnly, RequestOptions options)
            throws CheqiApiException {
        logger.debug("Getting stores for company: {}", companyId);
        options = RequestOptions.orDefaults(options);

        try {
            String url = buildUrl(Endpoints.COMPANY_STORES_ENDPOINT.getPath(companyId));
            if (activeOnly != null && activeOnly) {
                url += "?active=true";
            }

            Request httpRequest = buildGetRequest(url, options);
            Response response = clientFor(options).newCall(httpRequest).execute();

            return responseHandler.handleJsonListResponse(response, StoreDTO.class, "Get stores");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get stores", e);
            throw new CheqiApiException("Failed to get stores: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public StoreDTO getStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException {
        logger.debug("Getting store {} for company {}", storeId, companyId);
        options = RequestOptions.orDefaults(options);

        try {
            String url = buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildGetRequest(url, options);
            Response response = clientFor(options).newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Get store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to get store", e);
            throw new CheqiApiException("Failed to get store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, RequestOptions options)
            throws CheqiApiException {
        logger.info("Updating store {} for company {}", storeId, companyId);
        options = RequestOptions.orDefaults(options);

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String url = buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId));

            Request httpRequest = buildPutRequest(url, requestJson, options);
            Response response = clientFor(options).newCall(httpRequest).execute();

            return responseHandler.handleJsonResponse(response, StoreDTO.class, "Update store");
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update store", e);
            throw new CheqiApiException("Failed to update store: " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    @Override
    public void deleteStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException {
        logger.info("Deleting store {} for company {}", storeId, companyId);
        storeVoidOperation(buildDeleteRequest(
                buildUrl(Endpoints.COMPANY_STORE_ENDPOINT.getPath(companyId, storeId)),
                RequestOptions.orDefaults(options)), options, "Delete store", "Failed to delete store");
    }

    @Override
    public void activateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException {
        logger.info("Activating store {} for company {}", storeId, companyId);
        storeVoidOperation(buildPatchRequest(
                buildUrl(Endpoints.COMPANY_STORE_ACTIVATE_ENDPOINT.getPath(companyId, storeId)),
                RequestOptions.orDefaults(options)), options, "Activate store", "Failed to activate store");
    }

    @Override
    public void deactivateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException {
        logger.info("Deactivating store {} for company {}", storeId, companyId);
        storeVoidOperation(buildPatchRequest(
                buildUrl(Endpoints.COMPANY_STORE_DEACTIVATE_ENDPOINT.getPath(companyId, storeId)),
                RequestOptions.orDefaults(options)), options, "Deactivate store", "Failed to deactivate store");
    }

    private void storeVoidOperation(Request httpRequest, RequestOptions options, String operation, String failure)
            throws CheqiApiException {
        try {
            Response response = clientFor(RequestOptions.orDefaults(options)).newCall(httpRequest).execute();
            responseHandler.handleVoidResponse(response, operation);
        } catch (CheqiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error(failure, e);
            throw new CheqiApiException(failure + ": " + e.getMessage(), e, 0, CheqiApiException.ErrorCodes.UNKNOWN_ERROR, null);
        }
    }

    /**
     * Resolves the bearer credential for a call: the per-call access token when present,
     * otherwise the API key configured on the SDK.
     */
    private String credential(RequestOptions options) throws CheqiApiException {
        String token = options.getAccessToken() != null ? options.getAccessToken() : config.getApiKey();
        if (isBlank(token)) {
            throw new CheqiApiException(
                    "No credentials for this call: pass RequestOptions.withAccessToken(...) or configure apiKey",
                    0,
                    CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED,
                    null
            );
        }
        return token;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Request.Builder authorized(String url, RequestOptions options) throws CheqiApiException {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + credential(options))
                .addHeader("User-Agent", USER_AGENT);
    }

    private Request buildGetRequest(String url, RequestOptions options) throws CheqiApiException {
        return authorized(url, options)
                .get()
                .addHeader("Accept", "application/json")
                .build();
    }

    private Request buildPostRequest(String url, String requestBody, RequestOptions options) throws CheqiApiException {
        return authorized(url, options)
                .post(RequestBody.create(requestBody, JSON))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    private Request buildPutRequest(String url, String requestBody, RequestOptions options) throws CheqiApiException {
        return authorized(url, options)
                .put(RequestBody.create(requestBody, JSON))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    private Request buildDeleteRequest(String url, RequestOptions options) throws CheqiApiException {
        return authorized(url, options)
                .delete()
                .build();
    }

    private Request buildPatchRequest(String url, RequestOptions options) throws CheqiApiException {
        return authorized(url, options)
                .patch(RequestBody.create("", JSON))
                .build();
    }

    private Request buildJsonPatchRequest(String url, String requestBody, RequestOptions options)
            throws CheqiApiException {
        return authorized(url, options)
                .patch(RequestBody.create(requestBody, JSON))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }
}
