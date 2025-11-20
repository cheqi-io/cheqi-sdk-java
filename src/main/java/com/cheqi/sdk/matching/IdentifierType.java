package com.cheqi.sdk.matching;

/**
 * Types of customer identifiers supported for matching.
 */
public enum IdentifierType {
    /**
     * Payment Account Reference - tokenized card identifier.
     */
    PAR,
    
    /**
     * Customer email address.
     */
    EMAIL,
    
    /**
     * Primary Account Number - card number.
     */
    PAN,
    
    /**
     * International Bank Account Number.
     */
    IBAN
}
