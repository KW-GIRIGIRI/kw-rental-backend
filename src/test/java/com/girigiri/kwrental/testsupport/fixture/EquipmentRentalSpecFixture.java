package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

public class EquipmentRentalSpecFixture {

    public static EquipmentRentalSpec create() {
        return builder().build();
    }

    public static EquipmentRentalSpec.EquipmentRentalSpecBuilder builder() {
        return EquipmentRentalSpec.builder()
                .acceptDateTime(RentalDateTime.now())
                .propertyNumber("12345678")
                .reservationSpecId(0L)
                .reservationId(0L)
                .status(RentalSpecStatus.RENTED)
                .returnDateTime(null);
    }
}
