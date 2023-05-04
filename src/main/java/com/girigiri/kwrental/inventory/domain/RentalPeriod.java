package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.inventory.exception.RentalDateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter
@EqualsAndHashCode
public class RentalPeriod {

    @Column(nullable = false)
    private LocalDate rentalStartDate;

    @Column(nullable = false)
    private LocalDate rentalEndDate;

    protected RentalPeriod() {
    }

    public RentalPeriod(LocalDate rentalStartDate, LocalDate rentalEndDate) {
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
}
