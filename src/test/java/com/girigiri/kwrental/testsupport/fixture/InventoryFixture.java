package com.girigiri.kwrental.testsupport.fixture;

import static com.girigiri.kwrental.inventory.domain.Inventory.*;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;

public class InventoryFixture {

    public static Inventory create(final Rentable rentable, final Long memberId) {
        return builder(rentable).memberId(memberId).build();
    }

    public static InventoryBuilder builder(final Rentable rentable) {
        final LocalDate rentalStartDate = LocalDate.now().plusDays(1);
        final LocalDate rentalEndDate = rentalStartDate.plusDays(1);

        return Inventory.builder()
                .rentalAmount(RentalAmount.ofPositive(1))
                .rentalPeriod(new RentalPeriod(rentalStartDate, rentalEndDate))
                .rentable(rentable)
                .memberId(0L);
    }
}
