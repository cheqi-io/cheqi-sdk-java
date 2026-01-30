package com.cheqi.sdk.models.ubl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
public class Item {

    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String description;

    @XmlElement(name = "PackQuantity", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Quantity packQuantity;

    @XmlElement(name = "PackSizeNumeric", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Double packSizeNumeric;

    @XmlElement(name = "CatalogueIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean catalogueIndicator;

    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private String name;

    @XmlElement(name = "ItemTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Code itemTypeCode;

    @XmlElement(name = "HazardousRiskIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Boolean hazardousRiskIndicator;

    @XmlElement(name = "AdditionalInformation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> additionalInformation;

    @XmlElement(name = "Keyword", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> keyword;

    @XmlElement(name = "BrandName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private Set<String> brandName;

    @XmlElement(name = "ModelName", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> modelName;

    @XmlElement(name = "WarrantyInformation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private List<String> warrantyInformation;

    @XmlElement(name = "BuyersItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemIdentification buyersItemIdentification;

    @XmlElement(name = "SellersItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemIdentification sellersItemIdentification;

    @XmlElement(name = "ManufacturersItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemIdentification manufacturersItemIdentification;

    @XmlElement(name = "StandardItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemIdentification standardItemIdentification;

    @XmlElement(name = "CatalogueItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private ItemIdentification catalogueItemIdentification;

    @XmlElement(name = "AdditionalItemIdentification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemIdentification> additionalItemIdentification;

    @XmlElement(name = "CatalogueDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private DocumentReference catalogueDocumentReference;

    @XmlElement(name = "ItemSpecificationDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<DocumentReference> itemSpecificationDocumentReference;

    @XmlElement(name = "OriginCountry", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Country originCountry;

    @XmlElement(name = "CommodityClassification", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<CommodityClassification> commodityClassification;

    @XmlElement(name = "TransactionConditions", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<TransactionConditions> transactionConditions;

    @XmlElement(name = "ClassifiedTaxCategory")
    private List<TaxCategory> classifiedTaxCategory;

    @XmlElement(name = "AdditionalItemProperty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemProperty> additionalItemProperty;

    @XmlElement(name = "ManufacturerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Party> manufacturerParty;

    @XmlElement(name = "InformationContentProviderParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private Party informationContentProviderParty;

    @XmlElement(name = "OriginAddress", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Address> originAddress;

    @XmlElement(name = "ItemInstance", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<ItemInstance> itemInstance;

    @XmlElement(name = "Certificate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Certificate> certificate;

    @XmlElement(name = "Dimension", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<Dimension> dimension;

    @XmlElement(name = "EnvironmentalEmission", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    private List<EnvironmentalEmission> environmentalEmission;

    public Item() {
    }

    private Item(Builder builder) {
        this.description = builder.description;
        this.packQuantity = builder.packQuantity;
        this.packSizeNumeric = builder.packSizeNumeric;
        this.catalogueIndicator = builder.catalogueIndicator;
        this.name = builder.name;
        this.itemTypeCode = builder.itemTypeCode;
        this.hazardousRiskIndicator = builder.hazardousRiskIndicator;
        this.additionalInformation = builder.additionalInformation;
        this.keyword = builder.keyword;
        this.brandName = builder.brandName;
        this.modelName = builder.modelName;
        this.warrantyInformation = builder.warrantyInformation;
        this.buyersItemIdentification = builder.buyersItemIdentification;
        this.sellersItemIdentification = builder.sellersItemIdentification;
        this.manufacturersItemIdentification = builder.manufacturersItemIdentification;
        this.standardItemIdentification = builder.standardItemIdentification;
        this.catalogueItemIdentification = builder.catalogueItemIdentification;
        this.additionalItemIdentification = builder.additionalItemIdentification;
        this.catalogueDocumentReference = builder.catalogueDocumentReference;
        this.itemSpecificationDocumentReference = builder.itemSpecificationDocumentReference;
        this.originCountry = builder.originCountry;
        this.commodityClassification = builder.commodityClassification;
        this.transactionConditions = builder.transactionConditions;
        this.classifiedTaxCategory = builder.classifiedTaxCategory;
        this.additionalItemProperty = builder.additionalItemProperty;
        this.manufacturerParty = builder.manufacturerParty;
        this.informationContentProviderParty = builder.informationContentProviderParty;
        this.originAddress = builder.originAddress;
        this.itemInstance = builder.itemInstance;
        this.certificate = builder.certificate;
        this.dimension = builder.dimension;
        this.environmentalEmission = builder.environmentalEmission;
    }

    public static class Builder {
        private String description;
        private Quantity packQuantity;
        private Double packSizeNumeric;
        private Boolean catalogueIndicator;
        private String name;
        private Code itemTypeCode;
        private Boolean hazardousRiskIndicator;
        private List<String> additionalInformation;
        private List<String> keyword;
        private Set<String> brandName;
        private List<String> modelName;
        private List<String> warrantyInformation;
        private ItemIdentification buyersItemIdentification;
        private ItemIdentification sellersItemIdentification;
        private ItemIdentification manufacturersItemIdentification;
        private ItemIdentification standardItemIdentification;
        private ItemIdentification catalogueItemIdentification;
        private List<ItemIdentification> additionalItemIdentification;
        private DocumentReference catalogueDocumentReference;
        private List<DocumentReference> itemSpecificationDocumentReference;
        private Country originCountry;
        private List<CommodityClassification> commodityClassification;
        private List<TransactionConditions> transactionConditions;
        private List<TaxCategory> classifiedTaxCategory;
        private List<ItemProperty> additionalItemProperty;
        private List<Party> manufacturerParty;
        private Party informationContentProviderParty;
        private List<Address> originAddress;
        private List<ItemInstance> itemInstance;
        private List<Certificate> certificate;
        private List<Dimension> dimension;
        private List<EnvironmentalEmission> environmentalEmission;

        public Builder() {
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder packQuantity(Quantity packQuantity) {
            this.packQuantity = packQuantity;
            return this;
        }

        public Builder packSizeNumeric(Double packSizeNumeric) {
            this.packSizeNumeric = packSizeNumeric;
            return this;
        }

        public Builder catalogueIndicator(Boolean catalogueIndicator) {
            this.catalogueIndicator = catalogueIndicator;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder itemTypeCode(Code itemTypeCode) {
            this.itemTypeCode = itemTypeCode;
            return this;
        }

        public Builder hazardousRiskIndicator(Boolean hazardousRiskIndicator) {
            this.hazardousRiskIndicator = hazardousRiskIndicator;
            return this;
        }

        public Builder additionalInformation(List<String> additionalInformation) {
            this.additionalInformation = additionalInformation;
            return this;
        }

        public Builder keyword(List<String> keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder brandName(Set<String> brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder modelName(List<String> modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder warrantyInformation(List<String> warrantyInformation) {
            this.warrantyInformation = warrantyInformation;
            return this;
        }

        public Builder buyersItemIdentification(ItemIdentification buyersItemIdentification) {
            this.buyersItemIdentification = buyersItemIdentification;
            return this;
        }

        public Builder sellersItemIdentification(ItemIdentification sellersItemIdentification) {
            this.sellersItemIdentification = sellersItemIdentification;
            return this;
        }

        public Builder manufacturersItemIdentification(ItemIdentification manufacturersItemIdentification) {
            this.manufacturersItemIdentification = manufacturersItemIdentification;
            return this;
        }

        public Builder standardItemIdentification(ItemIdentification standardItemIdentification) {
            this.standardItemIdentification = standardItemIdentification;
            return this;
        }

        public Builder catalogueItemIdentification(ItemIdentification catalogueItemIdentification) {
            this.catalogueItemIdentification = catalogueItemIdentification;
            return this;
        }

        public Builder additionalItemIdentification(List<ItemIdentification> additionalItemIdentification) {
            this.additionalItemIdentification = additionalItemIdentification;
            return this;
        }

        public Builder catalogueDocumentReference(DocumentReference catalogueDocumentReference) {
            this.catalogueDocumentReference = catalogueDocumentReference;
            return this;
        }

        public Builder itemSpecificationDocumentReference(List<DocumentReference> itemSpecificationDocumentReference) {
            this.itemSpecificationDocumentReference = itemSpecificationDocumentReference;
            return this;
        }

        public Builder originCountry(Country originCountry) {
            this.originCountry = originCountry;
            return this;
        }

        public Builder commodityClassification(List<CommodityClassification> commodityClassification) {
            this.commodityClassification = commodityClassification;
            return this;
        }

        public Builder transactionConditions(List<TransactionConditions> transactionConditions) {
            this.transactionConditions = transactionConditions;
            return this;
        }

        public Builder classifiedTaxCategory(List<TaxCategory> classifiedTaxCategory) {
            this.classifiedTaxCategory = classifiedTaxCategory;
            return this;
        }

        public Builder additionalItemProperty(List<ItemProperty> additionalItemProperty) {
            this.additionalItemProperty = additionalItemProperty;
            return this;
        }

        public Builder manufacturerParty(List<Party> manufacturerParty) {
            this.manufacturerParty = manufacturerParty;
            return this;
        }

        public Builder informationContentProviderParty(Party informationContentProviderParty) {
            this.informationContentProviderParty = informationContentProviderParty;
            return this;
        }

        public Builder originAddress(List<Address> originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder itemInstance(List<ItemInstance> itemInstance) {
            this.itemInstance = itemInstance;
            return this;
        }

        public Builder certificate(List<Certificate> certificate) {
            this.certificate = certificate;
            return this;
        }

        public Builder dimension(List<Dimension> dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder environmentalEmission(List<EnvironmentalEmission> environmentalEmission) {
            this.environmentalEmission = environmentalEmission;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public Quantity getPackQuantity() {
        return packQuantity;
    }

    public Double getPackSizeNumeric() {
        return packSizeNumeric;
    }

    public Boolean isCatalogueIndicator() {
        return catalogueIndicator;
    }

    public String getName() {
        return name;
    }

    public Code getItemTypeCode() {
        return itemTypeCode;
    }

    public Boolean isHazardousRiskIndicator() {
        return hazardousRiskIndicator;
    }

    public List<String> getAdditionalInformation() {
        return additionalInformation;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public Set<String> getBrandName() {
        return brandName;
    }

    public List<String> getModelName() {
        return modelName;
    }

    public List<String> getWarrantyInformation() {
        return warrantyInformation;
    }

    public ItemIdentification getBuyersItemIdentification() {
        return buyersItemIdentification;
    }

    public ItemIdentification getSellersItemIdentification() {
        return sellersItemIdentification;
    }

    public ItemIdentification getManufacturersItemIdentification() {
        return manufacturersItemIdentification;
    }

    public ItemIdentification getStandardItemIdentification() {
        return standardItemIdentification;
    }

    public ItemIdentification getCatalogueItemIdentification() {
        return catalogueItemIdentification;
    }

    public List<ItemIdentification> getAdditionalItemIdentification() {
        return additionalItemIdentification;
    }

    public DocumentReference getCatalogueDocumentReference() {
        return catalogueDocumentReference;
    }

    public List<DocumentReference> getItemSpecificationDocumentReference() {
        return itemSpecificationDocumentReference;
    }

    public Country getOriginCountry() {
        return originCountry;
    }

    public List<CommodityClassification> getCommodityClassification() {
        return commodityClassification;
    }

    public List<TransactionConditions> getTransactionConditions() {
        return transactionConditions;
    }

    public List<TaxCategory> getClassifiedTaxCategory() {
        return classifiedTaxCategory;
    }

    public List<ItemProperty> getAdditionalItemProperty() {
        return additionalItemProperty;
    }

    public List<Party> getManufacturerParty() {
        return manufacturerParty;
    }

    public Party getInformationContentProviderParty() {
        return informationContentProviderParty;
    }

    public List<Address> getOriginAddress() {
        return originAddress;
    }

    public List<ItemInstance> getItemInstance() {
        return itemInstance;
    }

    public List<Certificate> getCertificate() {
        return certificate;
    }

    public List<Dimension> getDimension() {
        return dimension;
    }

    public List<EnvironmentalEmission> getEnvironmentalEmission() {
        return environmentalEmission;
    }
}
