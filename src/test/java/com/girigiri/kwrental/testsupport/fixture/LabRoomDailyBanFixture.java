package com.girigiri.kwrental.testsupport.fixture;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan.LabRoomDailyBanBuilder;

public class LabRoomDailyBanFixture {

	public static LabRoomDailyBanBuilder builder() {
		return LabRoomDailyBan.builder()
			.labRoomId(0L)
			.banDate(LocalDate.now());
	}

	public static LabRoomDailyBan create() {
		return builder().build();
	}
}
