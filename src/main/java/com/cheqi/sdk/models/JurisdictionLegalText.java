package com.cheqi.sdk.models;

/** Factory helpers for exact issuer-supplied legal receipt text. */
public class JurisdictionLegalText
        extends com.cheqi.sdk.models.generated.JurisdictionLegalText {
    public static JurisdictionLegalText of(String code, String text) {
        return of(code, text, null);
    }

    public static JurisdictionLegalText of(String code, String text, String languageCode) {
        JurisdictionLegalText legalText = new JurisdictionLegalText();
        legalText.setCode(code);
        legalText.setText(text);
        legalText.setLanguageCode(languageCode);
        return legalText;
    }
}
