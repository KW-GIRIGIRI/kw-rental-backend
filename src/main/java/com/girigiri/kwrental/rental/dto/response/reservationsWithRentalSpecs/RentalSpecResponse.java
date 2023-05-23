package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RentalSpecResponse {

    private Long rentalSpecId;
    private String propertyNumber;

    private RentalSpecResponse() {
    }

    @Builder
    public RentalSpecResponse(final Long rentalSpecId, final String propertyNumber) {
        this.rentalSpecId = rentalSpecId;
        this.propertyNumber = propertyNumber;
    }

    public static RentalSpecResponse from(final EquipmentRentalSpec equipmentRentalSpec) {
        return RentalSpecResponse.builder()
                .rentalSpecId(equipmentRentalSpec.getId())
                .propertyNumber(equipmentRentalSpec.getPropertyNumber())
                .build();
    }
}
