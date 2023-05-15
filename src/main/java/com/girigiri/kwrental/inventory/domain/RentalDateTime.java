package com.girigiri.kwrental.inventory.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Embeddable
public class RentalDateTime {

    private Instant instant;

    protected RentalDateTime() {
    }

    private RentalDateTime(final Instant instant) {
        this.instant = instant;
    }

    public static RentalDateTime now() {
        return new RentalDateTime(Instant.now());
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.ofInstant(this.instant, ZoneId.systemDefault());
    }

    public RentalDateTime calculateDay(final int days) {
        final LocalDate date = days < 0 ? toLocalDate().minusDays(days) : toLocalDate().plusDays(days);
        return RentalDateTime.from(date);
    }

    public static RentalDateTime from(final LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return RentalDateTime.from(localDateTime);
    }

    public static RentalDateTime from(final LocalDateTime localDateTime) {
        return new RentalDateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate toLocalDate() {
        return LocalDate.ofInstant(this.instant, ZoneId.systemDefault());
    }
}
