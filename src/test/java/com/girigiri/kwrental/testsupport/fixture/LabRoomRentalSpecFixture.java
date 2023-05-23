package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import static com.girigiri.kwrental.rental.domain.LabRoomRentalSpec.LabRoomRentalSpecBuilder;

public class LabRoomRentalSpecFixture {

    public static LabRoomRentalSpec create() {
        return builder().build();
    }

    public static LabRoomRentalSpecBuilder builder() {
        return LabRoomRentalSpec.builder()
                .acceptDateTime(RentalDateTime.now())
                .reservationSpecId(0L)
                .reservationId(0L)
                .status(RentalSpecStatus.RENTED)
                .returnDateTime(null);
    }
}
