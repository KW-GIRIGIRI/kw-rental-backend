package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class UnterminatedEquipmentReservationResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<UnterminatedEquipmentReservationSpecResponse> reservationSpecs;

    private UnterminatedEquipmentReservationResponse() {
    }

    private UnterminatedEquipmentReservationResponse(final LocalDate startDate, final LocalDate endDate, final List<UnterminatedEquipmentReservationSpecResponse> reservationSpecs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationSpecs = reservationSpecs;
    }

    public static UnterminatedEquipmentReservationResponse from(final Reservation unterminatedReservation) {
        final List<UnterminatedEquipmentReservationSpecResponse> reservationSpecResponses = unterminatedReservation.getReservationSpecs().stream()
                .map(UnterminatedEquipmentReservationSpecResponse::from)
                .toList();
        return new UnterminatedEquipmentReservationResponse(unterminatedReservation.getStartDate(), unterminatedReservation.getEndDate(), reservationSpecResponses);
    }
}
