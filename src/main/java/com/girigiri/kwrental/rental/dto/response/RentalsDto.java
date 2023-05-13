package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import lombok.Getter;

import java.util.List;

@Getter
public class RentalsDto {

    private List<RentalDto> rentals;

    private RentalsDto() {
    }

    public RentalsDto(final List<RentalDto> rentals) {
        this.rentals = rentals;
    }
}
