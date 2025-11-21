package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Product DTO representing a product line item in a receipt.
 *
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>productName</strong>: Name of the product</li>
 *   <li><strong>brandName</strong>: Brand name(s) of the product</li>
 *   <li><strong>articleNumber</strong>: Unique product identifier</li>
 *   <li><strong>invoicedQuantity</strong>: Quantity included in invoice</li>
 *   <li><strong>listPrice</strong>: Price per unit</li>
 *   <li><strong>lineSubtotal</strong>: Subtotal before taxes</li>
 *   <li><strong>lineTotal</strong>: Total amount charged for this line</li>
 * </ul>
 *
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>productDescription</strong>: Product description</li>
 *   <li><strong>productPeriod</strong>: Subscription or service period</li>
 *   <li><strong>currency</strong>: Currency for pricing</li>
 *   <li><strong>taxRates</strong>: Applied tax rates</li>
 *   <li><strong>allowanceCharges</strong>: Discounts or extra charges</li>
 *   <li><strong>itemClassifications</strong>: Product classifications</li>
 *   <li><strong>note</strong>: Additional seller notes</li>
 *   <li><strong>totalAllowanceCharges</strong>: Sum of allowance charges</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Product.Builder.class)
public final class Product {

    // ===== MANDATORY FIELDS =====

    /**
     * The name of the product.
     */
    @JsonProperty("productName")
    private final String productName;

    /**
     * The brand name(s) of the product.
     */
    @JsonProperty("brandName")
    private final Set<String> brandName;

    /**
     * The unique identifier of the product.
     */
    @JsonProperty("articleNumber")
    private final String articleNumber;

    /**
     * Quantity of the product that is included in the invoice.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNECERec20/">List of codes</a>
     */
    @JsonProperty("invoicedQuantity")
    private final Quantity invoicedQuantity;

    /**
     * List price of the product per unit.
     */
    @JsonProperty("listPrice")
    private final ItemPrice listPrice;

    /**
     * The subtotal before taxes.
     */
    @JsonProperty("lineSubtotal")
    private final BigDecimal lineSubtotal;

    /**
     * The total amount that is charged to the customer for this line.
     * This price should be lineSubtotal + taxes.
     */
    @JsonProperty("lineTotal")
    private final BigDecimal lineTotal;

    // ===== OPTIONAL FIELDS =====

    /**
     * The description of the product.
     */
    private final Optional<String> productDescription;

    /**
     * The period of the product, for example the subscription period.
     */
    private final Optional<Period> productPeriod;

    /**
     * Currency for the product.
     */
    private final Optional<String> currency;

    /**
     * The list of tax rates that are applied to the product.
     * For now UBL doesn't support multiple taxes on a single product, so this list should contain only one tax rate.
     */
    private final Optional<List<TaxRate>> taxRates;

    /**
     * Allowance charges for the product, these are for examples discounts or extra charges.
     */
    private final Optional<List<AllowanceCharge>> allowanceCharges;

    /**
     * The list of item classifications that are assigned to the product.
     *
     * @see <a href="https://docs.peppol.eu/poacc/billing/3.0/codelist/UNCL7143/">Peppol list of codes</a>
     */
    private final Optional<Set<ItemClassification>> itemClassifications;

    /**
     * Additional information that is provided by the seller.
     */
    private final Optional<List<String>> note;

    /**
     * The total sum of the allowance charges that are applied to the product.
     */
    private final Optional<BigDecimal> totalAllowanceCharges;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private Product(
            String productName,
            Set<String> brandName,
            String articleNumber,
            Quantity invoicedQuantity,
            ItemPrice listPrice,
            BigDecimal lineSubtotal,
            BigDecimal lineTotal,
            Optional<String> productDescription,
            Optional<Period> productPeriod,
            Optional<String> currency,
            Optional<List<TaxRate>> taxRates,
            Optional<List<AllowanceCharge>> allowanceCharges,
            Optional<Set<ItemClassification>> itemClassifications,
            Optional<List<String>> note,
            Optional<BigDecimal> totalAllowanceCharges,
            Map<String, Object> additionalProperties) {
        this.productName = productName;
        this.brandName = brandName;
        this.articleNumber = articleNumber;
        this.invoicedQuantity = invoicedQuantity;
        this.listPrice = listPrice;
        this.lineSubtotal = lineSubtotal;
        this.lineTotal = lineTotal;
        this.productDescription = productDescription;
        this.productPeriod = productPeriod;
        this.currency = currency;
        this.taxRates = taxRates;
        this.allowanceCharges = allowanceCharges;
        this.itemClassifications = itemClassifications;
        this.note = note;
        this.totalAllowanceCharges = totalAllowanceCharges;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    /**
     * @return The name of the product
     */
    @JsonIgnore
    public String getProductName() {
        return productName;
    }

    /**
     * @return The brand name(s) of the product
     */
    @JsonIgnore
    public Set<String> getBrandName() {
        return brandName;
    }

    /**
     * @return The unique identifier of the product
     */
    @JsonIgnore
    public String getArticleNumber() {
        return articleNumber;
    }

    /**
     * @return Quantity of the product that is included in the invoice
     */
    @JsonIgnore
    public Quantity getInvoicedQuantity() {
        return invoicedQuantity;
    }

    /**
     * @return List price of the product per unit
     */
    @JsonIgnore
    public ItemPrice getListPrice() {
        return listPrice;
    }

    /**
     * @return The subtotal before taxes
     */
    @JsonIgnore
    public BigDecimal getLineSubtotal() {
        return lineSubtotal;
    }

    /**
     * @return The total amount that is charged to the customer for this line
     */
    @JsonIgnore
    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The product description if provided
     */
    @JsonIgnore
    public Optional<String> getProductDescription() {
        if (productDescription == null) {
            return Optional.empty();
        }
        return productDescription;
    }

    /**
     * @return The product period if provided
     */
    @JsonIgnore
    public Optional<Period> getProductPeriod() {
        if (productPeriod == null) {
            return Optional.empty();
        }
        return productPeriod;
    }

    /**
     * @return The currency if provided
     */
    @JsonIgnore
    public Optional<String> getCurrency() {
        if (currency == null) {
            return Optional.empty();
        }
        return currency;
    }

    /**
     * @return The tax rates if provided
     */
    @JsonIgnore
    public Optional<List<TaxRate>> getTaxRates() {
        if (taxRates == null) {
            return Optional.empty();
        }
        return taxRates;
    }

    /**
     * @return The allowance charges if provided
     */
    @JsonIgnore
    public Optional<List<AllowanceCharge>> getAllowanceCharges() {
        if (allowanceCharges == null) {
            return Optional.empty();
        }
        return allowanceCharges;
    }

    /**
     * @return The item classifications if provided
     */
    @JsonIgnore
    public Optional<Set<ItemClassification>> getItemClassifications() {
        if (itemClassifications == null) {
            return Optional.empty();
        }
        return itemClassifications;
    }

    /**
     * @return The seller notes if provided
     */
    @JsonIgnore
    public Optional<List<String>> getNote() {
        if (note == null) {
            return Optional.empty();
        }
        return note;
    }

    /**
     * @return The total allowance charges if provided
     */
    @JsonIgnore
    public Optional<BigDecimal> getTotalAllowanceCharges() {
        if (totalAllowanceCharges == null) {
            return Optional.empty();
        }
        return totalAllowanceCharges;
    }

    /**
     * @return Additional properties map
     */
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Product && equalTo((Product) other);
    }

    private boolean equalTo(Product other) {
        return Objects.equals(this.productName, other.productName)
                && Objects.equals(this.brandName, other.brandName)
                && Objects.equals(this.articleNumber, other.articleNumber)
                && Objects.equals(this.invoicedQuantity, other.invoicedQuantity)
                && Objects.equals(this.listPrice, other.listPrice)
                && Objects.equals(this.lineSubtotal, other.lineSubtotal)
                && Objects.equals(this.lineTotal, other.lineTotal)
                && Objects.equals(this.productDescription, other.productDescription)
                && Objects.equals(this.productPeriod, other.productPeriod)
                && Objects.equals(this.currency, other.currency)
                && Objects.equals(this.taxRates, other.taxRates)
                && Objects.equals(this.allowanceCharges, other.allowanceCharges)
                && Objects.equals(this.itemClassifications, other.itemClassifications)
                && Objects.equals(this.note, other.note)
                && Objects.equals(this.totalAllowanceCharges, other.totalAllowanceCharges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.productName,
                this.brandName,
                this.articleNumber,
                this.invoicedQuantity,
                this.listPrice,
                this.lineSubtotal,
                this.lineTotal,
                this.productDescription,
                this.productPeriod,
                this.currency,
                this.taxRates,
                this.allowanceCharges,
                this.itemClassifications,
                this.note,
                this.totalAllowanceCharges
        );
    }

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", brandName=" + brandName +
                ", articleNumber='" + articleNumber + '\'' +
                ", invoicedQuantity=" + invoicedQuantity +
                ", listPrice=" + listPrice +
                ", lineSubtotal=" + lineSubtotal +
                ", lineTotal=" + lineTotal +
                ", productDescription=" + productDescription +
                ", productPeriod=" + productPeriod +
                ", currency=" + currency +
                ", taxRates=" + taxRates +
                ", allowanceCharges=" + allowanceCharges +
                ", itemClassifications=" + itemClassifications +
                ", note=" + note +
                ", totalAllowanceCharges=" + totalAllowanceCharges +
                '}';
    }

    public static Product.Builder builder() {
        return new Product.Builder();
    }

    /**
     * Convenience method to get validation errors.
     * Returns a list of error messages if the product has invalid mandatory fields.
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        // Check mandatory fields
        if (productName == null || productName.trim().isEmpty()) {
            errors.add("Product name is required and cannot be blank");
        }

        if (brandName == null || brandName.isEmpty()) {
            errors.add("Brand name is required and cannot be empty");
        } else {
            // Check if any brand name in the set is null or blank
            int index = 0;
            for (String brand : brandName) {
                if (brand == null || brand.trim().isEmpty()) {
                    errors.add("Brand name at index " + index + " cannot be null or blank");
                }
                index++;
            }
        }

        if (articleNumber == null || articleNumber.trim().isEmpty()) {
            errors.add("Article number is required and cannot be blank");
        }

        if (invoicedQuantity == null) {
            errors.add("Invoiced quantity is required");
        }

        if (listPrice == null) {
            errors.add("List price is required");
        }

        if (lineSubtotal == null) {
            errors.add("Line subtotal is required");
        }

        if (lineTotal == null) {
            errors.add("Line total is required");
        }

        // Validate nested objects if present
        if (invoicedQuantity != null && invoicedQuantity instanceof Object) {
            // Note: Quantity would need its own getValidationErrors() method for full validation
        }

        if (listPrice != null && listPrice instanceof Object) {
            // Note: ItemPrice would need its own getValidationErrors() method for full validation
        }

        // Validate optional nested objects if present
        if (productPeriod != null && productPeriod.isPresent()) {
            Period period = productPeriod.get();
            if (period != null) {
                List<String> periodErrors = period.getValidationErrors();
                for (String error : periodErrors) {
                    errors.add("Product period: " + error);
                }
            }
        }

        if (allowanceCharges != null && allowanceCharges.isPresent()) {
            List<AllowanceCharge> charges = allowanceCharges.get();
            if (charges != null) {
                for (int i = 0; i < charges.size(); i++) {
                    AllowanceCharge charge = charges.get(i);
                    if (charge == null) {
                        errors.add("Allowance charge at index " + i + " cannot be null");
                    }
                    // Note: AllowanceCharge would need its own getValidationErrors() method for full validation
                }
            }
        }

        if (taxRates != null && taxRates.isPresent()) {
            List<TaxRate> rates = taxRates.get();
            if (rates != null) {
                for (int i = 0; i < rates.size(); i++) {
                    TaxRate rate = rates.get(i);
                    if (rate == null) {
                        errors.add("Tax rate at index " + i + " cannot be null");
                    }
                    // Note: TaxRate would need its own getValidationErrors() method for full validation
                }
            }
        }

        if (itemClassifications != null && itemClassifications.isPresent()) {
            Set<ItemClassification> classifications = itemClassifications.get();
            if (classifications != null) {
                for (ItemClassification classification : classifications) {
                    if (classification == null) {
                        errors.add("Item classification cannot be null");
                    }
                    // Note: ItemClassification would need its own getValidationErrors() method for full validation
                }
            }
        }

        return errors;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String productName;
        private Set<String> brandName;
        private String articleNumber;
        private Quantity invoicedQuantity;
        private ItemPrice listPrice;
        private BigDecimal lineSubtotal;
        private BigDecimal lineTotal;
        private Optional<String> productDescription = Optional.empty();
        private Optional<Period> productPeriod = Optional.empty();
        private Optional<String> currency = Optional.empty();
        private Optional<List<TaxRate>> taxRates = Optional.empty();
        private Optional<List<AllowanceCharge>> allowanceCharges = Optional.empty();
        private Optional<Set<ItemClassification>> itemClassifications = Optional.empty();
        private Optional<List<String>> note = Optional.empty();
        private Optional<BigDecimal> totalAllowanceCharges = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Product.Builder from(Product other) {
            productName(other.getProductName());
            brandName(other.getBrandName());
            articleNumber(other.getArticleNumber());
            invoicedQuantity(other.getInvoicedQuantity());
            listPrice(other.getListPrice());
            lineSubtotal(other.getLineSubtotal());
            lineTotal(other.getLineTotal());
            productDescription(other.getProductDescription());
            productPeriod(other.getProductPeriod());
            currency(other.getCurrency());
            taxRates(other.getTaxRates());
            allowanceCharges(other.getAllowanceCharges());
            itemClassifications(other.getItemClassifications());
            note(other.getNote());
            totalAllowanceCharges(other.getTotalAllowanceCharges());
            return this;
        }

        @JsonSetter(value = "productName", nulls = Nulls.SKIP)
        public Product.Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        @JsonSetter(value = "brandName", nulls = Nulls.SKIP)
        public Product.Builder brandName(Set<String> brandName) {
            this.brandName = brandName;
            return this;
        }

        @JsonSetter(value = "articleNumber", nulls = Nulls.SKIP)
        public Product.Builder articleNumber(String articleNumber) {
            this.articleNumber = articleNumber;
            return this;
        }

        @JsonSetter(value = "invoicedQuantity", nulls = Nulls.SKIP)
        public Product.Builder invoicedQuantity(Quantity invoicedQuantity) {
            this.invoicedQuantity = invoicedQuantity;
            return this;
        }

        @JsonSetter(value = "listPrice", nulls = Nulls.SKIP)
        public Product.Builder listPrice(ItemPrice listPrice) {
            this.listPrice = listPrice;
            return this;
        }

        @JsonSetter(value = "lineSubtotal", nulls = Nulls.SKIP)
        public Product.Builder lineSubtotal(BigDecimal lineSubtotal) {
            this.lineSubtotal = lineSubtotal;
            return this;
        }

        @JsonSetter(value = "lineTotal", nulls = Nulls.SKIP)
        public Product.Builder lineTotal(BigDecimal lineTotal) {
            this.lineTotal = lineTotal;
            return this;
        }

        @JsonSetter(value = "description", nulls = Nulls.SKIP)
        public Product.Builder productDescription(Optional<String> productDescription) {
            this.productDescription = productDescription;
            return this;
        }

        public Product.Builder productDescription(String productDescription) {
            this.productDescription = Optional.ofNullable(productDescription);
            return this;
        }

        @JsonSetter(value = "productPeriod", nulls = Nulls.SKIP)
        public Product.Builder productPeriod(Optional<Period> productPeriod) {
            this.productPeriod = productPeriod;
            return this;
        }

        public Product.Builder productPeriod(Period productPeriod) {
            this.productPeriod = Optional.ofNullable(productPeriod);
            return this;
        }

        @JsonSetter(value = "currency", nulls = Nulls.SKIP)
        public Product.Builder currency(Optional<String> currency) {
            this.currency = currency;
            return this;
        }

        public Product.Builder currency(String currency) {
            this.currency = Optional.ofNullable(currency);
            return this;
        }

        @JsonSetter(value = "taxRates", nulls = Nulls.SKIP)
        public Product.Builder taxRates(Optional<List<TaxRate>> taxRates) {
            this.taxRates = taxRates;
            return this;
        }

        public Product.Builder taxRates(List<TaxRate> taxRates) {
            this.taxRates = Optional.ofNullable(taxRates);
            return this;
        }

        @JsonSetter(value = "allowanceCharges", nulls = Nulls.SKIP)
        public Product.Builder allowanceCharges(Optional<List<AllowanceCharge>> allowanceCharges) {
            this.allowanceCharges = allowanceCharges;
            return this;
        }

        public Product.Builder allowanceCharges(List<AllowanceCharge> allowanceCharges) {
            this.allowanceCharges = Optional.ofNullable(allowanceCharges);
            return this;
        }

        @JsonSetter(value = "itemClassifications", nulls = Nulls.SKIP)
        public Product.Builder itemClassifications(Optional<Set<ItemClassification>> itemClassifications) {
            this.itemClassifications = itemClassifications;
            return this;
        }

        public Product.Builder itemClassifications(Set<ItemClassification> itemClassifications) {
            this.itemClassifications = Optional.ofNullable(itemClassifications);
            return this;
        }

        @JsonSetter(value = "note", nulls = Nulls.SKIP)
        public Product.Builder note(Optional<List<String>> note) {
            this.note = note;
            return this;
        }

        public Product.Builder note(List<String> note) {
            this.note = Optional.ofNullable(note);
            return this;
        }

        @JsonSetter(value = "totalAllowanceCharges", nulls = Nulls.SKIP)
        public Product.Builder totalAllowanceCharges(Optional<BigDecimal> totalAllowanceCharges) {
            this.totalAllowanceCharges = totalAllowanceCharges;
            return this;
        }

        public Product.Builder totalAllowanceCharges(BigDecimal totalAllowanceCharges) {
            this.totalAllowanceCharges = Optional.ofNullable(totalAllowanceCharges);
            return this;
        }

        public Product build() {
            return new Product(
                    productName,
                    brandName,
                    articleNumber,
                    invoicedQuantity,
                    listPrice,
                    lineSubtotal,
                    lineTotal,
                    productDescription,
                    productPeriod,
                    currency,
                    taxRates,
                    allowanceCharges,
                    itemClassifications,
                    note,
                    totalAllowanceCharges,
                    additionalProperties
            );
        }
    }

    // ===== PRIVATE JSON SERIALIZATION METHODS =====

    @JsonProperty("description")
    private Optional<String> _getProductDescription() {
        return productDescription;
    }

    @JsonProperty("productPeriod")
    private Optional<Period> _getProductPeriod() {
        return productPeriod;
    }

    @JsonProperty("currency")
    private Optional<String> _getCurrency() {
        return currency;
    }

    @JsonProperty("taxRates")
    private Optional<List<TaxRate>> _getTaxRates() {
        return taxRates;
    }

    @JsonProperty("allowanceCharges")
    private Optional<List<AllowanceCharge>> _getAllowanceCharges() {
        return allowanceCharges;
    }

    @JsonProperty("itemClassifications")
    private Optional<Set<ItemClassification>> _getItemClassifications() {
        return itemClassifications;
    }

    @JsonProperty("note")
    private Optional<List<String>> _getNote() {
        return note;
    }

    @JsonProperty("totalAllowanceCharges")
    private Optional<BigDecimal> _getTotalAllowanceCharges() {
        return totalAllowanceCharges;
    }
}