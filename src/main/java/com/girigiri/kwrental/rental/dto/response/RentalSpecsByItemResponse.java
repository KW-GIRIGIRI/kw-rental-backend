package com.girigiri.kwrental.rental.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RentalSpecsByItemResponse {
    private List<RentalSpecByItemResponse> rentalSpecs;

    private RentalSpecsByItemResponse() {
    }

    private RentalSpecsByItemResponse(final List<RentalSpecByItemResponse> rentalSpecs) {
        this.rentalSpecs = rentalSpecs;
    }

    public static RentalSpecsByItemResponse from(final List<RentalSpecWithName> rentalSpecWithNames) {
        final List<RentalSpecByItemResponse> rentalSpecByItemResponses = rentalSpecWithNames.stream()
                .map(RentalSpecByItemResponse::from)
                .toList();
        return new RentalSpecsByItemResponse(rentalSpecByItemResponses);
    }
}
