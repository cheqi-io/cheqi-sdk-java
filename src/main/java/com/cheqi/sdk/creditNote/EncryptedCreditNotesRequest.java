package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.EncryptedCreditNote;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class EncryptedCreditNotesRequest {
    @JsonProperty("matchId")
    private String matchId;
    @JsonProperty("parentCheqiReceiptId")
    private String parentCheqiReceiptId;
    @JsonProperty("encryptedCreditNotes")
    private Set<EncryptedCreditNote> encryptedCreditNotes;
    @JsonProperty("templateHash")
    private String templateHash;

    public EncryptedCreditNotesRequest() {
    }

    public EncryptedCreditNotesRequest(String matchId, String parentCheqiReceiptId, Set<EncryptedCreditNote> encryptedCreditNotes, String templateHash) {
        this.encryptedCreditNotes = encryptedCreditNotes;
        this.templateHash = templateHash;
        this.matchId = matchId;
        this.parentCheqiReceiptId = parentCheqiReceiptId;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getParentCheqiReceiptId() {
        return this.parentCheqiReceiptId;
    }

    public void setParentCheqiReceiptId(String parentCheqiReceiptId) {
        this.parentCheqiReceiptId = parentCheqiReceiptId;
    }

    public Set<EncryptedCreditNote> getEncryptedCreditNotes() {
        return this.encryptedCreditNotes;
    }

    public void setEncryptedCreditNotes(Set<EncryptedCreditNote> encryptedCreditNotes) {
        this.encryptedCreditNotes = encryptedCreditNotes;
    }

    public String getTemplateHash() {
        return this.templateHash;
    }

    public void setTemplateHash(String templateHash) {
        this.templateHash = templateHash;
    }
}
