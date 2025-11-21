package com.cheqi.sdk.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Period DTO representing time periods with flexible start/end/duration definitions.
 *
 * <h3>All Fields Are Optional:</h3>
 * <p>Based on UBL specification, all fields are optional, but certain combinations are required for validity:</p>
 * <ul>
 *   <li><strong>startDate</strong>: The date on which this period begins</li>
 *   <li><strong>startTime</strong>: The time at which this period begins</li>
 *   <li><strong>endDate</strong>: The date on which this period ends</li>
 *   <li><strong>endTime</strong>: The time at which this period ends</li>
 *   <li><strong>durationMeasure</strong>: ISO 8601 duration code</li>
 *   <li><strong>descriptionCode</strong>: Period description codes</li>
 *   <li><strong>description</strong>: Period description text</li>
 * </ul>
 *
 * <h3>Business Rules:</h3>
 * <p>A valid period must have one of:</p>
 * <ul>
 *   <li>Start and end dates/times</li>
 *   <li>Start date/time and duration</li>
 *   <li>Just a duration</li>
 * </ul>
 *
 * <h3>Duration Examples:</h3>
 * <ul>
 *   <li>"P1Y" - 1 year</li>
 *   <li>"P1M" - 1 month</li>
 *   <li>"P1W" - 1 week</li>
 *   <li>"P1D" - 1 day</li>
 *   <li>"PT1H" - 1 hour</li>
 * </ul>
 *
 * <h3>Description Code Examples:</h3>
 * <ul>
 *   <li>"MONTHLY", "YEARLY", "WEEKLY", "DAILY"</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Period.Builder.class)
public final class Period {

    // ===== OPTIONAL FIELDS =====

    /**
     * The date on which this period begins.
     * UBL: Period. Start Date. Date (0..1)
     */
    @JsonProperty("startDate")
    private final Optional<Instant> startDate;

    /**
     * The time at which this period begins.
     * UBL: Period. Start Time. Time (0..1)
     */
    @JsonProperty("startTime")
    private final Optional<Instant> startTime;

    /**
     * The date on which this period ends.
     * UBL: Period. End Date. Date (0..1)
     */
    @JsonProperty("endDate")
    private final Optional<Instant> endDate;

    /**
     * The time at which this period ends.
     * UBL: Period. End Time. Time (0..1)
     */
    @JsonProperty("endTime")
    private final Optional<Instant> endTime;

    /**
     * The duration of this period, expressed as an ISO 8601 code.
     * UBL: Period. Duration. Measure (0..1)
     * Examples: "P1Y" (1 year), "P1M" (1 month), "P1W" (1 week), "P1D" (1 day), "PT1H" (1 hour)
     */
    @JsonProperty("durationMeasure")
    private final Optional<String> durationMeasure;

    /**
     * A description of this period, expressed as a code.
     * UBL: Period. Description Code. Code (0..n)
     * Examples: "MONTHLY", "YEARLY", "WEEKLY", "DAILY"
     */
    @JsonProperty("descriptionCode")
    private final Optional<List<String>> descriptionCode;

    /**
     * A description of this period, expressed as text.
     * UBL: Period. Description. Text (0..n)
     * Examples: "Monthly subscription period", "Annual billing cycle"
     */
    @JsonProperty("description")
    private final Optional<List<String>> description;

    private final Map<String, Object> additionalProperties;

    // ===== CONSTRUCTOR =====

    private Period(
            Optional<Instant> startDate,
            Optional<Instant> startTime,
            Optional<Instant> endDate,
            Optional<Instant> endTime,
            Optional<String> durationMeasure,
            Optional<List<String>> descriptionCode,
            Optional<List<String>> description,
            Map<String, Object> additionalProperties) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.durationMeasure = durationMeasure;
        this.descriptionCode = descriptionCode;
        this.description = description;
        this.additionalProperties = additionalProperties;
    }

    // ===== OPTIONAL FIELD ACCESSORS =====

    /**
     * @return The start date if provided
     */
    @JsonIgnore
    public Optional<Instant> getStartDate() {
        return startDate != null ? startDate : Optional.empty();
    }

    /**
     * @return The start time if provided
     */
    @JsonIgnore
    public Optional<Instant> getStartTime() {
        return startTime != null ? startTime : Optional.empty();
    }

    /**
     * @return The end date if provided
     */
    @JsonIgnore
    public Optional<Instant> getEndDate() {
        return endDate != null ? endDate : Optional.empty();
    }

    /**
     * @return The end time if provided
     */
    @JsonIgnore
    public Optional<Instant> getEndTime() {
        return endTime != null ? endTime : Optional.empty();
    }

    /**
     * @return The duration measure if provided
     */
    @JsonIgnore
    public Optional<String> getDurationMeasure() {
        return durationMeasure != null ? durationMeasure : Optional.empty();
    }

    /**
     * @return The description codes if provided
     */
    @JsonIgnore
    public Optional<List<String>> getDescriptionCode() {
        return descriptionCode != null ? descriptionCode : Optional.empty();
    }

    /**
     * @return The descriptions if provided
     */
    @JsonIgnore
    public Optional<List<String>> getDescription() {
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
                && Objects.equals(this.startTime, other.startTime)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.endTime, other.endTime)
                && Objects.equals(this.durationMeasure, other.durationMeasure)
                && Objects.equals(this.descriptionCode, other.descriptionCode)
                && Objects.equals(this.description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.startTime, this.endDate,
                this.endTime, this.durationMeasure, this.descriptionCode, this.description);
    }

    @Override
    public String toString() {
        return "Period{" +
                "startDate=" + startDate +
                ", startTime=" + startTime +
                ", endDate=" + endDate +
                ", endTime=" + endTime +
                ", durationMeasure=" + durationMeasure +
                ", descriptionCode=" + descriptionCode +
                ", description=" + description +
                '}';
    }

    public static Period.Builder builder() {
        return new Period.Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Optional<Instant> startDate = Optional.empty();
        private Optional<Instant> startTime = Optional.empty();
        private Optional<Instant> endDate = Optional.empty();
        private Optional<Instant> endTime = Optional.empty();
        private Optional<String> durationMeasure = Optional.empty();
        private Optional<List<String>> descriptionCode = Optional.empty();
        private Optional<List<String>> description = Optional.empty();
        private Map<String, Object> additionalProperties = new HashMap<>();

        private Builder() {}

        public Period.Builder from(Period other) {
            startDate(other.getStartDate().orElse(null));
            startTime(other.getStartTime().orElse(null));
            endDate(other.getEndDate().orElse(null));
            endTime(other.getEndTime().orElse(null));
            durationMeasure(other.getDurationMeasure().orElse(null));
            descriptionCode(other.getDescriptionCode().orElse(null));
            description(other.getDescription().orElse(null));
            return this;
        }

        @JsonSetter(value = "startDate", nulls = Nulls.SKIP)
        public Period.Builder startDate(Instant startDate) {
            this.startDate = Optional.ofNullable(startDate);
            return this;
        }

        public Period.Builder startDate(Optional<Instant> startDate) {
            this.startDate = startDate != null ? startDate : Optional.empty();
            return this;
        }

        @JsonSetter(value = "startTime", nulls = Nulls.SKIP)
        public Period.Builder startTime(Instant startTime) {
            this.startTime = Optional.ofNullable(startTime);
            return this;
        }

        public Period.Builder startTime(Optional<Instant> startTime) {
            this.startTime = startTime != null ? startTime : Optional.empty();
            return this;
        }

        @JsonSetter(value = "endDate", nulls = Nulls.SKIP)
        public Period.Builder endDate(Instant endDate) {
            this.endDate = Optional.ofNullable(endDate);
            return this;
        }

        public Period.Builder endDate(Optional<Instant> endDate) {
            this.endDate = endDate != null ? endDate : Optional.empty();
            return this;
        }

        @JsonSetter(value = "endTime", nulls = Nulls.SKIP)
        public Period.Builder endTime(Instant endTime) {
            this.endTime = Optional.ofNullable(endTime);
            return this;
        }

        public Period.Builder endTime(Optional<Instant> endTime) {
            this.endTime = endTime != null ? endTime : Optional.empty();
            return this;
        }

        @JsonSetter(value = "durationMeasure", nulls = Nulls.SKIP)
        public Period.Builder durationMeasure(String durationMeasure) {
            this.durationMeasure = Optional.ofNullable(durationMeasure);
            return this;
        }

        public Period.Builder durationMeasure(Optional<String> durationMeasure) {
            this.durationMeasure = durationMeasure != null ? durationMeasure : Optional.empty();
            return this;
        }

        @JsonSetter(value = "descriptionCode", nulls = Nulls.SKIP)
        public Period.Builder descriptionCode(List<String> descriptionCode) {
            this.descriptionCode = Optional.ofNullable(descriptionCode);
            return this;
        }

        public Period.Builder descriptionCode(Optional<List<String>> descriptionCode) {
            this.descriptionCode = descriptionCode != null ? descriptionCode : Optional.empty();
            return this;
        }

        @JsonSetter(value = "description", nulls = Nulls.SKIP)
        public Period.Builder description(List<String> description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public Period.Builder description(Optional<List<String>> description) {
            this.description = description != null ? description : Optional.empty();
            return this;
        }

        public Period build() {
            return new Period(startDate, startTime, endDate, endTime,
                    durationMeasure, descriptionCode, description, additionalProperties);
        }
    }

    // ===== CONVENIENCE METHODS =====

    /**
     * Convenience method to check if this period has a defined start.
     */
    @JsonIgnore
    public boolean hasStart() {
        return startDate.isPresent() || startTime.isPresent();
    }

    /**
     * Convenience method to check if this period has a defined end.
     */
    @JsonIgnore
    public boolean hasEnd() {
        return endDate.isPresent() || endTime.isPresent();
    }

    /**
     * Convenience method to check if this period has a duration.
     */
    @JsonIgnore
    public boolean hasDuration() {
        return durationMeasure.isPresent() && !durationMeasure.get().trim().isEmpty();
    }

    /**
     * Convenience method to check if this period is valid.
     * A period is valid if it has either:
     * 1. Start and end dates/times, or
     * 2. Start date/time and duration, or
     * 3. Just a duration
     */
    @JsonIgnore
    public boolean isValid() {
        return (hasStart() && hasEnd()) ||
                (hasStart() && hasDuration()) ||
                hasDuration();
    }

    /**
     * Convenience method to get validation errors.
     * Returns a list of error messages if the period is invalid.
     */
    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (!isValid()) {
            if (!hasStart() && !hasEnd() && !hasDuration()) {
                errors.add("Period must have either start and end dates/times, start date/time and duration, or just a duration");
            }
            if (hasStart() && !hasEnd() && !hasDuration()) {
                errors.add("Period must have either end dates/times or duration if start date/time is provided");
            }
            if (hasEnd() && !hasStart() && !hasDuration()) {
                errors.add("Period must have either start dates/times or duration if end date/time is provided");
            }
        }

        return errors;
    }
}
