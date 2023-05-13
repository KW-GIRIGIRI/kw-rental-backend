package com.girigiri.kwrental.rental.repository.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RentalDto {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<RentalSpecDto> rentalSpecs;

    public RentalDto(final LocalDate startDate, final LocalDate endDate, final Collection<RentalSpecDto> rentalSpecs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalSpecs = new HashSet<>(rentalSpecs);
    }
}
