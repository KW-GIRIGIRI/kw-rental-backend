package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class UnterminatedLabRoomReservationsResponse {

    private List<UnterminatedLabRoomReservationResponse> reservations;

    private UnterminatedLabRoomReservationsResponse() {
    }

    private UnterminatedLabRoomReservationsResponse(final List<UnterminatedLabRoomReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static UnterminatedLabRoomReservationsResponse from(final List<Reservation> reservations) {
        final List<UnterminatedLabRoomReservationResponse> unterminatedLabRoomReservationResponses = reservations.stream()
                .map(UnterminatedLabRoomReservationResponse::from)
                .toList();
        return new UnterminatedLabRoomReservationsResponse(unterminatedLabRoomReservationResponses);
    }
}
