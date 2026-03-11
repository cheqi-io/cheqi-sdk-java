package com.cheqi.sdk.creditNote;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ReturnLineItem {
    @JsonProperty("lineItemId")
    private String lineItemId;
    @JsonProperty("quantityReturned")
    private BigDecimal quantityReturned;
    @JsonProperty("condition")
    private ReturnCondition condition;
    @JsonProperty("conditionNotes")
    private String conditionNotes;

    public ReturnLineItem() {
    }

    public ReturnLineItem(String lineItemId, BigDecimal quantityReturned, 
                         ReturnCondition condition, String conditionNotes) {
        this.lineItemId = lineItemId;
        this.quantityReturned = quantityReturned;
        this.condition = condition;
        this.conditionNotes = conditionNotes;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }

    public BigDecimal getQuantityReturned() {
        return quantityReturned;
    }

    public void setQuantityReturned(BigDecimal quantityReturned) {
        this.quantityReturned = quantityReturned;
    }

    public ReturnCondition getCondition() {
        return condition;
    }

    public void setCondition(ReturnCondition condition) {
        this.condition = condition;
    }

    public String getConditionNotes() {
        return conditionNotes;
    }

    public void setConditionNotes(String conditionNotes) {
        this.conditionNotes = conditionNotes;
    }
}