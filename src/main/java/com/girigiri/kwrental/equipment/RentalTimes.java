package com.girigiri.kwrental.equipment;

import com.girigiri.kwrental.equipment.exception.EquipmentException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalTime;
import lombok.Getter;

@Getter
@Embeddable
public class RentalTimes {
    @Column(nullable = false)
    private LocalTime availableRentalFrom;

    @Column(nullable = false)
    private LocalTime availableRentalTo;

    protected RentalTimes() {
    }

    public RentalTimes(final LocalTime from, final LocalTime to) {
        validateAvailableTime(from, to);
        this.availableRentalFrom = from;
        this.availableRentalTo = to;
    }

    private static void validateAvailableTime(final LocalTime from, final LocalTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new EquipmentException("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안됩니다.");
        }
    }
}
