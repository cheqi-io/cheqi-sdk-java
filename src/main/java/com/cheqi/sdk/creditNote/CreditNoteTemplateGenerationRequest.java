package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.ReceiptFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreditNoteTemplateGenerationRequest {
    @JsonProperty("creditNoteTemplateRequest")
    private CreditNoteTemplateRequest creditNoteTemplateRequest;

    @JsonProperty("formats")
    private List<ReceiptFormat> formats;

    public CreditNoteTemplateGenerationRequest() {
    }

    public CreditNoteTemplateGenerationRequest(CreditNoteTemplateRequest creditNoteTemplateRequest, List<ReceiptFormat> formats) {
        this.creditNoteTemplateRequest = creditNoteTemplateRequest;
        this.formats = formats;
    }

    public CreditNoteTemplateRequest getCreditNoteTemplateRequest() {
        return creditNoteTemplateRequest;
    }

    public void setCreditNoteTemplateRequest(CreditNoteTemplateRequest creditNoteTemplateRequest) {
        this.creditNoteTemplateRequest = creditNoteTemplateRequest;
    }

    public List<ReceiptFormat> getFormats() {
        return formats;
    }

    public void setFormats(List<ReceiptFormat> formats) {
        this.formats = formats;
    }
}
