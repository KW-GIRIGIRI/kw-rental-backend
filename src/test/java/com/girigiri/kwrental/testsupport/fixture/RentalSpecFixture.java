package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public class RentalSpecFixture {

    public static RentalSpec create() {
        return builder().build();
    }

    public static RentalSpec.RentalSpecBuilder builder() {
        return RentalSpec.builder()
                .acceptDateTime(RentalDateTime.now())
                .propertyNumber("12345678")
                .reservationSpecId(0L)
                .reservationId(0L)
                .status(RentalSpecStatus.RENTED)
                .returnDateTime(null);
    }
}
