package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.config.ObjectMapperConfig;
import com.cheqi.sdk.encryption.EncryptionService;
import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.matching.MatchingService;
import com.cheqi.sdk.models.generated.EncryptedCreditNoteEnvelope;
import com.cheqi.sdk.models.generated.EncryptedReceiptPayload;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.MatchedRecipient;
import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Issues definitive credit-note values through the separate encrypted credit-note endpoint. */
public class CreditNoteService {
    private final CheqiApiClient apiClient;
    private final EncryptionService encryptionService;
    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;

    public CreditNoteService(
            CheqiApiClient apiClient,
            EncryptionService encryptionService,
            MatchingService matchingService
    ) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        this.encryptionService = Objects.requireNonNull(
                encryptionService,
                "encryptionService cannot be null"
        );
        this.matchingService = Objects.requireNonNull(matchingService, "matchingService cannot be null");
        this.objectMapper = ObjectMapperConfig.getInstance();
    }

    /**
     * Serializes and encrypts the supplied definitive credit-note payload without calculations.
     * A typed generated payload can replace {@code Object} once that schema is public.
     */
    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload,
            UUID storeId,
            String accessToken
    ) throws CheqiSDKException {
        requireNonNull(identificationDetails, "identificationDetails");
        requireText(parentCheqiReceiptId, "parentCheqiReceiptId");
        requireNonNull(creditNotePayload, "creditNotePayload");

        try {
            RecipientResolutionResponse resolution = accessToken == null
                    ? matchingService.matchCustomer(identificationDetails)
                    : matchingService.matchCustomer(identificationDetails, accessToken);
            validateDeviceRoute(resolution);

            String plaintextJson = creditNotePayload instanceof String
                    ? (String) creditNotePayload
                    : objectMapper.writeValueAsString(creditNotePayload);
            List<EncryptedReceiptPayload> deliveries = new ArrayList<>();
            for (MatchedRecipient recipient : resolution.getRecipients()) {
                deliveries.add(
                        encryptionService.encryptCreditNoteForRecipient(plaintextJson, recipient)
                );
            }

            EncryptedCreditNoteEnvelope envelope = new EncryptedCreditNoteEnvelope()
                    .matchId(resolution.getMatchId())
                    .storeId(storeId)
                    .parentCheqiReceiptId(parentCheqiReceiptId)
                    .deviceDeliveries(deliveries);
            ReceiptSubmissionResponse response = accessToken == null
                    ? apiClient.submitEncryptedCreditNote(envelope)
                    : apiClient.submitEncryptedCreditNote(envelope, accessToken);
            return CreditNoteResult.accepted(parentCheqiReceiptId, response);
        } catch (CheqiSDKException exception) {
            throw exception;
        } catch (CheqiApiException exception) {
            throw new CheqiSDKException(
                    "Credit-note submission failed: " + exception.getMessage(),
                    exception
            );
        } catch (Exception exception) {
            throw new CheqiSDKException(
                    "Credit-note processing failed: " + exception.getMessage(),
                    exception
            );
        }
    }

    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload,
            String accessToken
    ) throws CheqiSDKException {
        return issueCreditNote(
                identificationDetails,
                parentCheqiReceiptId,
                creditNotePayload,
                null,
                accessToken
        );
    }

    public CreditNoteResult issueCreditNote(
            IdentificationDetails identificationDetails,
            String parentCheqiReceiptId,
            Object creditNotePayload
    ) throws CheqiSDKException {
        return issueCreditNote(
                identificationDetails,
                parentCheqiReceiptId,
                creditNotePayload,
                null,
                null
        );
    }

    private static void validateDeviceRoute(RecipientResolutionResponse resolution)
            throws CheqiSDKException {
        if (resolution == null || !Boolean.TRUE.equals(resolution.getRouteFound())) {
            throw new CheqiSDKException(
                    "No Cheqi owner-device route was found",
                    CheqiSDKException.ErrorCodes.CUSTOMER_NOT_FOUND,
                    404,
                    null
            );
        }
        requireText(resolution.getMatchId(), "matchId");
        if (resolution.getRecipients() == null || resolution.getRecipients().isEmpty()) {
            throw validationError("Recipient resolution did not include owner devices");
        }
    }

    private static void requireNonNull(Object value, String name) throws CheqiSDKException {
        if (value == null) {
            throw validationError(name + " cannot be null");
        }
    }

    private static void requireText(String value, String name) throws CheqiSDKException {
        if (value == null || value.trim().isEmpty()) {
            throw validationError(name + " cannot be null or empty");
        }
    }

    private static CheqiSDKException validationError(String message) {
        return new CheqiSDKException(
                message,
                CheqiSDKException.ErrorCodes.VALIDATION_ERROR,
                400,
                null
        );
    }
}
