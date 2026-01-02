package com.cheqi.sdk.http;

import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


/**
 * Handles HTTP response processing including error handling and deserialization.
 */
public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private final ObjectMapper objectMapper;

    public ResponseHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Processes a response and returns the body as a string.
     */
    public String handleStringResponse(Response response, String operationName) throws CheqiApiException {
        try (ResponseBody responseBody = response.body()) {
            String responseContent = responseBody != null ? responseBody.string() : "";
            String correlationId = response.header("X-Correlation-ID");

            if (response.isSuccessful()) {
                logger.info("{} successful", operationName);

                if (responseContent.trim().isEmpty()) {
                    throw new CheqiApiException(
                            "Backend returned empty response",
                            response.code(),
                            CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                            correlationId
                    );
                }
                return responseContent;
            } else {
                throw buildApiException(response.code(), responseContent, correlationId, operationName);
            }
        } catch (IOException e) {
            throw new CheqiApiException(
                    "Failed to read response body: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    /**
     * Processes a response and deserializes to specified type.
     */
    public <T> T handleJsonResponse(Response response, Class<T> responseType, String operationName) throws CheqiApiException {
        try (ResponseBody responseBody = response.body()) {
            String responseJson = responseBody != null ? responseBody.string() : "";
            String correlationId = response.header("X-Correlation-ID");

            if (response.isSuccessful()) {
                logger.info("{} successful", operationName);

                if (responseJson.trim().isEmpty()) {
                    throw new CheqiApiException(
                            "Backend returned empty response",
                            response.code(),
                            CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                            correlationId
                    );
                }

                T result = objectMapper.readValue(responseJson, responseType);
                if (result == null) {
                    throw new CheqiApiException(
                            "Backend returned null response",
                            response.code(),
                            CheqiApiException.ErrorCodes.INVALID_RESPONSE,
                            correlationId
                    );
                }
                return result;
            } else {
                throw buildApiException(response.code(), responseJson, correlationId, operationName);
            }
        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            throw new CheqiApiException(
                    "Failed to parse response: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    /**
     * Handles void responses (201 Created, 204 No Content).
     */
    public void handleVoidResponse(Response response, String operationName) throws CheqiApiException {
        try (ResponseBody responseBody = response.body()) {
            String responseContent = responseBody != null ? responseBody.string() : "";
            String correlationId = response.header("X-Correlation-ID");

            if (response.isSuccessful()) {
                logger.info("{} successful", operationName);
                return;
            } else {
                throw buildApiException(response.code(), responseContent, correlationId, operationName);
            }
        } catch (IOException e) {
            throw new CheqiApiException(
                    "Failed to read response: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    public <T> List<T> handleJsonListResponse(Response response, Class<T> elementType, String operationName) throws CheqiApiException {
        try (ResponseBody responseBody = response.body()) {
            String responseJson = responseBody != null ? responseBody.string() : "";
            String correlationId = response.header("X-Correlation-ID");

            if (response.isSuccessful()) {
                logger.info("{} successful", operationName);

                if (responseJson.trim().isEmpty()) {
                    return List.of();
                }

                var listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
                return objectMapper.readValue(responseJson, listType);
            } else {
                throw buildApiException(response.code(), responseJson, correlationId, operationName);
            }
        } catch (CheqiApiException e) {
            throw e;
        } catch (IOException e) {
            throw new CheqiApiException(
                    "Failed to parse response: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }

    private CheqiApiException buildApiException(int statusCode, String responseContent, String correlationId, String operationName) {
        logger.error("{} failed. Status: {}, Response: {}", operationName, statusCode, responseContent);

        if (statusCode == 401) {
            return new CheqiApiException(
                    "Invalid or expired access token",
                    statusCode,
                    CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED,
                    correlationId
            );
        }

        if (statusCode == 403) {
            return new CheqiApiException(
                    "Access token does not have required permissions",
                    statusCode,
                    CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED,
                    correlationId
            );
        }

        String errorCode = determineErrorCode(statusCode);
        String errorMessage = parseErrorMessage(responseContent, operationName + " failed");

        return new CheqiApiException(errorMessage, statusCode, errorCode, correlationId);
    }

    private String determineErrorCode(int statusCode) {
        switch (statusCode) {
            case 400: return CheqiApiException.ErrorCodes.INVALID_REQUEST;
            case 401: return CheqiApiException.ErrorCodes.AUTHENTICATION_FAILED;
            case 403: return CheqiApiException.ErrorCodes.AUTHORIZATION_FAILED;
            case 408: return CheqiApiException.ErrorCodes.TIMEOUT_ERROR;
            case 429: return CheqiApiException.ErrorCodes.RATE_LIMIT_EXCEEDED;
            case 500:
            case 502:
            case 503:
            case 504: return CheqiApiException.ErrorCodes.SERVER_ERROR;
            default: return CheqiApiException.ErrorCodes.UNKNOWN_ERROR;
        }
    }

    private String parseErrorMessage(String responseJson, String defaultMessage) {
        try {
            if (responseJson != null && !responseJson.trim().isEmpty()) {
                var errorResponse = objectMapper.readTree(responseJson);
                if (errorResponse.has("message")) return errorResponse.get("message").asText();
                if (errorResponse.has("error")) return errorResponse.get("error").asText();
                if (errorResponse.has("error_description")) return errorResponse.get("error_description").asText();
            }
        } catch (Exception e) {
            logger.debug("Failed to parse error message from response: {}", e.getMessage());
        }
        return defaultMessage;
    }
}
