package com.girigiri.kwrental.reservation.repository.dto;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ReservationWithMemberNumber {

    private Reservation reservation;
    private String memberNumber;

    protected ReservationWithMemberNumber() {
    }

    @QueryProjection
    public ReservationWithMemberNumber(final Reservation reservation, final String memberNumber) {
        this.reservation = reservation;
        this.memberNumber = memberNumber;
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
