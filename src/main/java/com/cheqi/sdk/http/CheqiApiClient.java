package com.cheqi.sdk.http;

import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.*;

import java.util.List;
import java.util.UUID;

/** HTTP boundary used by the handwritten SDK services. */
public interface CheqiApiClient {

    /** Match recovery uses only the opaque id; no original matching identifiers. */
    MatchStatusResponse getMatch(String matchId, String accessToken) throws CheqiApiException;
    MatchStatusResponse getMatch(String matchId) throws CheqiApiException;


    RecipientResolutionResponse matchCustomer(
            IdentificationDetails request,
            String accessToken
    ) throws CheqiApiException;

    RecipientResolutionResponse matchCustomer(
            IdentificationDetails request
    ) throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedReceipt(
            EncryptedReceiptEnvelope request,
            String accessToken
    ) throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedReceipt(
            EncryptedReceiptEnvelope request
    ) throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedCreditNote(
            EncryptedCreditNoteEnvelope request,
            String accessToken
    ) throws CheqiApiException;

    ReceiptSubmissionResponse submitEncryptedCreditNote(
            EncryptedCreditNoteEnvelope request
    ) throws CheqiApiException;

    ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(
            ClientReceiptDownloadRequest request,
            String accessToken
    ) throws CheqiApiException;

    ClientReceiptDownloadResponse uploadEncryptedDownloadReceipt(
            ClientReceiptDownloadRequest request
    ) throws CheqiApiException;

    void sendReceiptViaEmail(
            String customerEmail,
            CheqiReceipt receipt,
            String accessToken
    ) throws CheqiApiException;

    void sendReceiptViaEmail(
            String customerEmail,
            CheqiReceipt receipt
    ) throws CheqiApiException;

    StoreDTO createStore(UUID companyId, CreateStoreRequest request, String accessToken)
            throws CheqiApiException;

    List<StoreDTO> getStores(UUID companyId, Boolean activeOnly, String accessToken)
            throws CheqiApiException;

    StoreDTO getStore(UUID companyId, UUID storeId, String accessToken)
            throws CheqiApiException;

    StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, String accessToken)
            throws CheqiApiException;

    void deleteStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException;

    void activateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException;

    void deactivateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiApiException;

    InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, String accessToken) throws CheqiApiException;
    InviteEmployeesResponse inviteUsers(UUID companyId, List<String> emails, Boolean resendPending, String accessToken) throws CheqiApiException;

    List<DestinationResponse> listReceiptDestinations(String accessToken) throws CheqiApiException;
    RegisterResponse registerReceiptDestination(RegisterRequest request, String accessToken) throws CheqiApiException;
    List<WebhookReceiptEnvelope> getPendingReceipts(UUID destinationId, String accessToken) throws CheqiApiException;
    void acknowledgeReceipts(UUID destinationId, List<String> receiptIds, String accessToken) throws CheqiApiException;
    void deactivateReceiptDestination(UUID destinationId, String accessToken) throws CheqiApiException;
    WebhookDTO updateWebhookSubscriptionUrl(UUID subscriptionId, String notificationUrl, String accessToken) throws CheqiApiException;
}
