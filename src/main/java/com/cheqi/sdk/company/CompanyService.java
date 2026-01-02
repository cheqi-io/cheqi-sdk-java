package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.models.company.Company;
import com.cheqi.sdk.models.company.ProvisionCompanyRequest;
import com.cheqi.sdk.models.company.ProvisionCompanyResponse;
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
     * Provisions a new company for a merchant.
     *
     * This method creates a new company in the Cheqi system and returns OAuth tokens
     * for immediate API access. Only available for ClientApplications with a partnerTier.
     *
     * @param company Company details for provisioning
     * @param adminEmail Email address for the company admin (receives invitation)
     * @param clientId clientId from client application
     * @param clientSecret clientSecret from client application
     * @return ProvisionCompanyResponse containing companyId and OAuth tokens
     * @throws CheqiSDKException if provisioning fails or validation errors occur
     */
    public ProvisionCompanyResponse provisionCompany(
            Company company,
            String adminEmail,
            String clientId,
            String clientSecret
    ) throws CheqiSDKException {

        if (company == null) {
            throw new CheqiSDKException("Company cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!company.isValid()) {
            throw new CheqiSDKException("Invalid company data: " + String.join(", ", company.getValidationErrors()),
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

        if (clientId == null || clientId.trim().isEmpty()) {
            throw new CheqiSDKException("Client application token is required",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new CheqiSDKException("Client application token is required",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }

        logger.info("Provisioning company: {}", company.getCompanyName());

        try {
            ProvisionCompanyRequest request = ProvisionCompanyRequest.builder()
                    .company(company)
                    .adminEmail(adminEmail)
                    .build();
            ProvisionCompanyResponse response = apiClient.provisionCompany(request, clientId, clientSecret);

            if (response.isProvisioned()) {
                logger.info("Company provisioned successfully: companyId={}", response.getCompanyId());
            } else if (response.isAlreadyExists()) {
                logger.info("Company already exists: companyId={}, requiresOAuth=true", response.getCompanyId());
            }

            return response;
        } catch (Exception e) {
            logger.error("Failed to provision company: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to provision company: " + e.getMessage(), e);
        }
    }
}
