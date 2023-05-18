package com.girigiri.kwrental.rental.repository.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RentalDto rentalDto = (RentalDto) o;
        return Objects.equals(startDate, rentalDto.startDate) && Objects.equals(endDate, rentalDto.endDate) && Objects.equals(rentalSpecs, rentalDto.rentalSpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, rentalSpecs);
    }
}
