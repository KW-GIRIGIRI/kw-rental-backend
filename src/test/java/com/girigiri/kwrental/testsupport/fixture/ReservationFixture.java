package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;

import java.util.List;

public class ReservationFixture {
    public static Reservation create(final List<RentalSpec> rentalSpecs) {
        return builder(rentalSpecs)
                .build();
    }

    public static Reservation.ReservationBuilder builder(final List<RentalSpec> rentalSpecs) {
        return Reservation.builder()
                .email("email@email.com")
                .purpose("this is purpose")
                .name("대여자")
                .phoneNumber("01012345678")
                .rentalSpecs(rentalSpecs);
    }
}
