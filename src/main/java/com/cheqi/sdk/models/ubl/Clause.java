package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Clause {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Content", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> Content;

    public Clause() {
    }

    private Clause(Builder builder) {
        this.id = builder.id;
        this.Content = builder.Content;
    }

    public static class Builder {
        private Identifier id;
        private List<String> Content;

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder Content(List<String> Content) {
            this.Content = Content;
            return this;
        }

        public Clause build() {
            return new Clause(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public List<String> getContent() {
        return Content;
    }
}
