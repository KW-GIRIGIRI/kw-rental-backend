package com.girigiri.kwrental.inventory.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

import com.girigiri.kwrental.inventory.exception.RentalDateException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
        if (rentalStartDate.equals(rentalEndDate)) {
            throw new RentalDateException("대여일자와 반납일자가 동일할 수 없습니다.");
        }
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
    }

    public Integer getRentalDays() {
        int rentalDays = 0;
        for (LocalDate date = rentalStartDate; date.isBefore(rentalEndDate); date = date.plusDays(1)) {
            if (isNotSupportedDayOfWeek(date)) {
                continue;
            }
            rentalDays++;
        }
        return rentalDays;
    }

    public static boolean isNotSupportedDayOfWeek(final LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.FRIDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    public boolean contains(final LocalDate date) {
        if (date == null) return false;
        return date.isEqual(rentalStartDate) || (date.isAfter(getRentalStartDate()) && date.isBefore(getRentalEndDate()));
    }

    public boolean isLegalReturnIn(final LocalDate date) {
        return this.contains(date) || rentalEndDate.isEqual(date);
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
