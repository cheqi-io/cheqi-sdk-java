package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Attachment {
    @XmlElement(name = "EmbeddedDocumentBinaryObject")
    private EmbeddedDocument embeddedDocumentBinaryObject;
    @XmlElement(name = "ExternalReference")
    private ExternalReference externalReference;

    private Attachment() {
        // No args constructor for JAXB
    }

    public static class Builder {
        private EmbeddedDocument embeddedDocumentBinaryObject;
        private ExternalReference externalReference;

        public Builder withEmbeddedDocumentBinaryObject(EmbeddedDocument embeddedDocumentBinaryObject) {
            this.embeddedDocumentBinaryObject = embeddedDocumentBinaryObject;
            return this;
        }

        public Builder withExternalReference(ExternalReference externalReference) {
            this.externalReference = externalReference;
            return this;
        }

        public Attachment build() {
            Attachment attachment = new Attachment();
            attachment.embeddedDocumentBinaryObject = this.embeddedDocumentBinaryObject;
            attachment.externalReference = this.externalReference;
            return attachment;
        }
    }

    public EmbeddedDocument getEmbeddedDocumentBinaryObject() {
        return embeddedDocumentBinaryObject;
    }

    public ExternalReference getExternalReference() {
        return externalReference;
    }
}
