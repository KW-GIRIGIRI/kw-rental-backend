package com.girigiri.kwrental.rental.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class RentalSpecsRequest {

    private Long reservationSpecId;
    private List<String> propertyNumbers;

    private RentalSpecsRequest() {
    }

    public RentalSpecsRequest(final Long reservationSpecId, final List<String> propertyNumbers) {
        this.reservationSpecId = reservationSpecId;
        this.propertyNumbers = propertyNumbers;
    }
}
