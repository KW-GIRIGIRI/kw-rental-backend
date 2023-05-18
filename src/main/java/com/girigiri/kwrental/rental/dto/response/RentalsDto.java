package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import lombok.Getter;

import java.util.Set;

@Getter
public class RentalsDto {

    private Set<RentalDto> rentals;

    private RentalsDto() {
    }

    public RentalsDto(final Set<RentalDto> rentals) {
        this.rentals = rentals;
    }
}
