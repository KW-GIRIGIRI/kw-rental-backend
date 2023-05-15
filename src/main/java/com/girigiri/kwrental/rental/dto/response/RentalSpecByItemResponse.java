package com.girigiri.kwrental.rental.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RentalSpecByItemResponse {

    private String status;
    private LocalDate acceptDate;
    private LocalDate returnDate;
    private String name;
    private String reason;

    private RentalSpecByItemResponse() {
    }

    public RentalSpecByItemResponse(final String status, final LocalDate acceptDate, final LocalDate returnDate, final String name, final String reason) {
        this.status = status;
        this.acceptDate = acceptDate;
        this.returnDate = returnDate;
        this.name = name;
        this.reason = reason;
    }

    public static RentalSpecByItemResponse from(final RentalSpecWithName rentalSpecWithName) {
        return RentalSpecByItemResponse.builder()
                .status(rentalSpecWithName.getStatus().isAbnormalReturned() ? "불량 반납" : "정상 반납")
                .acceptDate(LocalDate.from(rentalSpecWithName.getAcceptDateTime()))
                .returnDate(LocalDate.from(rentalSpecWithName.getReturnDateTime()))
                .name(rentalSpecWithName.getName())
                .reason(rentalSpecWithName.getStatus().name())
                .build();
    }
}
