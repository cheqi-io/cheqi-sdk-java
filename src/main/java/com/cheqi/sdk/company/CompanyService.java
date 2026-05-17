package com.cheqi.sdk.company;

import com.cheqi.sdk.http.CheqiApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Service for company operations.
 */
public class CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CheqiApiClient apiClient;

    public CompanyService(CheqiApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        logger.info("CompanyService initialized successfully");
    }
}
