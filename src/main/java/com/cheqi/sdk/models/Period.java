package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a billing or service period.
 * 
 * <p>Use this to specify the time range covered by a receipt, such as:</p>
 * <ul>
 *   <li>Subscription periods (e.g., monthly SaaS from Jan 1 - Jan 31)</li>
 *   <li>Rental periods (e.g., car rental from Dec 1 - Dec 7)</li>
 *   <li>Utility billing periods (e.g., electricity from Nov 1 - Nov 30)</li>
 *   <li>Service contracts (e.g., maintenance from Q1 2024)</li>
 * </ul>
 * 
 * <h3>Mandatory Fields:</h3>
 * <ul>
 *   <li><strong>startDate</strong>: The start date of the period</li>
 *   <li><strong>endDate</strong>: The end date of the period</li>
 * </ul>
 * 
 * <h3>Optional Fields:</h3>
 * <ul>
 *   <li><strong>description</strong>: Human-readable description of the period</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Period.Builder.class)
public final class Period {

    // ===== MANDATORY FIELDS =====

    /**
     * The start date of the period (local date, no timezone).
     * Example: "2024-01-01"
     */
    @JsonProperty("startDate")
    private final LocalDate startDate;

    /**
     * The end date of the period (local date, no timezone).
     * Example: "2024-01-31"
     */
    @JsonProperty("endDate")
    private final LocalDate endDate;

    // ===== OPTIONAL FIELDS =====

    /**
     * Optional human-readable description of the period.
     * Examples: "Monthly subscription - January 2024", "Q1 2024 service period"
     */
    @JsonProperty("description")
    private final Optional<String> description;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private Period(
            LocalDate startDate,
            LocalDate endDate,
            Optional<String> description,
            Map<String, Object> additionalProperties) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public LocalDate getStartDate() {
        return startDate;
    }

    @JsonIgnore
    public LocalDate getEndDate() {
        return endDate;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The description if provided
     */
    @JsonIgnore
    public Optional<String> getDescription() {
        return description != null ? description : Optional.empty();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Period && equalTo((Period) other);
    }

    private boolean equalTo(Period other) {
        return Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.endDate, this.description);
    }

    @Override
    public String toString() {
        return "Period{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", description=" + description +
                '}';
    }

    public static Period.Builder builder() {
        return new Period.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private LocalDate startDate;
        private LocalDate endDate;
        private Optional<String> description = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Period.Builder from(Period other) {
            startDate(other.getStartDate());
            endDate(other.getEndDate());
            description(other.getDescription().orElse(null));
            return this;
        }

        @JsonSetter(value = "startDate", nulls = Nulls.SKIP)
        public Period.Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        @JsonSetter(value = "endDate", nulls = Nulls.SKIP)
        public Period.Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        @JsonSetter(value = "description", nulls = Nulls.SKIP)
        public Period.Builder description(String description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public Period build() {
            return new Period(startDate, endDate, description, additionalProperties);
        }
    }

    // ===== VALIDATION METHODS =====

    /**
     * Validates this period.
     * @return true if both start and end dates are present and start is before or equal to end
     */
    @JsonIgnore
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Gets detailed validation errors for this period.
     * @return List of validation error messages, empty if valid
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (startDate == null) {
            errors.add("Start date is required");
        }
        if (endDate == null) {
            errors.add("End date is required");
        }
        
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            errors.add("Start date must be before or equal to end date");
        }

        return errors;
    }
}
