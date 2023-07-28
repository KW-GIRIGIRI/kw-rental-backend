package com.girigiri.kwrental.rental.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public record LabRoomRentalsDto(
    Set<LabRoomRentalDto> rentals) {
    public record LabRoomRentalDto(
        LocalDate startDate,
        LocalDate endDate,
        String name,
        Integer amount,
        RentalSpecStatus rentalSpecStatus) {

    }
}
