package com.girigiri.kwrental.testsupport.fixture;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.reservation.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec.ReservationSpecBuilder;

public class ReservationSpecFixture {

    public static ReservationSpec create(final Rentable rentable) {
        return builder(rentable)
                .build();
    }

    public static ReservationSpecBuilder builder(final Rentable rentable) {
        return ReservationSpec.builder()
                .amount(RentalAmount.ofPositive(1))
                .period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
                .rentable(rentable);
    }
}
