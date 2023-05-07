package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;

import java.util.List;

public class ReservationFixture {
    public static Reservation create(final List<ReservationSpec> reservationSpecs) {
        return builder(reservationSpecs)
                .build();
    }

    public static Reservation.ReservationBuilder builder(final List<ReservationSpec> reservationSpecs) {
        return Reservation.builder()
                .email("email@email.com")
                .purpose("this is purpose")
                .name("대여자")
                .phoneNumber("01012345678")
                .memberId(0L)
                .reservationSpecs(reservationSpecs);
    }
}
