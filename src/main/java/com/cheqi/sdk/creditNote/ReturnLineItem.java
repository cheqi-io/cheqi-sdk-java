package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * A requested return quantity and reason for a merchant-supplied product identifier.
 * Multiple entries may use the same product ID when different quantities have different reasons.
 */
public class ReturnLineItem {
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("quantity")
    private BigDecimal quantity;
    @JsonProperty("reasonCode")
    private ReturnReasonCode reasonCode;
    @JsonProperty("reasonDescription")
    private String reasonDescription;

    public ReturnLineItem() {
    }

    public ReturnLineItem(
            String productId,
            BigDecimal quantity,
            ReturnReasonCode reasonCode,
            String reasonDescription
    ) {
        this.productId = productId;
        this.quantity = quantity;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public void validate() {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("lineItems.productId is required");
        }
        if (productId.length() > 255) {
            throw new IllegalArgumentException("lineItems.productId must be at most 255 characters");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("lineItems.quantity must be greater than zero");
        }
        if (reasonCode == null) {
            throw new IllegalArgumentException("lineItems.reasonCode is required");
        }
        if (reasonDescription != null && reasonDescription.length() > 1000) {
            throw new IllegalArgumentException("lineItems.reasonDescription must be at most 1000 characters");
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ReturnReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReturnReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }
}
