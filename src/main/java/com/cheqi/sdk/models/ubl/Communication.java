package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:AggregatedComponents-2")
public class Communication {
    @XmlElement(name = "ChannelCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code channelCode;
    @XmlElement(name = "Channel", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String channel;
    @XmlElement(name = "Value", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String value;

    private Communication() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Communication communication;

        private Builder() {
            communication = new Communication();
        }

        public Builder channelCode(Code channelCode) {
            communication.channelCode = channelCode;
            return this;
        }

        public Builder channel(String channel) {
            communication.channel = channel;
            return this;
        }

        public Builder value(String value) {
            communication.value = value;
            return this;
        }

        public Communication build() {
            return communication;
        }
    }

    // Getters
    public Code getChannelCode() {
        return channelCode;
    }

    public String getChannel() {
        return channel;
    }

    public String getValue() {
        return value;
    }
}