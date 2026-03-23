package com.cheqi.sdk.models;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Represents a billing or service period.
 * Extends the generated UBL Period with convenient date conversion methods.
 *
 * <pre>
 * Period monthlyPeriod = Period.builder()
 *     .startDate(LocalDate.of(2024, 1, 1))
 *     .endDate(LocalDate.of(2024, 1, 31))
 *     .description("January 2024")
 *     .build();
 *
 * Period rentalPeriod = Period.builder()
 *     .startDate(LocalDateTime.of(2024, 12, 1, 14, 0))
 *     .endDate(LocalDateTime.of(2024, 12, 1, 17, 0))
 *     .description("3-hour rental")
 *     .build();
 * </pre>
 */
public class Period extends com.cheqi.sdk.models.generated.Period {

    public Period() {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String startDate;
        private String startTime;
        private String endDate;
        private String endTime;
        private String description;

        private Builder() {}

        public Builder from(com.cheqi.sdk.models.generated.Period other) {
            this.startDate = other.getStartDate();
            this.startTime = other.getStartTime();
            this.endDate = other.getEndDate();
            this.endTime = other.getEndTime();
            this.description = other.getDescription();
            return this;
        }

        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate.toString();
            return this;
        }

        public Builder startDate(LocalDateTime startDateTime) {
            this.startDate = startDateTime.toLocalDate().toString();
            this.startTime = startDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder startDate(LocalDateTime startDateTime, ZoneId zoneId) {
            Instant instant = startDateTime.atZone(zoneId).toInstant();
            this.startDate = startDateTime.toLocalDate().toString();
            this.startTime = startDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder startDate(Instant startDate) {
            OffsetDateTime odt = startDate.atOffset(ZoneOffset.UTC);
            this.startDate = odt.toLocalDate().toString();
            this.startTime = odt.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate.toString();
            return this;
        }

        public Builder endDate(LocalDateTime endDateTime) {
            this.endDate = endDateTime.toLocalDate().toString();
            this.endTime = endDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder endDate(LocalDateTime endDateTime, ZoneId zoneId) {
            this.endDate = endDateTime.toLocalDate().toString();
            this.endTime = endDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder endDate(Instant endDate) {
            OffsetDateTime odt = endDate.atOffset(ZoneOffset.UTC);
            this.endDate = odt.toLocalDate().toString();
            this.endTime = odt.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            return this;
        }

        public Builder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Period build() {
            Period p = new Period();
            p.setStartDate(startDate);
            p.setStartTime(startTime);
            p.setEndDate(endDate);
            p.setEndTime(endTime);
            p.setDescription(description);
            return p;
        }
    }
}
