package com.girigiri.kwrental.testsupport.fixture;

import static com.girigiri.kwrental.inventory.domain.Inventory.*;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;

public class InventoryFixture {

    public static Inventory create(final RentableAsset rentableAsset, final Long memberId) {
        return builder(rentableAsset).memberId(memberId).build();
    }

    public static InventoryBuilder builder(final RentableAsset rentableAsset) {
        final LocalDate rentalStartDate = LocalDate.now().plusDays(1);
        final LocalDate rentalEndDate = rentalStartDate.plusDays(1);

        return Inventory.builder()
            .rentalAmount(RentalAmount.ofPositive(1))
            .rentalPeriod(new RentalPeriod(rentalStartDate, rentalEndDate))
            .asset(rentableAsset)
            .memberId(0L);
    }
}
