package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.RecipientResolutionResponse;
import com.cheqi.commons.UBL.PurchaseReceipt;

public class ProcessReceiptResult {
    private final boolean success;
    private final String message;
    private final int encryptedReceiptsCreated;
    private final int recipientsFound;
    private final RecipientResolutionResponse matchResponse;
    private final Exception error;
    private final DeliveryStatus deliveryStatus;
    private final String emailAddress;

    private ProcessReceiptResult(
            boolean success,
            String message,
            int encryptedReceiptsCreated,
            int recipientsFound,
            RecipientResolutionResponse matchResponse,
            Exception error,
            DeliveryStatus deliveryStatus,
            String emailAddress) {
        this.success = success;
        this.message = message;
        this.encryptedReceiptsCreated = encryptedReceiptsCreated;
        this.recipientsFound = recipientsFound;
        this.matchResponse = matchResponse;
        this.error = error;
        this.deliveryStatus = deliveryStatus;
        this.emailAddress = emailAddress;
    }

    /**
     * Creates a successful result.
     *
     * @param receiptsCreated Number of encrypted receipts created
     * @param recipients Number of recipients found
     * @return Success result
     */
    public static ProcessReceiptResult success(int receiptsCreated, int recipients) {
        return new ProcessReceiptResult(
                true,
                "Receipt processed successfully",
                receiptsCreated,
                recipients,
                null,
                null,
                DeliveryStatus.DELIVERED_DIGITAL,
                null
        );
    }

    /**
     * Creates a successful result with match response details.
     *
     * @param receiptsCreated Number of encrypted receipts created
     * @param matchResponse The customer matching response
     * @return Success result with match details
     */
    public static ProcessReceiptResult success(int receiptsCreated, RecipientResolutionResponse matchResponse) {
        return new ProcessReceiptResult(
                true,
                "Receipt processed successfully",
                receiptsCreated,
                matchResponse != null ? matchResponse.getRecipients().size() : 0,
                matchResponse,
                null,
                DeliveryStatus.DELIVERED_DIGITAL,
                null
        );
    }

    /**
     * Creates a result for when customer is not found.
     *
     * @return Customer not found result
     */
    public static ProcessReceiptResult customerNotFound() {
        return new ProcessReceiptResult(
                false,
                "Customer not found with provided payment details",
                0,
                0,
                null,
                null,
                DeliveryStatus.CUSTOMER_NOT_FOUND,
                null
        );
    }
    
    /**
     * Creates a successful result for email delivery.
     *
     * @param emailAddress Email address where receipt was sent
     * @return Success result for email delivery
     */
    public static ProcessReceiptResult emailSent(String emailAddress) {
        return new ProcessReceiptResult(
                true,
                "Receipt sent via email to " + emailAddress,
                0,
                0,
                null,
                null,
                DeliveryStatus.DELIVERED_EMAIL,
                emailAddress
        );
    }

    /**
     * Creates a failure result with error details.
     *
     * @param message Error message
     * @param error The exception that caused the failure
     * @return Failure result
     */
    public static ProcessReceiptResult failure(String message, Exception error) {
        return new ProcessReceiptResult(
                false,
                message,
                0,
                0,
                null,
                error,
                DeliveryStatus.FAILED,
                null
        );
    }

    /**
     * Creates a failure result with custom message.
     *
     * @param message Error message
     * @return Failure result
     */
    public static ProcessReceiptResult failure(String message) {
        return new ProcessReceiptResult(
                false,
                message,
                0,
                0,
                null,
                null,
                DeliveryStatus.FAILED,
                null
        );
    }

    // Getters

    /**
     * @return true if the receipt processing was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return true if the receipt processing failed
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * @return Descriptive message about the result
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Number of encrypted receipts that were created
     */
    public int getEncryptedReceiptsCreated() {
        return encryptedReceiptsCreated;
    }

    /**
     * @return Number of recipients found during customer matching
     */
    public int getRecipientsFound() {
        return recipientsFound;
    }

    /**
     * @return The customer matching response, if available
     */
    public RecipientResolutionResponse getMatchResponse() {
        return matchResponse;
    }

    /**
     * @return The exception that caused the failure, if any
     */
    public Exception getError() {
        return error;
    }

    /**
     * @return true if customer was found during matching
     */
    public boolean isCustomerFound() {
        return matchResponse != null && matchResponse.isCustomerFound();
    }
    
    /**
     * @return true if customer was not found (POS should ask for email)
     */
    public boolean isCustomerNotFound() {
        return deliveryStatus == DeliveryStatus.CUSTOMER_NOT_FOUND;
    }
    
    /**
     * @return Delivery status for easy POS handling
     */
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    
    /**
     * @return Email address (suggested or where receipt was sent)
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @return true if email fallback is recommended
     */
    public boolean requiresEmailFallback() {
        return deliveryStatus == DeliveryStatus.CUSTOMER_NOT_FOUND;
    }

    @Override
    public String toString() {
        return "ProcessReceiptResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", deliveryStatus=" + deliveryStatus +
                ", encryptedReceiptsCreated=" + encryptedReceiptsCreated +
                ", recipientsFound=" + recipientsFound +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
