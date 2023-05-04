package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.rental.exception.RentedStatusWhenReturnException;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.exception.ReservationSpecNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class ReservationFromRental {
    private final Map<Long, ReservationSpec> reservationSpecMap;
    private final Reservation reservation;

    private ReservationFromRental(final Map<Long, ReservationSpec> reservationSpecMap, final Reservation reservation) {
        this.reservationSpecMap = reservationSpecMap;
        this.reservation = reservation;
    }

    public static ReservationFromRental from(final Reservation reservation) {
        final Map<Long, ReservationSpec> reservationSpecMap = reservation.getReservationSpecs().stream()
                .collect(toMap(ReservationSpec::getId, Function.identity()));
        return new ReservationFromRental(reservationSpecMap, reservation);
    }

    public boolean nowIsLegalForReturn(final Long reservationSpecId) {
        final ReservationSpec reservationSpec = getReservationSpec(reservationSpecId);
        return reservationSpec.isLegalReturnIn(LocalDate.now());
    }

    private ReservationSpec getReservationSpec(final Long reservationSpecId) {
        final ReservationSpec reservationSpec = reservationSpecMap.get(reservationSpecId);
        if (reservationSpec == null) throw new ReservationSpecNotFoundException();
        return reservationSpec;
    }

    public void setStatusAfterReturn(final Map<Long, List<RentalSpecStatus>> rentalStatusPerReservationSpecId) {
        for (Long reservationSpecId : rentalStatusPerReservationSpecId.keySet()) {
            final List<RentalSpecStatus> rentalSpecStatuses = rentalStatusPerReservationSpecId.get(reservationSpecId);
            setStatus(reservationSpecId, rentalSpecStatuses);
        }
        reservation.updateIfTerminated();
    }

    private void setStatus(final Long reservationSpecId, List<RentalSpecStatus> rentalSpecStatuses) {
        final ReservationSpec reservationSpec = getReservationSpec(reservationSpecId);
        reservationSpec.setStatus(getStatus(rentalSpecStatuses));
    }

    private ReservationSpecStatus getStatus(List<RentalSpecStatus> rentalSpecStatuses) {
        if (rentalSpecStatuses.contains(RentalSpecStatus.RENTED)) throw new RentedStatusWhenReturnException();
        if (rentalSpecStatuses.contains(RentalSpecStatus.OVERDUE_RENTED)) {
            return ReservationSpecStatus.OVERDUE_RENTED;
        }
        if (containsAbnormalReturn(rentalSpecStatuses)) {
            return ReservationSpecStatus.ABNORMAL_RETURNED;
        }
        if (isAllReturned(rentalSpecStatuses)) {
            return ReservationSpecStatus.RETURNED;
        }
        throw new NotExpectedRentalStatusException();
    }

    private static boolean isAllReturned(final List<RentalSpecStatus> rentalSpecStatuses) {
        return rentalSpecStatuses.stream().anyMatch(it -> it == RentalSpecStatus.RETURNED);
    }

    private static boolean containsAbnormalReturn(final List<RentalSpecStatus> rentalSpecStatuses) {
        return rentalSpecStatuses.contains(RentalSpecStatus.OVERDUE_RETURNED) || rentalSpecStatuses.contains(RentalSpecStatus.LOST)
                || rentalSpecStatuses.contains(RentalSpecStatus.BROKEN);
    }
}
