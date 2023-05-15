package com.girigiri.kwrental.reservation.domain;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
public class OperatingPeriod {
    private final LocalDate start;
    private final LocalDate end;

    public OperatingPeriod(final LocalDate start, final LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public Set<LocalDate> getRentalAvailableDates() {
        final Set<LocalDate> availableDates = new HashSet<>();
        for (LocalDate i = start; i.isBefore(end) || i.equals(end); i = i.plusDays(1)) {
            if (isRentalAvailable(i)) availableDates.add(i);
        }
        return availableDates;
    }

    private boolean isRentalAvailable(final LocalDate date) {
        final DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.equals(DayOfWeek.MONDAY) || dayOfWeek.equals(DayOfWeek.TUESDAY)
                || dayOfWeek.equals(DayOfWeek.WEDNESDAY) || dayOfWeek.equals(DayOfWeek.THURSDAY);
    }
}
