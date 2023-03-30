package com.girigiri.kwrental.equipment.dto.response;

import com.girigiri.kwrental.equipment.domain.RentalQuantity;

public record RentalQuantityResponse(
        int totalQuantity,
        int remainingQuantity
) {

    public static RentalQuantityResponse from(final RentalQuantity rentalQuantity) {
        return new RentalQuantityResponse(rentalQuantity.getTotalQuantity(), rentalQuantity.getRemainQuantity());
    }
}
