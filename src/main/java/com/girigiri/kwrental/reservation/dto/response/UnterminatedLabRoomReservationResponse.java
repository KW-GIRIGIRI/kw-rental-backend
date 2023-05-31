package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UnterminatedLabRoomReservationResponse {

    private Long reservationId;
    private Long reservationSpecId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private Integer amount;
    private ReservationSpecStatus status;

    private UnterminatedLabRoomReservationResponse() {
    }

    private UnterminatedLabRoomReservationResponse(final Long reservationId, final Long reservationSpecId, final LocalDate startDate, final LocalDate endDate, final String name, final Integer amount, final ReservationSpecStatus status) {
        this.reservationId = reservationId;
        this.reservationSpecId = reservationSpecId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.amount = amount;
        this.status = status;
    }

    public static UnterminatedLabRoomReservationResponse from(final Reservation reservation) {
        final ReservationSpec spec = reservation.getReservationSpecs().iterator().next();
        return new UnterminatedLabRoomReservationResponse(reservation.getId(), spec.getId(), spec.getStartDate(), spec.getEndDate(), spec.getRentable().getName(), spec.getAmount().getAmount(), spec.getStatus());
    }
}
