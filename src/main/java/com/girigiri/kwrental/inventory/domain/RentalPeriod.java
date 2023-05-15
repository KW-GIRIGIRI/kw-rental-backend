package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.inventory.exception.RentalDateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@Getter
@EqualsAndHashCode
public class RentalPeriod implements Comparable<RentalPeriod> {

    @Column(nullable = false)
    private LocalDate rentalStartDate;

    @Column(nullable = false)
    private LocalDate rentalEndDate;

    protected RentalPeriod() {
    }

    public RentalPeriod(final RentalDateTime start, final RentalDateTime end) {
        this(start.toLocalDate(), end.toLocalDate());
    }

    public RentalPeriod(final LocalDate rentalStartDate, final LocalDate rentalEndDate) {
        if (rentalStartDate == null || rentalEndDate == null) {
            throw new RentalDateException("일자에 빈 값이 올 수 없습니다.");
        }
        if (rentalEndDate.isBefore(rentalStartDate)) {
            throw new RentalDateException("반납일자는 대여일자보다 이전일 수 없습니다.");
        }
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
    }


    public Integer getRentalDays() {
        return (int) rentalStartDate.until(rentalEndDate, ChronoUnit.DAYS);
    }

    public boolean contains(final LocalDate date) {
        if (date == null) return false;
        return date.isEqual(rentalStartDate) || (date.isAfter(getRentalStartDate()) && date.isBefore(getRentalEndDate()));
    }

    public boolean isLegalReturnIn(final LocalDate date) {
        return this.contains(date) || rentalEndDate.isEqual(date);
    }

    public Set<LocalDate> getRentalAvailableDates() {
        final Set<LocalDate> availableDates = new HashSet<>();
        for (LocalDate i = rentalStartDate; i.isBefore(rentalEndDate) || i.equals(rentalEndDate); i = i.plusDays(1)) {
            if (isRentalAvailable(i)) availableDates.add(i);
        }
        return availableDates;
    }

    private boolean isRentalAvailable(final LocalDate date) {
        final DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.equals(DayOfWeek.MONDAY) || dayOfWeek.equals(DayOfWeek.TUESDAY)
                || dayOfWeek.equals(DayOfWeek.WEDNESDAY) || dayOfWeek.equals(DayOfWeek.THURSDAY);
    }

    @Override
    public int compareTo(final RentalPeriod o) {
        if (o == null) return 1;
        if (this.rentalStartDate.isBefore(o.rentalStartDate)) {
            return -1;
        }
        if (this.rentalStartDate.isEqual(o.rentalStartDate) && this.rentalEndDate.isBefore(o.rentalEndDate)) {
            return -1;
        }
        if (this.rentalStartDate.isEqual(o.rentalStartDate) && this.rentalEndDate.isEqual(o.rentalEndDate)) {
            return 0;
        }
        return 1;
    }
}
