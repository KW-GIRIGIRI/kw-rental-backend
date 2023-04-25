package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec.ReservationSpecBuilder;

import java.time.LocalDate;

public class ReservationSpecFixture {

    public static ReservationSpec create(final Equipment equipment) {
        return builder(equipment)
                .build();
    }

    public static ReservationSpecBuilder builder(final Equipment equipment) {
        return ReservationSpec.builder()
                .amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
                .equipment(equipment);
    }
}
