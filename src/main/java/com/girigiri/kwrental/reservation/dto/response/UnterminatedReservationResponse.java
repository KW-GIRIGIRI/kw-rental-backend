package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class UnterminatedReservationResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<UnterminatedReservationSpecResponse> reservationSpecs;

    private UnterminatedReservationResponse() {
    }

    private UnterminatedReservationResponse(final LocalDate startDate, final LocalDate endDate, final List<UnterminatedReservationSpecResponse> reservationSpecs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationSpecs = reservationSpecs;
    }

    public static UnterminatedReservationResponse from(final Reservation unterminatedReservation) {
        final List<UnterminatedReservationSpecResponse> reservationSpecResponses = unterminatedReservation.getReservationSpecs().stream()
                .map(UnterminatedReservationSpecResponse::from)
                .toList();
        return new UnterminatedReservationResponse(unterminatedReservation.getStartDate(), unterminatedReservation.getEndDate(), reservationSpecResponses);
    }
}
