package com.cheqi.sdk.config;

public enum Environment {
    SANDBOX("https://sandbox.api.cheqi.io"),
    TEST("https://test.api.cheqi.io"),
    PRODUCTION("https://api.cheqi.io");

    private final String baseUrl;

    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
