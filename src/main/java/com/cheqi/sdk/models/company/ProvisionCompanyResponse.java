package com.cheqi.sdk.models.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Response from company provisioning endpoint.
 * Contains company ID and OAuth tokens for immediate API access.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ProvisionCompanyResponse {

    @JsonProperty("companyId")
    private UUID companyId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("expiresIn")
    private Long expiresIn;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("invitationSent")
    private Boolean invitationSent;

    @JsonProperty("requiresOAuth")
    private Boolean requiresOAuth;

    public ProvisionCompanyResponse() {}

    public UUID getCompanyId() { return companyId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public String getTokenType() { return tokenType; }
    public Boolean getInvitationSent() { return invitationSent; }
    public Boolean getRequiresOAuth() { return requiresOAuth; }

    public boolean isProvisioned() {
        return "PROVISIONED".equals(status);
    }

    public boolean isAlreadyExists() {
        return "ALREADY_EXISTS".equals(status);
    }

    public boolean hasTokens() {
        return accessToken != null && !accessToken.isEmpty();
    }

    @Override
    public String toString() {
        return "ProvisionCompanyResponse{" +
                "companyId=" + companyId +
                ", status='" + status + '\'' +
                ", requiresOAuth=" + requiresOAuth +
                ", hasTokens=" + hasTokens() +
                '}';
    }
}