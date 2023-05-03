package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
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

    public static RentalSpecResponse from(final RentalSpec rentalSpec) {
        return RentalSpecResponse.builder()
                .rentalSpecId(rentalSpec.getId())
                .propertyNumber(rentalSpec.getPropertyNumber())
                .build();
    }
}
