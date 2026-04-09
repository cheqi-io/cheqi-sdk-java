package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.models.company.Company;
import com.cheqi.sdk.models.generated.CompanyCreationRequest;
import com.cheqi.sdk.models.generated.CompanyDTO;
import com.cheqi.sdk.models.generated.ProvisionCompanyRequest;
import com.cheqi.sdk.models.generated.ProvisionCompanyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Service for company provisioning operations.
 *
 * Enables trusted POS systems to provision companies and receive immediate API access
 * without waiting for merchant OAuth approval.
 *
 * <h3>Example usage:</h3>
 * <pre>
 * Company company = Company.builder()
 *     .companyName("My Store BV")
 *     .companyLegalName("My Store B.V.")
 *     .chamberOfCommerceNumber("12345678")
 *     .companyEmail("info@mystore.nl")
 *     .address(Address.builder()
 *         .streetName("Hoofdstraat 123")
 *         .cityName("Amsterdam")
 *         .postalZone("1012AB")
 *         .countryIsoCode("NL")
 *         .build())
 *     .build();
 *
 * ProvisionCompanyResponse response = sdk.getCompanyService()
 *     .provisionCompany(company, "owner@mystore.nl", clientApplicationToken);
 *
 * if (response.isProvisioned()) {
 *     String accessToken = response.getAccessToken();
 *     // Use accessToken to create stores and receipts
 * } else if (response.isAlreadyExists()) {
 *     // Company exists, initiate OAuth flow
 * }
 * </pre>
 */
public class CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CheqiApiClient apiClient;

    public CompanyService(CheqiApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        logger.info("CompanyService initialized successfully");
    }

    /**
     * Provisions a new company for a merchant using API key from SDK config.
     * Uses Bearer token authentication with the API key configured during SDK initialization.
     *
     * This method creates a new company in the Cheqi system and returns OAuth tokens
     * for immediate API access. Only available for partner-tier API keys.
     *
     * @param company Company details for provisioning
     * @param adminEmail Email address for the company admin (receives invitation)
     * @return ProvisionCompanyResponse containing companyId and OAuth tokens
     * @throws CheqiSDKException if provisioning fails or validation errors occur
     */
    public ProvisionCompanyResponse provisionCompany(
            CompanyCreationRequest company,
            String adminEmail
    ) throws CheqiSDKException {

        if (company == null) {
            throw new CheqiSDKException("Company cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new CheqiSDKException("Admin email is required",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!adminEmail.contains("@")) {
            throw new CheqiSDKException("Admin email must be a valid email address",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.info("Provisioning company: {}", company.getCompanyName());

        try {
            ProvisionCompanyRequest request = new ProvisionCompanyRequest();
            request.setCompany(company);
            request.setAdminEmail(adminEmail);
            ProvisionCompanyResponse response = apiClient.provisionCompany(request);

            if ("PROVISIONED".equals(response.getStatus())) {
                logger.info("Company provisioned successfully: companyId={}", response.getCompanyId());
            } else if ("ALREADY_EXISTS".equals(response.getStatus())) {
                logger.info("Company already exists: companyId={}, requiresOAuth=true", response.getCompanyId());
            }

            return response;
        } catch (Exception e) {
            logger.error("Failed to provision company: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to provision company: " + e.getMessage(), e);
        }
    }
}
