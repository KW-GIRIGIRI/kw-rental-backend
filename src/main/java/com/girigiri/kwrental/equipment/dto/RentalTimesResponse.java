package com.girigiri.kwrental.equipment.dto;

import com.girigiri.kwrental.equipment.RentalTimes;
import java.time.LocalTime;

public record RentalTimesResponse(
        LocalTime availableRentalFrom,
        LocalTime availableRentalTo
) {

    public static RentalTimesResponse from(final RentalTimes rentalTimes) {
        return new RentalTimesResponse(rentalTimes.getAvailableRentalFrom(), rentalTimes.getAvailableRentalTo());
    }
}
