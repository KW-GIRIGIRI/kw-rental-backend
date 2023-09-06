package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.schedule.domain.EntireOperation;

public class EntireOperationFixture {
	public static EntireOperation create(final boolean isRunning) {
		return builder(isRunning).build();
	}

	public static EntireOperation.EntireOperationBuilder builder(final boolean isRunning) {
		return EntireOperation.builder()
			.isRunning(isRunning);
	}
}
