package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class ReservationByStartDateResponse {

    private String name;
    private String memberNumber;
    private LocalDateTime acceptDateTime;
    private List<ReservationSpecByStartDateResponse> reservationSpecs;

    private ReservationByStartDateResponse() {
    }

    private ReservationByStartDateResponse(final String name, final String memberNumber, final LocalDateTime acceptDateTime, final List<ReservationSpecByStartDateResponse> reservationSpecs) {
        this.name = name;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservationByStartDateResponse of(final Reservation reservation, final List<RentalSpec> rentalSpecs) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        final List<ReservationSpecByStartDateResponse> reservationSpecByStartDateResponses = reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecByStartDateResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
        // TODO: 2023/05/03 학번이 가짜
        return new ReservationByStartDateResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecByStartDateResponses);
    }

    public static ReservationByStartDateResponse from(final Reservation reservation) {
        final List<ReservationSpecByStartDateResponse> reservationSpecByStartDateRespons = reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecByStartDateResponse.of(it, null))
                .toList();
        return new ReservationByStartDateResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecByStartDateRespons);
    }
}
