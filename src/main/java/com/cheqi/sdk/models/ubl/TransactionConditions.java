package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class TransactionConditions {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "ActionCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code actionCode;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> description;

    @XmlElement(name = "DocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<DocumentReference> documentReference;

    public TransactionConditions() {
    }

    private TransactionConditions(Builder builder) {
        this.id = builder.id;
        this.actionCode = builder.actionCode;
        this.description = builder.description;
        this.documentReference = builder.documentReference;
    }

    public static class Builder {
        private Identifier id;
        private Code actionCode;
        private List<String> description;
        private List<DocumentReference> documentReference;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder actionCode(Code actionCode) {
            this.actionCode = actionCode;
            return this;
        }

        public Builder description(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder addDescription(String desc) {
            if (this.description == null) {
                this.description = new ArrayList<>();
            }
            this.description.add(desc);
            return this;
        }

        public Builder documentReference(List<DocumentReference> documentReference) {
            this.documentReference = documentReference;
            return this;
        }

        public Builder addDocumentReference(DocumentReference docRef) {
            if (this.documentReference == null) {
                this.documentReference = new ArrayList<>();
            }
            this.documentReference.add(docRef);
            return this;
        }

        public TransactionConditions build() {
            return new TransactionConditions(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Code getActionCode() {
        return actionCode;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<DocumentReference> getDocumentReference() {
        return documentReference;
    }


    @Override
    public String toString() {
        return "TransactionConditions{" +
                "id='" + id + '\'' +
                ", actionCode='" + actionCode + '\'' +
                ", description=" + description +
                ", documentReference=" + documentReference +
                '}';
    }
}
