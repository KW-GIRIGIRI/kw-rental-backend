package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RentalSpecByStartDateResponse {

    private Long rentalSpecId;
    private String propertyNumber;

    private RentalSpecByStartDateResponse() {
    }

    @Builder
    public RentalSpecByStartDateResponse(final Long rentalSpecId, final String propertyNumber) {
        this.rentalSpecId = rentalSpecId;
        this.propertyNumber = propertyNumber;
    }

    public static RentalSpecByStartDateResponse from(final RentalSpec rentalSpec) {
        return RentalSpecByStartDateResponse.builder()
                .rentalSpecId(rentalSpec.getId())
                .propertyNumber(rentalSpec.getPropertyNumber())
                .build();
    }
}
