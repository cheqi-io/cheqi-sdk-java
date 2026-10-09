package com.cheqi.sdk.company;

import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.WebhookDTO;
import java.util.UUID;

/** Webhook subscription management. */
public class WebhookService {
    private final CheqiApiClient apiClient;

    public WebhookService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public WebhookDTO updateSubscriptionUrl(UUID subscriptionId, String notificationUrl, String accessToken)
            throws CheqiApiException {
        if (notificationUrl == null || notificationUrl.isBlank()) {
            throw new IllegalArgumentException("Notification URL is required");
        }
        return apiClient.updateWebhookSubscriptionUrl(subscriptionId, notificationUrl, accessToken);
    }
}
