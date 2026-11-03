package com.cheqi.sdk.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;

/** A validation issue identifying the affected field and failed rule. */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"ruleId", "path", "message"})
public final class InvoiceValidationIssue {
    private final String ruleId;
    private final String path;
    private final String message;

    @JsonCreator
    public InvoiceValidationIssue(@JsonProperty("ruleId") String ruleId,
                                  @JsonProperty("path") String path,
                                  @JsonProperty("message") String message) {
        this.ruleId = Objects.requireNonNull(ruleId, "ruleId");
        this.path = Objects.requireNonNull(path, "path");
        this.message = Objects.requireNonNull(message, "message");
    }

    public String getRuleId() { return ruleId; }
    public String getPath() { return path; }
    public String getMessage() { return message; }
}
