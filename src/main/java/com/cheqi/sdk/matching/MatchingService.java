package com.cheqi.sdk.matching;

import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import com.cheqi.sdk.models.generated.CardDetails;
import com.cheqi.sdk.models.generated.IdentificationDetails;
import com.cheqi.sdk.models.generated.PaymentAccountDetails;
import com.cheqi.sdk.models.generated.RecipientResolutionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for secure customer matching using payment details.
 * This service sends identification details to the backend for recipient resolution.
 * The backend determines the best matching strategy based on available identifiers
 * (tried in priority order):
 * <ol>
 *   <li>Pairing code (temporary code from the customer's Cheqi app)</li>
 *   <li>Card details (PAN, PAR)</li>
 *   <li>Payment account details (IBAN)</li>
 *   <li>Customer email</li>
 * </ol>
 * Thread-safe and designed for high-throughput matching operations.
 *
 * @see IdentificationDetails
 */
public class MatchingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    
    private final CheqiApiClient apiClient;
    
    public MatchingService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Matches a customer using the provided identification details (OAuth mode).
     *
     * <p>At least one identifier must be present: pairing code, card details,
     * payment account details, or email.</p>
     *
     * @param identificationDetails Customer identifiers (card, pairing code, email, etc.)
     * @param accessToken OAuth access token for API calls
     * @return RecipientResolutionResponse with matched recipients or empty if not found
     * @throws CheqiApiException if matching operation fails
     * @throws IllegalArgumentException if no valid identifiers are provided
     */
    public RecipientResolutionResponse matchCustomer(
            IdentificationDetails identificationDetails,
            String accessToken) throws CheqiApiException {
        
        logger.debug("Starting customer matching");
        
        // Validate request has at least one identifier
        if (!hasValidIdentifiers(identificationDetails)) {
            String error = "IdentificationDetails must contain at least one identifier (card details, payment account details, or email)";
            logger.error(error);
            throw new IllegalArgumentException(error);
        }

        logAvailableIdentifiers(identificationDetails);
        
        try {
            RecipientResolutionResponse response = apiClient.matchCustomer(identificationDetails, accessToken);
            
            if (Boolean.TRUE.equals(response.getRouteFound())) {
                logger.info("Customer matched successfully: {} recipients found", response.getRecipients().size());
            } else {
                logger.info("No customer match found");
            }
            
            return response;
            
        } catch (CheqiApiException e) {
            logger.error("Customer matching failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during customer matching: {}", e.getMessage(), e);
            throw new CheqiApiException(
                "Customer matching failed: " + e.getMessage(),
                e,
                0,
                CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                null
            );
        }
    }

    /**
     * Matches a customer using API key authentication (configured during SDK initialization).
     *
     * <p>At least one identifier must be present: pairing code, card details,
     * payment account details, or email.</p>
     *
     * @param identificationDetails Customer identifiers (card, pairing code, email, etc.)
     * @return RecipientResolutionResponse with matched recipients or empty if not found
     * @throws CheqiApiException if matching operation fails
     * @throws IllegalArgumentException if no valid identifiers are provided
     */
    public RecipientResolutionResponse matchCustomer(
            IdentificationDetails identificationDetails) throws CheqiApiException {

        logger.debug("Starting customer matching with API key");

        // Validate request has at least one identifier
        if (!hasValidIdentifiers(identificationDetails)) {
            String error = "Identification details must contain at least one identifier (card details, payment account details, or email)";
            logger.error(error);
            throw new IllegalArgumentException(error);
        }

        logAvailableIdentifiers(identificationDetails);

        try {
            RecipientResolutionResponse response = apiClient.matchCustomer(identificationDetails);

            if (Boolean.TRUE.equals(response.getRouteFound())) {
                logger.info("Customer matched successfully: {} recipients found", response.getRecipients().size());
            } else {
                logger.info("No customer match found");
            }

            return response;

        } catch (CheqiApiException e) {
            logger.error("Customer matching failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during customer matching: {}", e.getMessage(), e);
            throw new CheqiApiException(
                    "Customer matching failed: " + e.getMessage(),
                    e,
                    0,
                    CheqiApiException.ErrorCodes.UNKNOWN_ERROR,
                    null
            );
        }
    }
    
    /**
     * Validates that the payment details are usable for resolution.
     *
     * <p>A request is valid when it carries a customer locator (card details, payment account
     * details, cheqiReceiptId, email or pairing code) <em>or</em> just a payment type. The latter
     * is the anonymous "no customer" case: the backend resolves it to the QR-code download fallback
     * route and returns a self-service download URL.
     */
    private boolean hasValidIdentifiers(IdentificationDetails identificationDetails) {
        if (identificationDetails == null) {
            return false;
        }

        return hasCardDetails(identificationDetails) ||
               hasPaymentAccountDetails(identificationDetails) ||
               hasCheqiReceiptId(identificationDetails) ||
               hasEmail(identificationDetails) ||
               hasPairingCode(identificationDetails) ||
               hasPaymentType(identificationDetails);
    }

    /**
     * Checks if a payment type is present (anonymous "no customer" download-fallback case).
     */
    private boolean hasPaymentType(IdentificationDetails identificationDetails) {
        return identificationDetails.getPaymentType() != null;
    }
    
    /**
     * Checks if card details (PAN or PAR) are present.
     */
    private boolean hasCardDetails(IdentificationDetails identificationDetails) {
        if (identificationDetails.getCardDetails() == null) {
            return false;
        }
        
        CardDetails card = identificationDetails.getCardDetails();
        return isNotEmpty(card.getPaymentAccountReference());
    }

    private boolean hasCheqiReceiptId(IdentificationDetails identificationDetails){
        return isNotEmpty(identificationDetails.getCheqiReceiptId());
    }
    
    /**
     * Checks if payment account details (IBAN) are present.
     */
    private boolean hasPaymentAccountDetails(IdentificationDetails identificationDetails) {
        if (identificationDetails.getPaymentAccountDetails() == null) {
            return false;
        }
        
        PaymentAccountDetails details = identificationDetails.getPaymentAccountDetails();
        return isNotEmpty(details.getIdentifier());
    }
    
    /**
     * Checks if customer email is present.
     */
    private boolean hasEmail(IdentificationDetails identificationDetails) {
        return isNotEmpty(identificationDetails.getRecipientEmail());
    }

    private boolean hasPairingCode(IdentificationDetails identificationDetails) {
        return isNotEmpty(identificationDetails.getPairingCode());
    }
    
    /**
     * Logs which identifiers are available for matching.
     */
    private void logAvailableIdentifiers(IdentificationDetails identificationDetails) {
        StringBuilder identifiers = new StringBuilder("Available identifiers: ");
        
        if (hasCardDetails(identificationDetails)) {
            identifiers.append("[Card] ");
        }
        if (hasPaymentAccountDetails(identificationDetails)) {
            identifiers.append("[PaymentAccount] ");
        }
        if (hasEmail(identificationDetails)) {
            identifiers.append("[Email] ");
        }
        if (hasCheqiReceiptId(identificationDetails)) {
            identifiers.append("[CheqiReceiptId] ");
        }
        if (hasPairingCode(identificationDetails)) {
            identifiers.append("[PairingCode] ");
        }
        
        logger.debug(identifiers.toString().trim());
    }
    
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
