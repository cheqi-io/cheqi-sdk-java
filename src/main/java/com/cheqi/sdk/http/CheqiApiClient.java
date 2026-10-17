package com.cheqi.sdk.http;

import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.*;

import java.util.List;
import java.util.UUID;

/**
 * HTTP boundary used by the handwritten SDK services.
 *
 * <p>Every call takes {@link RequestOptions}; {@code null} means {@link RequestOptions#defaults()}.</p>
 */
public interface CheqiApiClient {

    /** Exchanges client credentials for a short-lived Company token bound to one Grant. */
    OAuthTokenExchangeResponse requestAccessToken(ClientCredentialsTokenRequest request, RequestOptions options)
            throws CheqiApiException;

    /** Match recovery uses only the opaque id; no original matching identifiers. */
    MatchStatusResponse getMatch(String matchId, RequestOptions options) throws CheqiApiException;

    RecipientResolutionResponse matchCustomer(IdentificationDetails request, RequestOptions options)
            throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedReceipt(EncryptedReceiptEnvelope request, RequestOptions options)
            throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedCreditNote(EncryptedCreditNoteEnvelope request, RequestOptions options)
            throws CheqiApiException;

    ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(
            ClientReceiptDownloadRequest request,
            RequestOptions options
    ) throws CheqiApiException;

    void sendReceiptViaEmail(String customerEmail, CheqiReceipt receipt, RequestOptions options)
            throws CheqiApiException;

    StoreDTO createStore(UUID companyId, CreateStoreRequest request, RequestOptions options)
            throws CheqiApiException;

    List<StoreDTO> getStores(UUID companyId, Boolean activeOnly, RequestOptions options)
            throws CheqiApiException;

    StoreDTO getStore(UUID companyId, UUID storeId, RequestOptions options)
            throws CheqiApiException;

    StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, RequestOptions options)
            throws CheqiApiException;

    void deleteStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException;

    void activateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException;

    void deactivateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiApiException;

    InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, Boolean resendPending,
                                        RequestOptions options) throws CheqiApiException;

    List<DestinationResponse> listReceiptDestinations(RequestOptions options) throws CheqiApiException;
    RegisterResponse registerReceiptDestination(RegisterRequest request, RequestOptions options) throws CheqiApiException;
    List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId, RequestOptions options) throws CheqiApiException;
    void acknowledgeReceipts(UUID destinationId, List<String> receiptIds, RequestOptions options) throws CheqiApiException;
    void deactivateReceiptDestination(UUID destinationId, RequestOptions options) throws CheqiApiException;
    WebhookDTO updateWebhookSubscriptionUrl(UUID subscriptionId, String notificationUrl, RequestOptions options)
            throws CheqiApiException;
}
