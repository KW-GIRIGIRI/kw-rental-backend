package com.girigiri.kwrental.rental.repository.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RentalDto {

    private final Long reservationId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<RentalSpecDto> rentalSpecs;

    public RentalDto(final Long reservationId, final LocalDate startDate, final LocalDate endDate, final Collection<RentalSpecDto> rentalSpecs) {
        this.reservationId = reservationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalSpecs = new HashSet<>(rentalSpecs);
    }
}
