package com.girigiri.kwrental.equipment;

import com.girigiri.kwrental.equipment.exception.RentalDaysException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class RentalDays {

    @Column(nullable = false)
    private Integer maxRentalDays;

    @Column(nullable = false)
    private Integer maxDaysBeforeRental;

    @Column(nullable = false)
    private Integer minDaysBeforeRental;

    protected RentalDays() {
    }

    public RentalDays(final Integer maxRentalDays, final Integer maxDaysBeforeRental,
                      final Integer minDaysBeforeRental) {
        if (maxRentalDays <= 0) {
            throw new RentalDaysException("대여 가능 일 수는 항상 양수여야 합니다.");
        }

        if (maxDaysBeforeRental < 0) {
            throw new RentalDaysException("최대 대여 예약 가능 일 수가 음수면 안됩니다.");
        }

        if (minDaysBeforeRental < 0) {
            throw new RentalDaysException("최소 대여 예약 가능 일 수가 음수면 안됩니다.");
        }

        if (maxDaysBeforeRental.compareTo(minDaysBeforeRental) < 0) {
            throw new RentalDaysException("최대 대여 예약 가능 일 수가 최소 대여 예약 가능 일 수보다 작으면 안됩니다.");
        }

        this.maxRentalDays = maxRentalDays;
        this.maxDaysBeforeRental = maxDaysBeforeRental;
        this.minDaysBeforeRental = minDaysBeforeRental;
    }
}
