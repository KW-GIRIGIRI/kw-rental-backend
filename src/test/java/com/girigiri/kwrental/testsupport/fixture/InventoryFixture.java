package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;

import java.time.LocalDate;

import static com.girigiri.kwrental.inventory.domain.Inventory.InventoryBuilder;

public class InventoryFixture {

    public static Inventory create() {
        return builder().build();
    }

    public static InventoryBuilder builder() {
        final LocalDate rentalStartDate = LocalDate.now().plusDays(1);
        final LocalDate rentalEndDate = rentalStartDate.plusDays(1);

        return Inventory.builder()
                .amount(1)
                .rentalPeriod(new RentalPeriod(rentalStartDate, rentalEndDate))
                .equipmentId(1L);
    }
}
