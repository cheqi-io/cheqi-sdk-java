package com.cheqi.sdk.matching;

import com.cheqi.commons.DTOs.*;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for secure customer matching using payment details.
 * 
 * This service sends PaymentDetails to the backend for recipient resolution.
 * The backend determines the best matching strategy based on available identifiers:
 * - Card details (PAN, PAR)
 * - Payment account details (IBAN)
 * - Customer email
 *
 * Thread-safe and designed for high-throughput matching operations.
 */
public class MatchingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    
    private final CheqiApiClient apiClient;
    
    public MatchingService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Matches a customer using the provided payment details.
     * 
     * The backend determines the best matching strategy based on available identifiers:
     * - Card details (PAN, PAR)
     * - Payment account details (IBAN)
     * - Customer email
     * 
     * @param paymentDetails Payment details containing customer identifiers
     * @param accessToken OAuth access token for API calls
     * @return RecipientResolutionResponse with matched recipients or empty if not found
     * @throws CheqiApiException if matching operation fails
     * @throws IllegalArgumentException if paymentDetails contains no valid identifiers
     */
    public RecipientResolutionResponse matchCustomer(
            PaymentDetails paymentDetails,
            String accessToken) throws CheqiApiException {
        
        logger.debug("Starting customer matching");
        
        // Validate request has at least one identifier
        if (!hasValidIdentifiers(paymentDetails)) {
            String error = "PaymentDetails must contain at least one identifier (card details, payment account details, or email)";
            logger.error(error);
            throw new IllegalArgumentException(error);
        }
        
        logAvailableIdentifiers(paymentDetails);
        
        try {
            RecipientResolutionResponse response = apiClient.matchCustomer(paymentDetails, accessToken);
            
            if (response.isCustomerFound()) {
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
     * Validates that the payment details contain at least one identifier.
     */
    private boolean hasValidIdentifiers(PaymentDetails paymentDetails) {
        if (paymentDetails == null) {
            return false;
        }
        
        return hasCardDetails(paymentDetails) ||
               hasPaymentAccountDetails(paymentDetails) ||
               hasEmail(paymentDetails);
    }
    
    /**
     * Checks if card details (PAN or PAR) are present.
     */
    private boolean hasCardDetails(PaymentDetails paymentDetails) {
        if (!paymentDetails.getCardDetails().isPresent()) {
            return false;
        }
        
        CardDetails card = paymentDetails.getCardDetails().get();
        return (card.getPaymentAccountNumber().isPresent() && isNotEmpty(card.getPaymentAccountNumber().get())) ||
               (card.getPaymentAccountReference().isPresent() && isNotEmpty(card.getPaymentAccountReference().get()));
    }
    
    /**
     * Checks if payment account details (IBAN) are present.
     */
    private boolean hasPaymentAccountDetails(PaymentDetails paymentDetails) {
        if (!paymentDetails.getPaymentAccountDetails().isPresent()) {
            return false;
        }
        
        PaymentAccountDetails details = paymentDetails.getPaymentAccountDetails().get();
        return isNotEmpty(details.getIdentifier());
    }
    
    /**
     * Checks if customer email is present.
     */
    private boolean hasEmail(PaymentDetails paymentDetails) {
        return paymentDetails.getCustomerEmail().isPresent() && 
               isNotEmpty(paymentDetails.getCustomerEmail().get());
    }
    
    /**
     * Logs which identifiers are available for matching.
     */
    private void logAvailableIdentifiers(PaymentDetails paymentDetails) {
        StringBuilder identifiers = new StringBuilder("Available identifiers: ");
        
        if (hasCardDetails(paymentDetails)) {
            identifiers.append("[Card] ");
        }
        if (hasPaymentAccountDetails(paymentDetails)) {
            identifiers.append("[PaymentAccount] ");
        }
        if (hasEmail(paymentDetails)) {
            identifiers.append("[Email] ");
        }
        
        logger.debug(identifiers.toString().trim());
    }
    
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
