package com.cheqi.sdk.models;

import com.cheqi.sdk.models.generated.FiscalizationStatus;

/** Required fiscalization identity plus fluent access to generated optional fields. */
public class FiscalizationData extends com.cheqi.sdk.models.generated.FiscalizationData {
    public static FiscalizationData of(String system, FiscalizationStatus status) {
        if (system == null || system.trim().isEmpty()) {
            throw new IllegalArgumentException("system is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
        FiscalizationData data = new FiscalizationData();
        data.setSystem(system);
        data.setStatus(status);
        return data;
    }

    public FiscalizationData addField(String code, String value, String label) {
        addAdditionalFieldsItem(JurisdictionField.of(code, value, label));
        return this;
    }
}
