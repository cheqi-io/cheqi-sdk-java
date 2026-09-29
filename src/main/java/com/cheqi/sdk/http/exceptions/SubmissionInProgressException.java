package com.cheqi.sdk.http.exceptions;

/** An authenticated submission is already running; retry through match recovery. */
public final class SubmissionInProgressException extends CheqiApiException {
    private final int retryAfterSeconds;

    public SubmissionInProgressException(int retryAfterSeconds) {
        super("Receipt submission is in progress", 202, "SUBMISSION_IN_PROGRESS", null);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRetryAfterSeconds() { return retryAfterSeconds; }

    @Override
    public boolean isRetryable() { return true; }
}
