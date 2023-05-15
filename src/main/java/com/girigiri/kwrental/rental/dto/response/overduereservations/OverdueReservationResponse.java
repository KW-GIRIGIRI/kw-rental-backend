package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class OverdueReservationResponse {
    private Long reservationId;
    private String name;
    private String memberNumber;
    private Instant returnDate;
    private List<OverdueReservationSpecResponse> reservationSpecs;

    private OverdueReservationResponse() {
    }

    private OverdueReservationResponse(final Long reservationId, final String name, final String memberNumber, final Instant returnDate, final List<OverdueReservationSpecResponse> reservationSpecs) {
        this.reservationId = reservationId;
        this.name = name;
        this.memberNumber = memberNumber;
        this.returnDate = returnDate;
        this.reservationSpecs = reservationSpecs;
    }

    public static OverdueReservationResponse of(final ReservationWithMemberNumber reservationWithMemberNumber, final List<RentalSpec> rentalSpecs) {
        final Reservation reservation = reservationWithMemberNumber.getReservation();
        final List<OverdueReservationSpecResponse> overdueReservationSpecResponses = mapToReservationSpecResponse(rentalSpecs, reservation);
        return new OverdueReservationResponse(reservation.getId(), reservation.getName(),
                reservationWithMemberNumber.getMemberNumber(), reservation.getAcceptDateTime().getInstant(), overdueReservationSpecResponses);
    }

    private static List<OverdueReservationSpecResponse> mapToReservationSpecResponse(final List<RentalSpec> rentalSpecs, final Reservation reservation) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        return reservation.getReservationSpecs().stream()
                .filter(it -> groupedRentalSpecsByReservationSpecId.get(it.getId()) != null)
                .map(it -> OverdueReservationSpecResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
    }
}
