package com.girigiri.kwrental.reservation.service.reserve.template;

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

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.operation.service.OperationChecker;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.service.remainquantity.RemainQuantityValidator;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReserveTemplateTest {

	@Mock
	private PenaltyChecker penaltyChecker;
	@Mock
	private RemainQuantityValidator remainQuantityValidator;
	@Mock
	private OperationChecker operationChecker;
	@InjectMocks
	private ReserveTemplate reserveTemplate;

	@Test
	@DisplayName("이미 진행 중인 페널티가 있으면 대여 예약를 할 수 없다.")
	void reserve_hasOngoingPenalty() {
		// given
		given(penaltyChecker.hasOngoingPenalty(1L)).willReturn(true);
		given(operationChecker.canOperate(anyCollection())).willReturn(true);

		// when, then
		assertThatThrownBy(() -> reserveTemplate.reserve(1L, Collections.emptyList(),
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
		final RentableAsset asset = LabRoomFixture.builder().id(assetId).build();
		final ReservationSpec spec = ReservationSpecFixture.builder(asset)
			.amount(RentalAmount.ofPositive(amount))
			.period(rentalPeriod)
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(spec)).build();
		doThrow(NotEnoughAmountException.class).when(remainQuantityValidator)
			.validateAmount(assetId, amount, rentalPeriod);
		given(operationChecker.canOperate(anyCollection())).willReturn(true);

		// when, then
		assertThatThrownBy(() -> reserveTemplate.reserve(memberId, List.of(reservation),
			ReserveValidator.noExtraValidation()))
			.isExactlyInstanceOf(NotEnoughAmountException.class);
	}
}