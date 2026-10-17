package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.RequestOptions;
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
    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails) throws CheqiSDKException {
        return inviteUsers(companyId, emails, null, RequestOptions.defaults());
    }

    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, RequestOptions options)
            throws CheqiSDKException {
        return inviteUsers(companyId, emails, null, options);
    }

    public InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, Boolean resendPending,
                                               RequestOptions options) throws CheqiSDKException {
        if (companyId == null || emails == null || emails.isEmpty()) {
            throw new CheqiSDKException("Company ID and at least one email are required");
        }
        try {
            return apiClient.inviteUsers(companyId, emails, resendPending, options);
        } catch (CheqiApiException e) {
            throw new CheqiSDKException("Failed to invite employees: " + e.getMessage(), e,
                    e.getErrorCode(), e.getHttpStatusCode(), e.getCorrelationId());
        }
    }

    public List<DestinationResponse> listReceiptDestinations() throws CheqiApiException {
        return listReceiptDestinations(RequestOptions.defaults());
    }

    public List<DestinationResponse> listReceiptDestinations(RequestOptions options) throws CheqiApiException {
        return apiClient.listReceiptDestinations(options);
    }

    public RegisterResponse registerReceiptDestination(RegisterRequest request) throws CheqiApiException {
        return registerReceiptDestination(request, RequestOptions.defaults());
    }

    public RegisterResponse registerReceiptDestination(RegisterRequest request, RequestOptions options)
            throws CheqiApiException {
        return apiClient.registerReceiptDestination(request, options);
    }

    public List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId) throws CheqiApiException {
        return getPendingReceipts(destinationId, RequestOptions.defaults());
    }

    public List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId, RequestOptions options)
            throws CheqiApiException {
        return apiClient.getPendingReceipts(destinationId, options);
    }

    public void acknowledgeReceipts(UUID destinationId, List<String> receiptIds) throws CheqiApiException {
        acknowledgeReceipts(destinationId, receiptIds, RequestOptions.defaults());
    }

    public void acknowledgeReceipts(UUID destinationId, List<String> receiptIds, RequestOptions options)
            throws CheqiApiException {
        apiClient.acknowledgeReceipts(destinationId, receiptIds, options);
    }

    public void deactivateReceiptDestination(UUID destinationId) throws CheqiApiException {
        deactivateReceiptDestination(destinationId, RequestOptions.defaults());
    }

    public void deactivateReceiptDestination(UUID destinationId, RequestOptions options) throws CheqiApiException {
        apiClient.deactivateReceiptDestination(destinationId, options);
    }
}
