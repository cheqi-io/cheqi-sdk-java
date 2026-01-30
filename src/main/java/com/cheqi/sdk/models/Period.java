package com.cheqi.sdk.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.*;
import java.util.*;

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
 * <h3>Easy Usage Examples:</h3>
 * <pre>
 * // Simple date-only period (uses LocalDate - easiest!)
 * Period monthlyPeriod = Period.builder()
 *     .startDate(LocalDate.of(2024, 1, 1))
 *     .endDate(LocalDate.of(2024, 1, 31))
 *     .description("January 2024")
 *     .build();
 * 
 * // With specific times (uses LocalDateTime)
 * Period rentalPeriod = Period.builder()
 *     .startDate(LocalDateTime.of(2024, 12, 1, 14, 0))  // 2:00 PM
 *     .endDate(LocalDateTime.of(2024, 12, 1, 17, 0))    // 5:00 PM
 *     .description("3-hour rental")
 *     .build();
 * 
 * // With timezone support
 * Period eventPeriod = Period.builder()
 *     .startDate(LocalDateTime.of(2024, 6, 15, 19, 0), ZoneId.of("Europe/Amsterdam"))
 *     .endDate(LocalDateTime.of(2024, 6, 15, 22, 0), ZoneId.of("Europe/Amsterdam"))
 *     .description("Concert")
 *     .build();
 * </pre>
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
     * The start date/time of the period.
     * Uses ISO-8601 format with timezone (e.g., "2024-01-01T00:00:00Z").
     * For date-only periods, use midnight UTC (e.g., "2024-01-01T00:00:00Z").
     */
    @JsonProperty("startDate")
    private final Instant startDate;

    /**
     * The end date/time of the period.
     * Uses ISO-8601 format with timezone (e.g., "2024-01-31T23:59:59Z").
     * For date-only periods, use end of day UTC (e.g., "2024-01-31T23:59:59Z").
     */
    @JsonProperty("endDate")
    private final Instant endDate;

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
            Instant startDate,
            Instant endDate,
            Optional<String> description,
            Map<String, Object> additionalProperties) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.additionalProperties = additionalProperties;
    }

    // ===== MANDATORY FIELD ACCESSORS =====

    @JsonIgnore
    public Instant getStartDate() {
        return startDate;
    }

    @JsonIgnore
    public Instant getEndDate() {
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
        private Instant startDate;
        private Instant endDate;
        private Optional<String> description = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Period.Builder from(Period other) {
            startDate(other.getStartDate());
            endDate(other.getEndDate());
            description(other.getDescription().orElse(null));
            return this;
        }

        /**
         * Sets the start date/time of the period.
         * @param startDate Start timestamp
         * @return This builder
         */
        @JsonSetter(value = "startDate", nulls = Nulls.SKIP)
        public Period.Builder startDate(Instant startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Sets the start date (at midnight UTC).
         * Convenient method for date-only periods.
         * 
         * @param startDate Start date (will be converted to midnight UTC)
         * @return This builder
         */
        public Period.Builder startDate(LocalDate startDate) {
            this.startDate = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            return this;
        }

        /**
         * Sets the start date/time in UTC.
         * 
         * @param startDateTime Start date/time (will be converted to UTC)
         * @return This builder
         */
        public Period.Builder startDate(LocalDateTime startDateTime) {
            this.startDate = startDateTime.atZone(ZoneOffset.UTC).toInstant();
            return this;
        }

        /**
         * Sets the start date/time with a specific timezone.
         * 
         * @param startDateTime Start date/time
         * @param zoneId Timezone (e.g., ZoneId.of("Europe/Amsterdam"))
         * @return This builder
         */
        public Period.Builder startDate(LocalDateTime startDateTime, ZoneId zoneId) {
            this.startDate = startDateTime.atZone(zoneId).toInstant();
            return this;
        }

        /**
         * Sets the end date/time of the period.
         * @param endDate End timestamp
         * @return This builder
         */
        @JsonSetter(value = "endDate", nulls = Nulls.SKIP)
        public Period.Builder endDate(Instant endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Sets the end date (at end of day - 23:59:59 UTC).
         * Convenient method for date-only periods.
         * 
         * @param endDate End date (will be converted to 23:59:59 UTC)
         * @return This builder
         */
        public Period.Builder endDate(LocalDate endDate) {
            this.endDate = endDate.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();
            return this;
        }

        /**
         * Sets the end date/time in UTC.
         * 
         * @param endDateTime End date/time (will be converted to UTC)
         * @return This builder
         */
        public Period.Builder endDate(LocalDateTime endDateTime) {
            this.endDate = endDateTime.atZone(ZoneOffset.UTC).toInstant();
            return this;
        }

        /**
         * Sets the end date/time with a specific timezone.
         * 
         * @param endDateTime End date/time
         * @param zoneId Timezone (e.g., ZoneId.of("Europe/Amsterdam"))
         * @return This builder
         */
        public Period.Builder endDate(LocalDateTime endDateTime, ZoneId zoneId) {
            this.endDate = endDateTime.atZone(zoneId).toInstant();
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
     * @return true if both start and end timestamps are present and start is before or equal to end
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
            errors.add("Start date/time is required");
        }
        if (endDate == null) {
            errors.add("End date/time is required");
        }
        
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            errors.add("Start date/time must be before or equal to end date/time");
        }

        return errors;
    }
}
