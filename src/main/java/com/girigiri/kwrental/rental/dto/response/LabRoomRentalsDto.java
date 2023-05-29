package com.girigiri.kwrental.rental.dto.response;

import lombok.Getter;

import java.util.Set;

@Getter
public class LabRoomRentalsDto {

    private Set<LabRoomRentalDto> rentals;

    private LabRoomRentalsDto() {
    }

    public LabRoomRentalsDto(final Set<LabRoomRentalDto> rentals) {
        this.rentals = rentals;
    }
}
