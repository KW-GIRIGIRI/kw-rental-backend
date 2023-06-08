package com.girigiri.kwrental.rental.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class EquipmentRentalSpecsResponse {
    private List<EquipmentRentalSpecResponse> rentalSpecs;

    private EquipmentRentalSpecsResponse() {
    }

    private EquipmentRentalSpecsResponse(final List<EquipmentRentalSpecResponse> rentalSpecs) {
        this.rentalSpecs = rentalSpecs;
    }

    public static EquipmentRentalSpecsResponse from(final List<RentalSpecWithName> rentalSpecWithNames) {
        final List<EquipmentRentalSpecResponse> equipmentRentalSpecRespons = rentalSpecWithNames.stream()
            .map(EquipmentRentalSpecResponse::from)
            .toList();
        return new EquipmentRentalSpecsResponse(equipmentRentalSpecRespons);
    }
}
