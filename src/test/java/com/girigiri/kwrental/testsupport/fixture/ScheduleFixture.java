package com.girigiri.kwrental.testsupport.fixture;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.girigiri.kwrental.schedule.domain.Schedule;

public class ScheduleFixture {

	public static Schedule create(final DayOfWeek dayOfWeek) {
		return builder(dayOfWeek).build();
	}

	public static Schedule.ScheduleBuilder builder(final DayOfWeek dayOfWeek) {
		return Schedule.builder()
			.dayOfWeek(dayOfWeek)
			.startAt(LocalTime.MIN)
			.endAt(LocalTime.MAX);
	}
}
