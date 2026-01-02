package com.cheqi.sdk.decryption;

public class DecryptedCreditNote {
    private final String creditNoteContentJson;

    public DecryptedCreditNote(String creditNoteContentJson) {
        this.creditNoteContentJson = creditNoteContentJson;
    }

    public String getCreditNoteContentJson() {
        return creditNoteContentJson;
    }
}