package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import java.time.LocalDateTime;

public class RentalSpecFixture {

    public static RentalSpec create() {
        return builder().build();
    }

    public static RentalSpec.RentalSpecBuilder builder() {
        return RentalSpec.builder()
                .acceptDateTime(LocalDateTime.now())
                .propertyNumber("12345678")
                .reservationSpecId(0L)
                .reservationId(0L)
                .status(RentalSpecStatus.RENTED)
                .returnDateTime(null);
    }
}
