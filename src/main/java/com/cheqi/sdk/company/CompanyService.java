package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;

import java.util.List;
import java.util.UUID;

/** Company membership operations. */
public class CompanyService {
    private final CheqiApiClient apiClient;

    public CompanyService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** Sends invitations to the supplied email addresses. A successful response has no body. */
    public void inviteUsers(UUID companyId, List<String> emails, String accessToken) throws CheqiSDKException {
        if (companyId == null || emails == null || emails.isEmpty()) {
            throw new CheqiSDKException("Company ID and at least one email are required");
        }
        try {
            apiClient.inviteUsers(companyId, emails, accessToken);
        } catch (CheqiApiException e) {
            throw new CheqiSDKException("Failed to invite employees: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
    }
}
