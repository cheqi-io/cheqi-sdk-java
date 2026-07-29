package com.cheqi.sdk.models;

import java.util.ArrayList;
import java.util.List;

/** Convenience builder for issuer-supplied country-specific receipt data. */
public class JurisdictionalData extends com.cheqi.sdk.models.generated.JurisdictionalData {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String countryCode;
        private String profile;
        private String profileVersion;
        private com.cheqi.sdk.models.generated.FiscalizationData fiscalization;
        private final List<com.cheqi.sdk.models.generated.JurisdictionLegalText> legalTexts =
                new ArrayList<>();
        private final List<com.cheqi.sdk.models.generated.JurisdictionField> additionalFields =
                new ArrayList<>();

        private Builder() {
        }

        public Builder countryCode(String value) { countryCode = value; return this; }
        public Builder profile(String value) { profile = value; return this; }
        public Builder profileVersion(String value) { profileVersion = value; return this; }

        public Builder fiscalization(com.cheqi.sdk.models.generated.FiscalizationData value) {
            fiscalization = value;
            return this;
        }

        public Builder addLegalText(
                com.cheqi.sdk.models.generated.JurisdictionLegalText value
        ) {
            legalTexts.add(value);
            return this;
        }

        public Builder addLegalText(String code, String text, String languageCode) {
            legalTexts.add(JurisdictionLegalText.of(code, text, languageCode));
            return this;
        }

        public Builder addField(com.cheqi.sdk.models.generated.JurisdictionField value) {
            additionalFields.add(value);
            return this;
        }

        public Builder addField(String code, String value, String label) {
            additionalFields.add(JurisdictionField.of(code, value, label));
            return this;
        }

        public JurisdictionalData build() {
            if (profile == null || profile.trim().isEmpty()) {
                throw new IllegalStateException("profile is required");
            }
            JurisdictionalData data = new JurisdictionalData();
            data.setCountryCode(countryCode);
            data.setProfile(profile);
            data.setProfileVersion(profileVersion);
            data.setFiscalization(fiscalization);
            data.setLegalTexts(legalTexts.isEmpty() ? null : legalTexts);
            data.setAdditionalFields(additionalFields.isEmpty() ? null : additionalFields);
            return data;
        }
    }
}
