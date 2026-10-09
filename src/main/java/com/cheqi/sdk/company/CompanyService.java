package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.DestinationResponse;
import com.cheqi.sdk.models.generated.InviteEmployeesResponse;
import com.cheqi.sdk.models.generated.RegisterRequest;
import com.cheqi.sdk.models.generated.RegisterResponse;
import com.cheqi.sdk.models.generated.WebhookReceiptEnvelope;

import java.util.List;
import java.util.UUID;

/** Company membership operations. */
public class CompanyService {
    private final CheqiApiClient apiClient;

    public CompanyService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** Sends invitations to the supplied email addresses and returns their outcomes. */
    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, String accessToken) throws CheqiSDKException {
        return inviteUsers(companyId, emails, null, accessToken);
    }

    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, Boolean resendPending, String accessToken) throws CheqiSDKException {
        if (companyId == null || emails == null || emails.isEmpty()) {
            throw new CheqiSDKException("Company ID and at least one email are required");
        }
        try {
            return apiClient.inviteUsers(companyId, emails, resendPending, accessToken);
        } catch (CheqiApiException e) {
            throw new CheqiSDKException("Failed to invite employees: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
    }

    public List<DestinationResponse> listReceiptDestinations(String accessToken) throws CheqiApiException {
        return apiClient.listReceiptDestinations(accessToken);
    }

    public RegisterResponse registerReceiptDestination(RegisterRequest request, String accessToken) throws CheqiApiException {
        return apiClient.registerReceiptDestination(request, accessToken);
    }

    public List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId, String accessToken) throws CheqiApiException {
        return apiClient.getPendingReceipts(destinationId, accessToken);
    }

    public void acknowledgeReceipts(UUID destinationId, List<String> receiptIds, String accessToken) throws CheqiApiException {
        apiClient.acknowledgeReceipts(destinationId, receiptIds, accessToken);
    }

    public void deactivateReceiptDestination(UUID destinationId, String accessToken) throws CheqiApiException {
        apiClient.deactivateReceiptDestination(destinationId, accessToken);
    }
}
