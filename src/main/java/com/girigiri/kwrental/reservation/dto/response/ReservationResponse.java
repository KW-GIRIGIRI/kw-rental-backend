package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationResponse {

    private String name;
    private String memberNumber;
    private List<ReservationSpecResponse> reservationSpecs;

    private ReservationResponse() {
    }

    private ReservationResponse(final String name, final String memberNumber, final List<ReservationSpecResponse> reservationSpecs) {
        this.name = name;
        this.memberNumber = memberNumber;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservationResponse from(final Reservation reservation) {
        final List<ReservationSpecResponse> reservationSpecResponses = reservation.getReservationSpecs().stream()
                .map(ReservationSpecResponse::from)
                .toList();
        // TODO: 2023/04/26 학번을 제대로 적용해줘야 함
        return new ReservationResponse(reservation.getName(), "11111111", reservationSpecResponses);
    }
}
