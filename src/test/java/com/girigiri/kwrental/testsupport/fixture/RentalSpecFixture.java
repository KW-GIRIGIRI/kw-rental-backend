package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.RentalSpec;

import java.time.LocalDate;

import static com.girigiri.kwrental.reservation.domain.RentalSpec.RentalSpecBuilder;

public class RentalSpecFixture {

    public static RentalSpec create(final Equipment equipment) {
        return builder(equipment)
                .build();
    }

    public static RentalSpecBuilder builder(final Equipment equipment) {
        return RentalSpec.builder()
                .amount(new RentalAmount(1))
                .period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
                .equipment(equipment);
    }
}
