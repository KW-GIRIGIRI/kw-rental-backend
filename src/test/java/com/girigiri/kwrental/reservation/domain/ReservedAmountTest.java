package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservedAmountTest {

	@Test
	@DisplayName("남은 수량을 조회한다.")
	void getRemainingAmount() {
		// given
		final ReservedAmount reservedAmount = new ReservedAmount(1L, 10, 9);

		// when
		final int actual = reservedAmount.getRemainingAmount();

		// then
		assertThat(actual).isOne();
	}
}