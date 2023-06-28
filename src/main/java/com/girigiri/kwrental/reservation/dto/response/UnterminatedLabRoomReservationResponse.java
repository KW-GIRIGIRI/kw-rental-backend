package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDate;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;

public record UnterminatedLabRoomReservationResponse(

    Long reservationId,
    Long reservationSpecId,
    LocalDate startDate,
    LocalDate endDate,
    String name,
    Integer amount,
    ReservationSpecStatus status
) {
    public static UnterminatedLabRoomReservationResponse from(final Reservation reservation) {
        final ReservationSpec spec = reservation.getReservationSpecs().iterator().next();
        return new UnterminatedLabRoomReservationResponse(reservation.getId(), spec.getId(), spec.getStartDate(),
            spec.getEndDate(), spec.getRentable().getName(), spec.getAmount().getAmount(), spec.getStatus());
    }
}
