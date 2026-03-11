package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ItemPropertyGroup {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private String id;

    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String name;

    @XmlElement(name = "ImportanceCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String importanceCode;

    public ItemPropertyGroup() {
    }

    private ItemPropertyGroup(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.importanceCode = builder.importanceCode;
    }

    public static class Builder {
        private final String id;
        private String name;
        private String importanceCode;

        public Builder(String id) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID is mandatory and cannot be null or empty");
            }
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder importanceCode(String importanceCode) {
            this.importanceCode = importanceCode;
            return this;
        }

        public ItemPropertyGroup build() {
            return new ItemPropertyGroup(this);
        }
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImportanceCode() {
        return importanceCode;
    }

    @Override
    public String toString() {
        return "ItemPropertyGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", importanceCode='" + importanceCode + '\'' +
                '}';
    }
}
