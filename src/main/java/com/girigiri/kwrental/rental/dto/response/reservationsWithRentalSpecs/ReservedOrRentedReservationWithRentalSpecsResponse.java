package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class ReservedOrRentedReservationWithRentalSpecsResponse {

    private Long reservationId;
    private String name;
    private String memberNumber;
    private LocalDateTime acceptDateTime;
    private List<ReservationSpecWithRentalSpecsResponse> reservationSpecs;

    private ReservedOrRentedReservationWithRentalSpecsResponse() {
    }

    private ReservedOrRentedReservationWithRentalSpecsResponse(final Long reservationId, final String name, final String memberNumber, final LocalDateTime acceptDateTime, final List<ReservationSpecWithRentalSpecsResponse> reservationSpecs) {
        this.reservationId = reservationId;
        this.name = name;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public static ReservedOrRentedReservationWithRentalSpecsResponse of(final ReservationWithMemberNumber reservationWithMemberNumber, final List<RentalSpec> rentalSpecs) {
        final List<ReservationSpecWithRentalSpecsResponse> reservationSpecWithRentalSpecsResponse = mapToReservationSpecWithRentalSpecResponse(rentalSpecs, reservationWithMemberNumber);
        final Reservation reservation = reservationWithMemberNumber.getReservation();
        final LocalDateTime rentalAcceptDateTime = reservation.getAcceptDateTime() == null ? null : reservation.getAcceptDateTime().toLocalDateTime();
        return new ReservedOrRentedReservationWithRentalSpecsResponse(reservation.getId(), reservation.getName(),
                reservationWithMemberNumber.getMemberNumber(), rentalAcceptDateTime, reservationSpecWithRentalSpecsResponse);
    }

    private static List<ReservationSpecWithRentalSpecsResponse> mapToReservationSpecWithRentalSpecResponse(final List<RentalSpec> rentalSpecs, final ReservationWithMemberNumber reservationWithMemberNumber) {
        final Map<Long, List<RentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId));
        return reservationWithMemberNumber.getReservedOrRentedSpecs().stream()
                .map(it -> ReservationSpecWithRentalSpecsResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
    }
}
