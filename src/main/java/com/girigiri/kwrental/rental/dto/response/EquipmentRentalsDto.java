package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.repository.dto.EquipmentRentalDto;
import lombok.Getter;

import java.util.Set;

@Getter
public class EquipmentRentalsDto {

    private Set<EquipmentRentalDto> rentals;

    private EquipmentRentalsDto() {
    }

    public EquipmentRentalsDto(final Set<EquipmentRentalDto> rentals) {
        this.rentals = rentals;
    }
}
