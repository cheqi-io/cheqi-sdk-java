package com.cheqi.sdk.receipt;

import com.cheqi.commons.DTOs.RecipientResolutionResponse;

public class ProcessReceiptResult {
    private final boolean success;
    private final String message;
    private final int encryptedReceiptsCreated;
    private final int recipientsFound;
    private final RecipientResolutionResponse matchResponse;
    private final Exception error;

    private ProcessReceiptResult(
            boolean success,
            String message,
            int encryptedReceiptsCreated,
            int recipientsFound,
            RecipientResolutionResponse matchResponse,
            Exception error) {
        this.success = success;
        this.message = message;
        this.encryptedReceiptsCreated = encryptedReceiptsCreated;
        this.recipientsFound = recipientsFound;
        this.matchResponse = matchResponse;
        this.error = error;
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
                null
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
                error
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

    @Override
    public String toString() {
        return "ProcessReceiptResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", encryptedReceiptsCreated=" + encryptedReceiptsCreated +
                ", recipientsFound=" + recipientsFound +
                ", customerFound=" + isCustomerFound() +
                '}';
    }
}
