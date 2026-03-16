package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class NotificationDisplayCode {
    @JsonProperty("type")
    private final BarcodeType type;

    @JsonProperty("data")
    private final String data;

    private NotificationDisplayCode(BarcodeType type, String data) {
        this.type = type;
        this.data = data;
    }

    public BarcodeType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public static NotificationDisplayCode of(BarcodeType type, String data) {
        return new NotificationDisplayCode(type, data);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof NotificationDisplayCode)) return false;
        NotificationDisplayCode that = (NotificationDisplayCode) other;
        return type == that.type && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }

    @Override
    public String toString() {
        return "NotificationDisplayCode{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
