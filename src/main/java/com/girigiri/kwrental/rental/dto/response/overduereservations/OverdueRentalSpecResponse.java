package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.rental.domain.RentalSpec;
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

    public static OverdueRentalSpecResponse from(final RentalSpec rentalSpec) {
        return new OverdueRentalSpecResponse(rentalSpec.getId(), rentalSpec.getPropertyNumber());
    }
}
