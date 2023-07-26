package com.girigiri.kwrental.rental.dto.response;

import java.time.LocalDate;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import lombok.Builder;

@Builder
public record LabRoomReservationResponse(
    Long reservationId,
    Long reservationSpecId,
    String status,
    LocalDate startDate,
    LocalDate endDate,
    String renterName,
    RentalSpecStatus reason) {

    public LabRoomReservationResponse(final Long reservationId, final Long reservationSpecId,
        final LocalDate startDate, final LocalDate endDate, final String renterName, final RentalSpecStatus reason) {
        this(reservationId, reservationSpecId, reason.isNormalReturned() ? "정상 반납" : "불량 반납", startDate, endDate,
            renterName, reason);
    }
}
