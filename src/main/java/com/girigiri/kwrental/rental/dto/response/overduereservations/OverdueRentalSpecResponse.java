package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import lombok.Getter;

@Getter
public class OverdueRentalSpecResponse {
    private Long rentalSpecId;
    private String propertyNumber;

    private OverdueRentalSpecResponse() {
    }

    private OverdueRentalSpecResponse(final Long rentalSpecId, final String propertyNumber) {
        this.rentalSpecId = rentalSpecId;
        this.propertyNumber = propertyNumber;
    }

    public static OverdueRentalSpecResponse from(final EquipmentRentalSpec equipmentRentalSpec) {
        return new OverdueRentalSpecResponse(equipmentRentalSpec.getId(), equipmentRentalSpec.getPropertyNumber());
    }
}
