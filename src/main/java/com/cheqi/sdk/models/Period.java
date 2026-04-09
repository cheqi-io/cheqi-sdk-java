package com.cheqi.sdk.models;

import java.time.*;

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
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private String description;

        private Builder() {}

        public Builder from(com.cheqi.sdk.models.generated.Period other) {
            this.startDate = other.getStartDate();
            this.endDate = other.getEndDate();
            this.description = other.getDescription();
            return this;
        }

        public Builder startDate(OffsetDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder startDate(LocalDateTime startDateTime) {
            this.startDate = startDateTime.atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder startDate(LocalDateTime startDateTime, ZoneId zoneId) {
            this.startDate = startDateTime.atZone(zoneId).toOffsetDateTime();
            return this;
        }

        public Builder startDate(Instant startDate) {
            this.startDate = startDate.atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder endDate(OffsetDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder endDate(LocalDateTime endDateTime) {
            this.endDate = endDateTime.atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder endDate(LocalDateTime endDateTime, ZoneId zoneId) {
            this.endDate = endDateTime.atZone(zoneId).toOffsetDateTime();
            return this;
        }

        public Builder endDate(Instant endDate) {
            this.endDate = endDate.atOffset(ZoneOffset.UTC);
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Period build() {
            Period p = new Period();
            p.setStartDate(startDate);
            p.setEndDate(endDate);
            p.setDescription(description);
            return p;
        }
    }
}
