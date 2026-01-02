package com.cheqi.sdk.exceptions;

/**
 * Exception thrown when credit note processing fails.
 */
public class CreditNoteProcessingException extends RuntimeException {
    
    public CreditNoteProcessingException(String message) {
        super(message);
    }
    
    public CreditNoteProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}