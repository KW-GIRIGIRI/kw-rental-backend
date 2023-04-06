package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;

import java.time.LocalDate;

import static com.girigiri.kwrental.inventory.domain.Inventory.InventoryBuilder;

public class InventoryFixture {

    public static Inventory create(final Equipment equipment) {
        return builder(equipment).build();
    }

    public static InventoryBuilder builder(final Equipment equipment) {
        final LocalDate rentalStartDate = LocalDate.now().plusDays(1);
        final LocalDate rentalEndDate = rentalStartDate.plusDays(1);

        return Inventory.builder()
                .rentalAmount(new RentalAmount(1))
                .rentalPeriod(new RentalPeriod(rentalStartDate, rentalEndDate))
                .equipment(equipment);
    }
}
