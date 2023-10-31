package com.girigiri.kwrental.rental.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateEquipmentRentalRequest(@NotNull Long reservationId,
                                           @NotEmpty List<EquipmentRentalSpecsRequest> rentalSpecsRequests) {

    @Builder
    public record EquipmentRentalSpecsRequest(@NotNull Long reservationSpecId, @NotEmpty List<String> propertyNumbers) {
    }
}
