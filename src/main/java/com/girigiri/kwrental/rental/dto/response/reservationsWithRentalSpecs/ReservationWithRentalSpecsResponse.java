package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class ReservationWithRentalSpecsResponse {

    private Long reservationId;
    private String name;
    private String memberNumber;
    private LocalDateTime acceptDateTime;
    private List<ReservationSpecWithRentalSpecsResponse> reservationSpecs;

    private ReservationWithRentalSpecsResponse() {
    }

    private ReservationWithRentalSpecsResponse(final Long reservationId, final String name, final String memberNumber, final LocalDateTime acceptDateTime, final List<ReservationSpecWithRentalSpecsResponse> reservationSpecs) {
        this.reservationId = reservationId;
        this.name = name;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservationWithRentalSpecsResponse of(final ReservationWithMemberNumber reservationWithMemberNumber, final List<RentalSpec> rentalSpecs) {
        final Reservation reservation = reservationWithMemberNumber.getReservation();
        final List<ReservationSpecWithRentalSpecsResponse> reservationSpecWithRentalSpecsRespons = mapToReservationSpecWithRentalSpecResponse(rentalSpecs, reservation);
        return new ReservationWithRentalSpecsResponse(reservation.getId(), reservation.getName(),
                reservationWithMemberNumber.getMemberNumber(), reservation.getAcceptDateTime(), reservationSpecWithRentalSpecsRespons);
    }

    private static List<ReservationSpecWithRentalSpecsResponse> mapToReservationSpecWithRentalSpecResponse(final List<RentalSpec> rentalSpecs, final Reservation reservation) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        return reservation.getReservationSpecs().stream()
                .map(it -> ReservationSpecWithRentalSpecsResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
    }
}
