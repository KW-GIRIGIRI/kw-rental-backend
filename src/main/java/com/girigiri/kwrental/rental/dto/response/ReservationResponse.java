package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class ReservationResponse {

    private String name;
    private String memberNumber;
    private LocalDateTime acceptDateTime;
    private List<ReservationSpecResponse> reservationSpecs;

    private ReservationResponse() {
    }

    private ReservationResponse(final String name, final String memberNumber, final LocalDateTime acceptDateTime, final List<ReservationSpecResponse> reservationSpecs) {
        this.name = name;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservationResponse of(final Reservation reservation, final List<RentalSpecResponse> rentalSpecResponses) {
        final Map<Long, List<RentalSpecResponse>> groupedByReservationSpecId = rentalSpecResponses.stream()
                .collect(groupingBy(RentalSpecResponse::getReservationSpecId));
        final List<ReservationSpecResponse> reservationSpecResponses = reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecResponse.of(it, groupedByReservationSpecId.get(it.getId())))
                .toList();
        // TODO: 2023/04/26 학번을 제대로 적용해줘야 함
        return new ReservationResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecResponses);
    }

    public static ReservationResponse from(final Reservation reservation) {
        final List<ReservationSpecResponse> reservationSpecResponses = reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecResponse.of(it, null))
                .toList();
        return new ReservationResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecResponses);
    }
}
