package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EquipmentReservationWithMemberNumberTest {

	@Test
	@DisplayName("수령된 대여 예약 인지 확인한다.")
	void isAccepted() {
		// given
		final EquipmentReservationWithMemberNumber equipmentReservationWithMemberNumber = new EquipmentReservationWithMemberNumber(
			1L, "name", "11111111", RentalDateTime.now(), Collections.emptyList());

		// when
		final boolean actual = equipmentReservationWithMemberNumber.isAccepted();

		// then
		assertThat(actual).isTrue();
	}
}