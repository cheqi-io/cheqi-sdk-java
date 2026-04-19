package com.cheqi.sdk.decryption;

import com.cheqi.sdk.creditNote.CreditNoteEnvelope;
import com.cheqi.sdk.models.generated.CreditNoteContextEnvelope;

public class DecryptedDeliveredCreditNote {
    private CreditNoteEnvelope creditNoteEnvelope;
    private CreditNoteContextEnvelope creditNoteContextEnvelope;
    private final String creditNoteContentJson;
    private final String creditNoteContextJson;

    public DecryptedDeliveredCreditNote(
            CreditNoteEnvelope creditNoteEnvelope,
            CreditNoteContextEnvelope creditNoteContextEnvelope,
            String creditNoteContentJson,
            String creditNoteContextJson) {
        this.creditNoteEnvelope = creditNoteEnvelope;
        this.creditNoteContextEnvelope = creditNoteContextEnvelope;
        this.creditNoteContentJson = creditNoteContentJson;
        this.creditNoteContextJson = creditNoteContextJson;
    }

    public CreditNoteEnvelope getCreditNoteEnvelope() {
        return creditNoteEnvelope;
    }

    public void setCreditNoteEnvelope(CreditNoteEnvelope creditNoteEnvelope) {
        this.creditNoteEnvelope = creditNoteEnvelope;
    }

    public CreditNoteContextEnvelope getCreditNoteContextEnvelope() {
        return creditNoteContextEnvelope;
    }

    public void setCreditNoteContextEnvelope(CreditNoteContextEnvelope creditNoteContextEnvelope) {
        this.creditNoteContextEnvelope = creditNoteContextEnvelope;
    }

    public String getCreditNoteContentJson() {
        return creditNoteContentJson;
    }

    public String getCreditNoteContextJson() {
        return creditNoteContextJson;
    }
}
