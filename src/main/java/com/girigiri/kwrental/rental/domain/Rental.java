package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;
import com.girigiri.kwrental.reservation.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class Rental {

    private final Map<Long, RentalSpec> rentalSpecMap;
    private final ReservationFromRental reservationFromRental;

    private Rental(final Map<Long, RentalSpec> rentalSpecMap, final ReservationFromRental reservationFromRental) {
        this.rentalSpecMap = rentalSpecMap;
        this.reservationFromRental = reservationFromRental;
    }

    public static Rental of(final List<RentalSpec> rentalSpecs, final Reservation reservation) {
        final Map<Long, RentalSpec> rentalSpecMap = rentalSpecs.stream()
                .collect(toMap(RentalSpec::getId, Function.identity()));
        final ReservationFromRental reservationFromRental = ReservationFromRental.from(reservation);
        return new Rental(rentalSpecMap, reservationFromRental);
    }

    public void returnAll(final Map<Long, RentalSpecStatus> statusesPerRentalSpecId) {
        final LocalDateTime returnDateTime = LocalDateTime.now();
        for (Long rentalSpecId : statusesPerRentalSpecId.keySet()) {
            final RentalSpec rentalSpec = getRentalSpec(rentalSpecId);
            setStatus(rentalSpec, statusesPerRentalSpecId.get(rentalSpecId));
            rentalSpec.setReturnDateTimeIfAnyReturned(returnDateTime);
        }
        final Map<Long, List<RentalSpecStatus>> rentalStatusPerReservationSpecId = groupRentalStatusByReservationSpecId();
        reservationFromRental.setStatusAfterReturn(rentalStatusPerReservationSpecId);
    }

    private void setStatus(final RentalSpec rentalSpec, final RentalSpecStatus status) {
        final boolean nowIsLegalForReturn = reservationFromRental.nowIsLegalForReturn(rentalSpec.getReservationSpecId());
        if (status == RentalSpecStatus.RETURNED && !nowIsLegalForReturn) {
            rentalSpec.setStatus(RentalSpecStatus.OVERDUE_RETURNED);
            return;
        }
        rentalSpec.setStatus(status);
    }

    private RentalSpec getRentalSpec(final Long id) {
        final RentalSpec rentalSpec = rentalSpecMap.get(id);
        if (rentalSpec == null) throw new RentalSpecNotFoundException();
        return rentalSpec;
    }

    private Map<Long, List<RentalSpecStatus>> groupRentalStatusByReservationSpecId() {
        return rentalSpecMap.values().stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId, mapping(RentalSpec::getStatus, toList())));
    }
}
