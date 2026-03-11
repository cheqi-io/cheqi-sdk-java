package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.*;

import java.util.Base64;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
public class EmbeddedDocument {
    @XmlAttribute
    private String mimeCode;
    @XmlAttribute
    private String filename;
    @XmlElement(name = "EmbeddedDocumentBinaryObject")
    private String embeddedDocumentBinaryObject;

    public EmbeddedDocument() {
        //No args constructor for JAXB
    }

    private EmbeddedDocument(Builder builder) {
        this.mimeCode = builder.mimeCode;
        this.filename = builder.filename;
        setEmbeddedDocumentBinaryObject(builder.embeddedDocumentBinaryObject);
    }

    public static class Builder {
        private String mimeCode;
        private String filename;
        private byte[] embeddedDocumentBinaryObject;

        public Builder(String mimeCode, String filename, byte[] embeddedDocumentBinaryObject) {
            if (mimeCode == null || filename == null || embeddedDocumentBinaryObject == null) {
                throw new IllegalArgumentException("mimeCode, filename and embeddedDocumentBinaryObject cannot be null");
            }
            this.mimeCode = mimeCode;
            this.filename = filename;
            this.embeddedDocumentBinaryObject = embeddedDocumentBinaryObject;
        }

        public EmbeddedDocument build() {
            return new EmbeddedDocument(this);
        }
    }

    public String getMimeCode() {
        return mimeCode;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getEmbeddedDocumentBinaryObject() {
        return Base64.getDecoder().decode(embeddedDocumentBinaryObject);
    }

    public void setEmbeddedDocumentBinaryObject(byte[] embeddedDocumentBinaryObject) {
        this.embeddedDocumentBinaryObject = Base64.getEncoder().encodeToString(embeddedDocumentBinaryObject);
    }
}
