package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.domain.LabRoom.LabRoomBuilder;

public class LabRoomFixture {

    public static LabRoomBuilder builder() {
        return LabRoom.builder()
                .name("name")
                .totalQuantity(2)
                .maxRentalDays(1)
                .isAvailable(true);
    }

    public static LabRoom create() {
        return builder()
                .build();
    }
}
