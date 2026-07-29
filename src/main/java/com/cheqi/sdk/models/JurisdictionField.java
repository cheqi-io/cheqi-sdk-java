package com.cheqi.sdk.models;

/** Factory helpers for a country-namespaced jurisdiction extension field. */
public class JurisdictionField extends com.cheqi.sdk.models.generated.JurisdictionField {
    public static JurisdictionField of(String code, String value) {
        return of(code, value, null);
    }

    public static JurisdictionField of(String code, String value, String label) {
        JurisdictionField field = new JurisdictionField();
        field.setCode(code);
        field.setValue(value);
        field.setLabel(label);
        return field;
    }
}
