package com.cheqi.sdk.config;

/**
 * Predefined Cheqi API environments.
 * 
 * Use these for standard deployments, or use customApiEndpoint() for custom URLs.
 */
public enum Environment {
    /**
     * Sandbox environment for testing and development.
     */
    SANDBOX("https://sandbox.api.cheqi.io"),
    
    /**
     * Production environment for live operations.
     */
    PRODUCTION("https://api.cheqi.io");

    private final String baseUrl;

    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the base URL for this environment.
     * 
     * @return The API base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}
