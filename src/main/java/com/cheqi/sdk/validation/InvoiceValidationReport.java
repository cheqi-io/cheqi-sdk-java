package com.cheqi.sdk.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Objects;

/** Java binding of the existing issuer-only Rust InvoiceValidationReport. */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"profile", "valid", "completeDocument", "issues"})
public final class InvoiceValidationReport {
    private final String profile;
    private final boolean valid;
    private final boolean completeDocument;
    private final List<InvoiceValidationIssue> issues;

    @JsonCreator
    public InvoiceValidationReport(@JsonProperty("profile") String profile,
                                   @JsonProperty("valid") boolean valid,
                                   @JsonProperty("completeDocument") boolean completeDocument,
                                   @JsonProperty("issues") List<InvoiceValidationIssue> issues) {
        this.profile = Objects.requireNonNull(profile, "profile");
        this.valid = valid;
        this.completeDocument = completeDocument;
        this.issues = List.copyOf(Objects.requireNonNull(issues, "issues"));
    }

    public String getProfile() { return profile; }
    public boolean isValid() { return valid; }
    public boolean isCompleteDocument() { return completeDocument; }
    public List<InvoiceValidationIssue> getIssues() { return issues; }
}
