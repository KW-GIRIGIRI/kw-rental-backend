package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.reservation.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationReserveServiceTest {

	@Mock
	private PenaltyService penaltyService;
	@Mock
	private AmountValidator amountValidator;
	@InjectMocks
	private ReservationReserveService reservationReserveService;

	@Test
	@DisplayName("이미 진행 중인 페널티가 있으면 대여 예약를 할 수 없다.")
	void reserve_hasOngoingPenalty() {
		// given
		given(penaltyService.hasOngoingPenalty(1L)).willReturn(true);

		// when, then
		assertThatThrownBy(() -> reservationReserveService.reserve(1L, Collections.emptyList(),
			ReserveValidator.noExtraValidation())).isExactlyInstanceOf(ReservationException.class);
	}

	@Test
	@DisplayName("대여 예약이 대여 가능한 갯수만큼 있는지 검증한다.")
	void reserve_validateAvailableCount() {
		// given
		final Long assetId = 1L;
		final Integer amount = 1;
		final RentalPeriod rentalPeriod = new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1));
		final Long memberId = 2L;
		final Rentable asset = LabRoomFixture.builder().id(assetId).build();
		final ReservationSpec spec = ReservationSpecFixture.builder(asset)
			.amount(RentalAmount.ofPositive(amount))
			.period(rentalPeriod)
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(spec)).build();
		doThrow(NotEnoughAmountException.class).when(amountValidator).validateAmount(assetId, amount, rentalPeriod);

		// when, then
		assertThatThrownBy(() -> reservationReserveService.reserve(memberId, List.of(reservation),
			ReserveValidator.noExtraValidation()))
			.isExactlyInstanceOf(NotEnoughAmountException.class);
	}
}