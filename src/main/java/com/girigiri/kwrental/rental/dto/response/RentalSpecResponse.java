package com.girigiri.kwrental.rental.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RentalSpecResponse {

    @JsonIgnore
    private Long reservationSpecId;

    private Long rentalSpecId;
    private String propertyNumber;

    private RentalSpecResponse() {
    }

    @Builder
    public RentalSpecResponse(final Long reservationSpecId, final Long rentalSpecId, final String propertyNumber) {
        this.reservationSpecId = reservationSpecId;
        this.rentalSpecId = rentalSpecId;
        this.propertyNumber = propertyNumber;
    }
}
