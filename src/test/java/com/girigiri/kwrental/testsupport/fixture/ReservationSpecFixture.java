package com.girigiri.kwrental.testsupport.fixture;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec.ReservationSpecBuilder;

public class ReservationSpecFixture {

    public static ReservationSpec create(final RentableAsset rentableAsset) {
        return builder(rentableAsset)
            .build();
    }

    public static ReservationSpecBuilder builder(final RentableAsset rentableAsset) {
        return ReservationSpec.builder()
            .amount(RentalAmount.ofPositive(1))
            .period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
            .asset(rentableAsset);
    }
}
