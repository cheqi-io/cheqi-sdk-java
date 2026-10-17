package com.cheqi.sdk.http;

import java.time.Duration;
import java.util.Objects;

/**
 * Per-call settings that override the SDK-level configuration for a single API call.
 *
 * <p>Pass an OAuth access token here when acting on behalf of a connected company. The SDK never
 * stores or refreshes tokens: obtain them with {@code OAuthService#requestAccessToken}, cache them
 * per Grant in your own store, and pass the current one on each call.</p>
 *
 * <pre>
 * RequestOptions options = RequestOptions.withAccessToken(tokenStore.currentToken(grantId));
 * sdk.getReceiptService().issueReceipt(identificationDetails, receiptPayload, options);
 * </pre>
 *
 * <p>Without an access token the call authenticates with the API key configured on the SDK.</p>
 */
public final class RequestOptions {
    private static final RequestOptions DEFAULTS = new Builder().build();

    private final String accessToken;
    private final Duration timeout;
    private final Integer maxRetries;

    private RequestOptions(Builder builder) {
        this.accessToken = builder.accessToken;
        this.timeout = builder.timeout;
        this.maxRetries = builder.maxRetries;
    }

    /** Options that use the SDK-level configuration for everything. */
    public static RequestOptions defaults() {
        return DEFAULTS;
    }

    /** Options that authenticate this call with the given OAuth access token. */
    public static RequestOptions withAccessToken(String accessToken) {
        return builder().accessToken(accessToken).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Returns {@code options}, or the defaults when it is {@code null}. */
    static RequestOptions orDefaults(RequestOptions options) {
        return options == null ? DEFAULTS : options;
    }

    /** OAuth access token for this call, or {@code null} to use the configured API key. */
    public String getAccessToken() {
        return accessToken;
    }

    /** Timeout for this call, or {@code null} to use the configured timeout. */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Maximum retries for this call, or {@code null} to use the configured value. Only applies to
     * operations the SDK retries; encrypted submissions are never retried automatically.
     */
    public Integer getMaxRetries() {
        return maxRetries;
    }

    public Builder toBuilder() {
        return new Builder().accessToken(accessToken).timeout(timeout).maxRetries(maxRetries);
    }

    public static final class Builder {
        private String accessToken;
        private Duration timeout;
        private Integer maxRetries;

        private Builder() {
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public RequestOptions build() {
            if (accessToken != null && accessToken.trim().isEmpty()) {
                throw new IllegalArgumentException("accessToken cannot be empty");
            }
            if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
                throw new IllegalArgumentException("timeout must be positive");
            }
            if (maxRetries != null && maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries cannot be negative");
            }
            return new RequestOptions(this);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RequestOptions)) return false;
        RequestOptions that = (RequestOptions) other;
        return Objects.equals(accessToken, that.accessToken)
                && Objects.equals(timeout, that.timeout)
                && Objects.equals(maxRetries, that.maxRetries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, timeout, maxRetries);
    }

    @Override
    public String toString() {
        // Never print the token itself.
        return "RequestOptions{accessToken=" + (accessToken == null ? "null" : "***")
                + ", timeout=" + timeout + ", maxRetries=" + maxRetries + "}";
    }
}
