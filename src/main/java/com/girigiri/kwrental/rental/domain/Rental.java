package com.girigiri.kwrental.rental.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;
import com.girigiri.kwrental.rental.exception.RentedStatusForReturnException;
import com.girigiri.kwrental.reservation.domain.Reservation;

public class Rental {

    private final Map<Long, RentalSpec> rentalSpecMap;
    private final ReservationFromRental reservationFromRental;

    private Rental(final Map<Long, RentalSpec> rentalSpecMap, final ReservationFromRental reservationFromRental) {
        this.rentalSpecMap = rentalSpecMap;
        this.reservationFromRental = reservationFromRental;
    }

    public static Rental of(final List<? extends RentalSpec> rentalSpecs, final Reservation reservation) {
        final Map<Long, RentalSpec> rentalSpecMap = rentalSpecs.stream()
                .collect(toMap(RentalSpec::getId, Function.identity()));
        final ReservationFromRental reservationFromRental = ReservationFromRental.from(reservation);
        return new Rental(rentalSpecMap, reservationFromRental);
    }

    public void returnByRentalSpecId(final Long rentalSpecId, final RentalSpecStatus status) {
        final LocalDateTime returnDateTime = LocalDateTime.now();
        final RentalSpec rentalSpec = getRentalSpec(rentalSpecId);
        initStatus(rentalSpec, status);
        rentalSpec.setReturnDateTimeIfAnyReturned(returnDateTime);
    }

    private void initStatus(final RentalSpec rentalSpec, final RentalSpecStatus status) {
        if (status == RentalSpecStatus.RENTED)
            throw new RentedStatusForReturnException();
        final boolean nowIsLegalForReturn = reservationFromRental.nowIsLegalForReturn(
            rentalSpec.getReservationSpecId());
        if (status == RentalSpecStatus.RETURNED && !nowIsLegalForReturn) {
            rentalSpec.setStatus(RentalSpecStatus.OVERDUE_RETURNED);
            return;
        }
        rentalSpec.setStatus(status);
    }

    public void updateStatusByRentalSpecId(final Long rentalSpecId, final RentalSpecStatus status) {
        final RentalSpec rentalSpec = getRentalSpec(rentalSpecId);
        updateStatus(rentalSpec, status);
    }

    private void updateStatus(final RentalSpec rentalSpec, final RentalSpecStatus status) {
        if (status == RentalSpecStatus.RENTED)
            throw new RentedStatusForReturnException();
        rentalSpec.setStatus(status);
    }

    public <T extends RentalSpec> T getRentalSpecAs(final Long id, final Class<T> clazz) {
        final RentalSpec rentalSpec = getRentalSpec(id);
        return rentalSpec.as(clazz);
    }

    private RentalSpec getRentalSpec(final Long id) {
        final RentalSpec rentalSpec = rentalSpecMap.get(id);
        if (rentalSpec == null)
            throw new RentalSpecNotFoundException();
        return rentalSpec;
    }

    public void setReservationStatusAfterModification() {
        final Map<Long, List<RentalSpecStatus>> rentalStatusPerReservationSpecId = groupRentalStatusByReservationSpecId();
        reservationFromRental.setStatusAfterReturn(rentalStatusPerReservationSpecId);
    }

    private Map<Long, List<RentalSpecStatus>> groupRentalStatusByReservationSpecId() {
        return rentalSpecMap.values().stream()
                .collect(groupingBy(RentalSpec::getReservationSpecId, mapping(RentalSpec::getStatus, toList())));
    }
}
