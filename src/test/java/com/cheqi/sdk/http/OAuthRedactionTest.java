package com.cheqi.sdk.http;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.models.generated.*;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OAuthRedactionTest {
    @Test void diagnosticsRedactCredentialsWithoutChangingJson() throws Exception {
        Object[][] cases = {
                {new ClientCredentialsTokenRequest(), "ClientSecret", "client_secret"},
                {new ClientApplicationPublicKeyRequest(), "ClientSecret", "client_secret"},
                {new AuthorizationCodeExchangeRequest(), "ClientSecret", "client_secret"},
                {new AuthorizationCodeExchangeRequest(), "Code", "code"},
                {new AuthorizationCodeExchangeRequest(), "CodeVerifier", "code_verifier"},
                {new OAuthTokenExchangeResponse(), "AccessToken", "access_token"}
        };
        for (Object[] entry : cases) {
            Object model = entry[0];
            String secret = "dummy-secret\nsecond-line";
            model.getClass().getMethod("set" + entry[1], String.class).invoke(model, secret);
            assertFalse(model.toString().contains("dummy-secret"));
            assertFalse(model.toString().contains("second-line"));
            var json = ObjectMapperConfig.getInstance().valueToTree(model);
            assertEquals(secret, json.path((String) entry[2]).asText());
        }
    }

    @Test void tokenErrorsDoNotExposeResponseBodiesOrParserCauses() {
        for (int status : new int[]{200, 400}) {
            String json = status == 400 ? "{\"message\":\"dummy-echoed-secret\"}"
                    : "{\"access_token\":\"dummy-echoed-secret\",\"expires_in\":\"dummy-echoed-secret\"}";
            var response = new Response.Builder().request(new Request.Builder().url("https://example.com").build())
                    .protocol(Protocol.HTTP_1_1).code(status).message("test").header("X-Correlation-ID", "test-correlation")
                    .body(ResponseBody.create(json, MediaType.get("application/json"))).build();
            var error = assertThrows(CheqiApiException.class, () -> new ResponseHandler(ObjectMapperConfig.getInstance())
                    .handleJsonResponse(response, OAuthTokenExchangeResponse.class, "Access token request"));
            assertFalse(error.toString().contains("dummy-echoed-secret"));
            assertNull(error.getCause());
            assertEquals(status, error.getHttpStatusCode());
            assertEquals("test-correlation", error.getCorrelationId());
        }
    }
}
