package com.girigiri.kwrental.testsupport.fixture;

import java.time.DayOfWeek;

import com.girigiri.kwrental.operation.domain.Schedule;

public class ScheduleFixture {

	public static Schedule create(final DayOfWeek dayOfWeek) {
		return builder(dayOfWeek).build();
	}

	public static Schedule.ScheduleBuilder builder(final DayOfWeek dayOfWeek) {
		return Schedule.builder()
			.dayOfWeek(dayOfWeek);
	}
}
