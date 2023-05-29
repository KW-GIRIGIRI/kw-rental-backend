package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LabRoomRentalDto {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String name;
    private final Integer amount;
    private final RentalSpecStatus rentalSpecStatus;

    public LabRoomRentalDto(final LocalDate startDate, final LocalDate endDate, final String name, final Integer amount, final RentalSpecStatus rentalSpecStatus) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.amount = amount;
        this.rentalSpecStatus = rentalSpecStatus;
    }
}
