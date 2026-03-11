package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ExternalReference {
    @XmlElement(name = "URI", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String uri;

    public ExternalReference() {
        //No args constructor for JAXB
    }

    private ExternalReference(Builder builder) {
        this.uri = builder.uri;
    }

    public static class Builder {
        private String uri;

        public Builder(String uri) {
            if (uri == null) {
                throw new IllegalArgumentException("URI cannot be null");
            }
            this.uri = uri;
        }

        public ExternalReference build() {
            return new ExternalReference(this);
        }
    }

    public String getUri() {
        return uri;
    }
}
