package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class ItemProperty {
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Identifier id;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    private Name name;
    @XmlElement(name = "NameCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code nameCode;
    @XmlElement(name = "TestMethod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String testMethod;
    @XmlElement(name = "Value", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String value;
    @XmlElement(name = "ValueQuantity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Quantity valueQuantity;
    @XmlElement(name = "ValueQualifier", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> valueQualifier;
    @XmlElement(name = "ImportanceCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code importanceCode;
    @XmlElement(name = "ListValue", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> listValue;
    @XmlElement(name = "UsabilityPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Period usabilityPeriod;
    @XmlElement(name = "ItemPropertyGroup", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemPropertyGroup> itemPropertyGroup;
    @XmlElement(name = "RangeDimension", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Dimension rangeDimension;
    @XmlElement(name = "ItemPropertyRange", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemPropertyRange itemPropertyRange;
    @XmlElement(name = "StandardPropertyIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private PropertyIdentification standardPropertyIdentification;

    public ItemProperty() {
    }

    private ItemProperty(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.nameCode = builder.nameCode;
        this.testMethod = builder.testMethod;
        this.value = builder.value;
        this.valueQuantity = builder.valueQuantity;
        this.valueQualifier = builder.valueQualifier;
        this.importanceCode = builder.importanceCode;
        this.listValue = builder.listValue;
        this.usabilityPeriod = builder.usabilityPeriod;
        this.itemPropertyGroup = builder.itemPropertyGroup;
        this.rangeDimension = builder.rangeDimension;
        this.itemPropertyRange = builder.itemPropertyRange;
        this.standardPropertyIdentification = builder.standardPropertyIdentification;
    }

    public static class Builder {
        private Identifier id;
        private final Name name;
        private Code nameCode;
        private String testMethod;
        private String value;
        private Quantity valueQuantity;
        private List<String> valueQualifier;
        private Code importanceCode;
        private List<String> listValue;
        private Period usabilityPeriod;
        private List<ItemPropertyGroup> itemPropertyGroup;
        private Dimension rangeDimension;
        private ItemPropertyRange itemPropertyRange;
        private PropertyIdentification standardPropertyIdentification;

        public Builder(Name name) {
            if (name == null) {
                throw new IllegalArgumentException("Name is mandatory and cannot be null or empty");
            }
            this.name = name;
        }

        public Builder id(Identifier id) {
            this.id = id;
            return this;
        }

        public Builder nameCode(Code nameCode) {
            this.nameCode = nameCode;
            return this;
        }

        public Builder testMethod(String testMethod) {
            this.testMethod = testMethod;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder valueQuantity(Quantity valueQuantity) {
            this.valueQuantity = valueQuantity;
            return this;
        }

        public Builder valueQualifier(List<String> valueQualifier) {
            this.valueQualifier = valueQualifier;
            return this;
        }

        public Builder importanceCode(Code importanceCode) {
            this.importanceCode = importanceCode;
            return this;
        }

        public Builder listValue(List<String> listValue) {
            this.listValue = listValue;
            return this;
        }

        public Builder usabilityPeriod(Period usabilityPeriod) {
            this.usabilityPeriod = usabilityPeriod;
            return this;
        }

        public Builder itemPropertyGroup(List<ItemPropertyGroup> itemPropertyGroup) {
            this.itemPropertyGroup = itemPropertyGroup;
            return this;
        }

        public Builder rangeDimension(Dimension rangeDimension) {
            this.rangeDimension = rangeDimension;
            return this;
        }

        public Builder itemPropertyRange(ItemPropertyRange itemPropertyRange) {
            this.itemPropertyRange = itemPropertyRange;
            return this;
        }

        public Builder standardPropertyIdentification(PropertyIdentification standardPropertyIdentification) {
            this.standardPropertyIdentification = standardPropertyIdentification;
            return this;
        }

        public ItemProperty build() {
            return new ItemProperty(this);
        }
    }

    public static Builder builder(Name name) {
        return new Builder(name);
    }

    // Getters
    public Identifier getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Code getNameCode() {
        return nameCode;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public String getValue() {
        return value;
    }

    public Quantity getValueQuantity() {
        return valueQuantity;
    }

    public List<String> getValueQualifier() {
        return valueQualifier;
    }

    public Code getImportanceCode() {
        return importanceCode;
    }

    public List<String> getListValue() {
        return listValue;
    }

    public Period getUsabilityPeriod() {
        return usabilityPeriod;
    }

    public List<ItemPropertyGroup> getItemPropertyGroup() {
        return itemPropertyGroup;
    }

    public Dimension getRangeDimension() {
        return rangeDimension;
    }

    public ItemPropertyRange getItemPropertyRange() {
        return itemPropertyRange;
    }

    public PropertyIdentification getStandardPropertyIdentification() {
        return standardPropertyIdentification;
    }

    @Override
    public String toString() {
        return "ItemProperty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nameCode='" + nameCode + '\'' +
                ", testMethod='" + testMethod + '\'' +
                ", value='" + value + '\'' +
                ", valueQuantity=" + valueQuantity +
                ", valueQualifier=" + valueQualifier +
                ", importanceCode='" + importanceCode + '\'' +
                ", listValue=" + listValue +
                ", usabilityPeriod=" + usabilityPeriod +
                ", itemPropertyGroup=" + itemPropertyGroup +
                ", rangeDimension=" + rangeDimension +
                ", itemPropertyRange=" + itemPropertyRange +
                ", standardPropertyIdentification=" + standardPropertyIdentification +
                '}';
    }
}
