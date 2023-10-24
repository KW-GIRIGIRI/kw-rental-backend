package com.girigiri.kwrental.operation.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

class ScheduleTest {

	@Test
	@DisplayName("특정 시점이 해당 스케줄에 적용되는지 확인한다.")
	void canOperatesAt() {
		// given
		final Schedule schedule = ScheduleFixture.create(DayOfWeek.FRIDAY);
		final LocalDate friday = LocalDate.of(2023, 9, 15);

		// when
		final boolean actual = schedule.canOperatesAt(friday);

		// then
		assertThat(actual).isTrue();
	}
}