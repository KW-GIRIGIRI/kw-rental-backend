package com.girigiri.kwrental.reservation.domain;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ReservationWithMemberNumber {

    private final Reservation reservation;
    private final String memberNumber;


    public ReservationWithMemberNumber(final Reservation reservation, final String memberNumber) {
        this.reservation = reservation;
        this.memberNumber = memberNumber;
    }

    public List<ReservationSpec> getReservedOrRentedSpecs() {
        return reservation.getReservationSpecs().stream()
                .filter(ReservationSpec::isReservedOrRented)
                .toList();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ReservationWithMemberNumber that = (ReservationWithMemberNumber) o;
        return Objects.equals(reservation, that.reservation) && Objects.equals(memberNumber, that.memberNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservation, memberNumber);
    }
}
