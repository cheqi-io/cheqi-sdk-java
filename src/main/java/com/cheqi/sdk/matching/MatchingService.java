package com.cheqi.sdk.matching;

import com.cheqi.commons.DTOs.*;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.exceptions.CheqiApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for secure customer matching using the commons DTOs.
 * 
 * This service sends RecipientResolutionRequest objects to the backend
 * and lets the backend handle matching logic based on available data.
 */
public class MatchingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    
    private final CheqiApiClient apiClient;
    
    public MatchingService(CheqiApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Matches a customer using the provided identifiers.
     * 
     * The backend will determine the best matching strategy based on
     * which fields are populated in the request (card details, payment
     * account details, email, etc.).
     * 
     * @param request RecipientResolutionRequest with available identifiers
     * @param accessToken OAuth access token for API calls
     * @return MatchingResult with customer information if found
     * @throws CheqiApiException if matching operation fails
     */
    public RecipientResolutionResponse matchCustomer(
            PaymentDetails request,
            String accessToken) throws CheqiApiException {
        
        logger.info("Starting customer matching with RecipientResolutionRequest");
        
        try {
            // Validate request has at least some identifier
            if (!hasValidIdentifiers(request)) {
                throw new IllegalArgumentException("RecipientResolutionRequest must contain at least one identifier (card details, payment account details, or email)");
            }
            
            // Call backend matching endpoint
            RecipientResolutionResponse response = apiClient.matchCustomer(request, accessToken);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Customer matching failed", e);
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
     * Validates that the request contains at least one identifier.
     */
    private boolean hasValidIdentifiers(PaymentDetails request) {
        if (request == null) {
            return false;
        }
        
        // Check if card details are provided
        if (request.getCardDetails().isPresent()) {
            CardDetails card = request.getCardDetails().get();
            if ((card.getPaymentAccountNumber().isPresent() && isNotEmpty(card.getPaymentAccountNumber().get())) || 
                (card.getPaymentAccountReference().isPresent() && isNotEmpty(card.getPaymentAccountReference().get()))) {
                return true;
            }
        }
        
        // Check if payment account details are provided
        if (request.getPaymentAccountDetails().isPresent()) {
            PaymentAccountDetails paymentAccountDetails = request.getPaymentAccountDetails().get();
            if (isNotEmpty(paymentAccountDetails.getIdentifier())) {
                return true;
            }
        }
        
        // Check if email is provided
        if (request.getCustomerEmail().isPresent() && isNotEmpty(request.getCustomerEmail().get())) {
            return true;
        }
        
        return false;
    }
    
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
