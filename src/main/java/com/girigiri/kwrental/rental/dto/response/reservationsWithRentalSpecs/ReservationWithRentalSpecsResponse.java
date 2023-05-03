package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class ReservationWithRentalSpecsResponse {

    private String name;
    private String memberNumber;
    private LocalDateTime acceptDateTime;
    private List<ReservationSpecWithRentalSpecsResponse> reservationSpecs;

    private ReservationWithRentalSpecsResponse() {
    }

    private ReservationWithRentalSpecsResponse(final String name, final String memberNumber, final LocalDateTime acceptDateTime, final List<ReservationSpecWithRentalSpecsResponse> reservationSpecs) {
        this.name = name;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservationWithRentalSpecsResponse of(final Reservation reservation, final List<RentalSpec> rentalSpecs) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        final List<ReservationSpecWithRentalSpecsResponse> reservationSpecWithRentalSpecsRespons = reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecWithRentalSpecsResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
        // TODO: 2023/05/03 학번이 가짜
        return new ReservationWithRentalSpecsResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecWithRentalSpecsRespons);
    }
}
