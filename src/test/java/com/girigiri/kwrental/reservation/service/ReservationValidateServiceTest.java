package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationValidateServiceTest {
	@Mock
	private ReservationRepository reservationRepository;
	@InjectMocks
	private ReservationValidateService reservationValidateService;

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증하고 기자재 별로 다시 분류한다.")
	void validatePropertyNumbersCountAndGroupByEquipmentId() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when
		final Map<Long, Set<String>> propertyNumbersByEquipmentId = reservationValidateService.validatePropertyNumbersCountAndGroupByEquipmentId(
			reservation.getId(), Map.of(reservationSpec.getId(), Set.of("1111111", "222222222"))
		);

		// then
		assertThat(propertyNumbersByEquipmentId.get(equipment.getId())).containsExactlyInAnyOrder("1111111",
			"222222222");
	}

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 예약 신청한 갯수와 일치하지 않으면 예외발생")
	void validatePropertyNumbersCountAndGroupByEquipmentId_notSameAmount() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(1))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when, then
		assertThatThrownBy(() -> reservationValidateService.validatePropertyNumbersCountAndGroupByEquipmentId(
			reservation.getId(), Map.of(reservationSpec.getId(), Set.of("1111111", "222222222"))))
			.isExactlyInstanceOf(ReservationSpecException.class);
	}

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증하려는데 예약 상세가 존재하지 않으면 예외발생")
	void validatePropertyNumbersCountAndGroupByEquipmentId_notFoundReservationSpec() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(1))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when, then
		assertThatThrownBy(() -> reservationValidateService.validatePropertyNumbersCountAndGroupByEquipmentId(
			reservation.getId(), Map.of(4L, Set.of("1111111", "222222222"), 2L, Set.of("333333333"))))
			.isExactlyInstanceOf(ReservationSpecException.class);
	}

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한 지 검증하려하는데 대여 예약이 존재하지 않으면 예외 발생")
	void validatePropertyNumbersCountAndGroupByEquipmentId_notFoundReservation() {
		// given
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> reservationValidateService.validatePropertyNumbersCountAndGroupByEquipmentId(
			1L, Map.of(1L, Set.of("1111111", "222222222"))))
			.isExactlyInstanceOf(ReservationNotFoundException.class);
	}
}