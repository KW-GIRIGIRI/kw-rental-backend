package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OperatingPeriodTest {

	@Test
	@DisplayName("운영 기간에 해당하는 날짜를 반환한다")
	void getRentalAvailableDates() {
		// given
		final LocalDate monday = LocalDate.of(2023, 6, 26);
		final LocalDate sunday = LocalDate.of(2023, 7, 2);
		final OperatingPeriod operatingPeriod = new OperatingPeriod(monday, sunday);

		// when
		final Set<LocalDate> actual = operatingPeriod.getRentalAvailableDates();

		// then
		assertThat(actual).containsExactlyInAnyOrder(monday, monday.plusDays(1), monday.plusDays(2),
			monday.plusDays(3));
	}
}