package com.cheqi.sdk.models;

public enum AccountIdentifierType {
    IBAN,
    ROUTING_ACCOUNT,   // for US
    SORT_CODE_ACCOUNT, // for UK
    TOKEN,
    WALLET,
    OTHER
}