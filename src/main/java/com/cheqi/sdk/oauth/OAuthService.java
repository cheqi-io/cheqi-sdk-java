package com.cheqi.sdk.oauth;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.RequestOptions;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.ClientCredentialsTokenRequest;
import com.cheqi.sdk.models.generated.OAuthTokenExchangeResponse;

import java.util.Objects;

/**
 * Obtains short-lived Company access tokens with the OAuth client-credentials grant.
 *
 * <p>Each token is bound to one Grant, so request one per connected company. The SDK does not
 * cache or refresh tokens: store each token with its {@code expires_in} in your own token store,
 * request a new one before it expires, and pass it per call with
 * {@link RequestOptions#withAccessToken(String)}.</p>
 *
 * <pre>
 * OAuthTokenExchangeResponse token = sdk.getOAuthService().requestAccessToken(
 *         new ClientCredentialsTokenRequest()
 *                 .clientId(clientId)
 *                 .clientSecret(clientSecret)
 *                 .grantId(grantId)
 *                 .scope("receipt:issue"));
 * </pre>
 */
public class OAuthService {
    private final CheqiApiClient apiClient;

    public OAuthService(CheqiApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
    }

    /**
     * Requests an access token for one Grant. {@code grantType} defaults to
     * {@code client_credentials} when not set.
     */
    public OAuthTokenExchangeResponse requestAccessToken(ClientCredentialsTokenRequest request)
            throws CheqiSDKException {
        return requestAccessToken(request, RequestOptions.defaults());
    }

    public OAuthTokenExchangeResponse requestAccessToken(ClientCredentialsTokenRequest request,
                                                        RequestOptions options) throws CheqiSDKException {
        try {
            return apiClient.requestAccessToken(request, options);
        } catch (CheqiApiException e) {
            throw new CheqiSDKException("Access token request failed: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
    }
}
