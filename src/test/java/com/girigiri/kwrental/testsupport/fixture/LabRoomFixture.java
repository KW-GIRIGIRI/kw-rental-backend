package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom.LabRoomBuilder;

public class LabRoomFixture {

	public static LabRoomBuilder builder() {
		return LabRoom.builder()
			.name("name")
			.totalQuantity(2)
			.rentableQuantity(2)
			.maxRentalDays(1)
			.isAvailable(true)
			.reservationCountPerDay(1);
	}

	public static LabRoom create() {
		return builder()
			.build();
	}
}
