package com.girigiri.kwrental.reservation.service.remainquantity;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class RemainQuantityValidatorTest {
	@Mock
	private AssetService assetService;
	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@InjectMocks
	private RemainQuantityValidator remainQuantityValidator;

	@Test
	@DisplayName("대여 가능한 갯수를 확인")
	void validateAmount() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(2).build();
		LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();

		given(assetService.getRentableById(any())).willReturn(equipment);
		given(reservationSpecRepository.findOverlappedReservedOrRentedByPeriod(any(), any())).willReturn(
			List.of(reservationSpec1, reservationSpec2));

		// when, then
		assertThatCode(() -> remainQuantityValidator.validateAmount(1L, 1, new RentalPeriod(now, now.plusDays(2))))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("대여 가능한 갯수가 요청한 갯수보다 부족하면 예외 발생")
	void validateAmount_notEnoughRemainingAmount() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(2).build();
		LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();

		given(assetService.getRentableById(any())).willReturn(equipment);
		given(reservationSpecRepository.findOverlappedReservedOrRentedByPeriod(any(), any())).willReturn(
			List.of(reservationSpec1, reservationSpec2));

		// when, then
		assertThatThrownBy(() -> remainQuantityValidator.validateAmount(1L, 1, new RentalPeriod(now, now.plusDays(1))))
			.isExactlyInstanceOf(NotEnoughAmountException.class);
	}
}