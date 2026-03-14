package com.cheqi.sdk.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheqiSDKConfigTest {

    @Test
    void validConfigBuildsSuccessfully() {
        CheqiSDKConfig config = CheqiSDKConfig.builder()
                .apiEndpoint(Environment.SANDBOX)
                .apiKey("sk_test_abc123")
                .build();

        assertEquals("https://sandbox.api.cheqi.io", config.getApiEndpoint());
        assertEquals("sk_test_abc123", config.getApiKey());
    }

    @Test
    void missingEndpointThrows() {
        assertThrows(IllegalStateException.class, () ->
                CheqiSDKConfig.builder()
                        .apiKey("sk_test_abc123")
                        .build()
        );
    }

    @Test
    void negativeTimeoutThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CheqiSDKConfig.builder()
                        .apiEndpoint(Environment.SANDBOX)
                        .timeoutSeconds(-1)
                        .build()
        );
    }

    @Test
    void zeroTimeoutThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CheqiSDKConfig.builder()
                        .apiEndpoint(Environment.SANDBOX)
                        .timeoutSeconds(0)
                        .build()
        );
    }

    @Test
    void negativeRetriesThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CheqiSDKConfig.builder()
                        .apiEndpoint(Environment.SANDBOX)
                        .maxRetries(-1)
                        .build()
        );
    }

    @Test
    void defaultTimeoutIs30() {
        CheqiSDKConfig config = CheqiSDKConfig.builder()
                .apiEndpoint(Environment.PRODUCTION)
                .build();

        assertEquals(30, config.getTimeoutSeconds());
    }

    @Test
    void defaultMaxRetriesIs3() {
        CheqiSDKConfig config = CheqiSDKConfig.builder()
                .apiEndpoint(Environment.PRODUCTION)
                .build();

        assertEquals(3, config.getMaxRetries());
    }

    @Test
    void customEndpointWorks() {
        CheqiSDKConfig config = CheqiSDKConfig.builder()
                .customApiEndpoint("https://custom.api.example.com")
                .build();

        assertEquals("https://custom.api.example.com", config.getApiEndpoint());
    }

    @Test
    void encryptionConfigDefaultsWhenNotSet() {
        CheqiSDKConfig config = CheqiSDKConfig.builder()
                .apiEndpoint(Environment.SANDBOX)
                .build();

        assertNotNull(config.getEncryptionConfig());
    }

}
