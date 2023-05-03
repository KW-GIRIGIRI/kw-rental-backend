package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class OverdueReservationResponse {
    private String name;
    private String memberNumber;
    private LocalDateTime returnDate;
    private List<OverdueReservationSpecsResponse> reservationSpecs;

    private OverdueReservationResponse() {
    }

    private OverdueReservationResponse(final String name, final String memberNumber, final LocalDateTime returnDate, final List<OverdueReservationSpecsResponse> reservationSpecs) {
        this.name = name;
        this.memberNumber = memberNumber;
        this.returnDate = returnDate;
        this.reservationSpecs = reservationSpecs;
    }

    public static OverdueReservationResponse of(final Reservation reservation, final List<RentalSpec> rentalSpecs) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        final List<OverdueReservationSpecsResponse> reservationSpecByStartDateResponses = reservation.getReservationSpecs().stream()
                .map(it -> OverdueReservationSpecsResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
        // TODO: 2023/05/03 학번이 가짜
        return new OverdueReservationResponse(reservation.getName(), "11111111", reservation.getAcceptDateTime(), reservationSpecByStartDateResponses);
    }
}
