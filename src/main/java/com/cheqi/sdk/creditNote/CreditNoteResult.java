package com.cheqi.sdk.creditNote;

import com.cheqi.sdk.models.generated.ReceiptSubmissionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

/** Result returned after Cheqi accepts an encrypted credit-note processing job. */
public class CreditNoteResult {
    @JsonProperty("cheqiReceiptId")
    private final String cheqiReceiptId;
    @JsonProperty("parentCheqiReceiptId")
    private final String parentCheqiReceiptId;
    @JsonProperty("matchId")
    private final String matchId;
    @JsonProperty("status")
    private final ReceiptSubmissionResponse.StatusEnum status;
    @JsonProperty("createdAt")
    private final OffsetDateTime createdAt;

    private CreditNoteResult(String parentCheqiReceiptId, ReceiptSubmissionResponse response) {
        this.cheqiReceiptId = response.getCheqiReceiptId();
        this.parentCheqiReceiptId = parentCheqiReceiptId;
        this.matchId = response.getMatchId();
        this.status = response.getStatus();
        this.createdAt = response.getCreatedAt();
    }

    public static CreditNoteResult accepted(
            String parentCheqiReceiptId,
            ReceiptSubmissionResponse response
    ) {
        if (response == null) {
            throw new IllegalArgumentException("response cannot be null");
        }
        return new CreditNoteResult(parentCheqiReceiptId, response);
    }

    public boolean isAccepted() {
        return cheqiReceiptId != null && !cheqiReceiptId.trim().isEmpty();
    }

    public boolean isSuccess() {
        return isAccepted();
    }

    public String getCheqiReceiptId() {
        return cheqiReceiptId;
    }

    public String getParentCheqiReceiptId() {
        return parentCheqiReceiptId;
    }

    public String getMatchId() {
        return matchId;
    }

    public ReceiptSubmissionResponse.StatusEnum getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
