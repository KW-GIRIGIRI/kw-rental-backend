package com.girigiri.kwrental.equipment.dto;

import com.girigiri.kwrental.equipment.domain.RentalDays;
import lombok.Builder;

@Builder
public record RentalDaysResponse(
        Integer maxRentalDays,
        Integer maxDaysBeforeRental,
        Integer minDaysBeforeRental
) {

    public static RentalDaysResponse from(final RentalDays rentalDays) {
        return RentalDaysResponse.builder()
                .maxRentalDays(rentalDays.getMaxRentalDays())
                .maxDaysBeforeRental(rentalDays.getMaxDaysBeforeRental())
                .minDaysBeforeRental(rentalDays.getMinDaysBeforeRental())
                .build();
    }
}
