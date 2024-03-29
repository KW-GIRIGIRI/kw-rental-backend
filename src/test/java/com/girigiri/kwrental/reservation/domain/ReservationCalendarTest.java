package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

class ReservationCalendarTest {

	@Test
	@DisplayName("대여 상세를 예약 달력에 추가한다.")
	void addAll() {
		// given
		final Equipment equipment = EquipmentFixture.create();
		final YearMonth now = YearMonth.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(2))
			.period(new RentalPeriod(now.atDay(1), now.atDay(2)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
			.period(new RentalPeriod(now.atEndOfMonth(), now.atEndOfMonth().plusDays(1))).build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment)
			.period(new RentalPeriod(now.atEndOfMonth().minusDays(1), now.atEndOfMonth())).build();
		final Reservation reservation1 = ReservationFixture.create(List.of(reservationSpec1));
		final Reservation reservation2 = ReservationFixture.create(List.of(reservationSpec2));
		final Reservation reservation3 = ReservationFixture.create(List.of(reservationSpec3));
		final ReservationCalendar calendar = ReservationCalendar.from(now.atDay(1), now.atEndOfMonth());

		// when
		calendar.addAll(List.of(reservationSpec1, reservationSpec2, reservationSpec3));

		// then
		assertAll(
			() -> assertThat(calendar.getCalendar().get(1)).containsExactly(
				reservation1.getName(), reservation1.getName()),
			() -> assertThat(calendar.getCalendar().get(now.atEndOfMonth().getDayOfMonth())).containsOnly(
				reservation1.getName())
		);
	}
}