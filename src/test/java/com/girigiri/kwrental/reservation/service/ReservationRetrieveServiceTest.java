package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationRetrieveServiceTest {

	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@Mock
	private ReservationRepository reservationRepository;
	@InjectMocks
	private ReservationRetrieveService reservationRetrieveService;

	@Test
	@DisplayName("특정 기간에 수령하는 특정 기자재의 대여 예약을 조회한다.")
	void getReservationsByEquipmentsPerYearMonth() {
		// given
		final Equipment equipment = EquipmentFixture.create();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(reservationSpec));
		given(reservationSpecRepository.findNotCanceldByStartDateBetween(any(), any(), any()))
			.willReturn(List.of(reservationSpec));

		// when
		final ReservationsByEquipmentPerYearMonthResponse expect = reservationRetrieveService.getReservationsByEquipmentsPerYearMonth(
			equipment.getId(), YearMonth.now());

		// then
		assertThat(
			expect.reservations().get(reservationSpec.getStartDate().getDayOfMonth())).containsExactlyInAnyOrder(
			reservation.getName(), reservation.getName());
	}

	@Test
	@DisplayName("특정 날짜에 수령하는 대여 예약을 조회한다.")
	void getReservationsByStartDate() {
		// given
		final Equipment equipment = EquipmentFixture.create();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).build();
		final Reservation reservation = ReservationFixture.create(List.of(reservationSpec));
		final EquipmentReservationWithMemberNumber equipmentReservation = new EquipmentReservationWithMemberNumber(
			reservation.getId(), reservation.getName(), "11111111", reservation.getAcceptDateTime(),
			List.of(reservationSpec));
		given(reservationSpecRepository.findEquipmentReservationWhenAccept(any()))
			.willReturn(Set.of(equipmentReservation));

		// when
		final Set<EquipmentReservationWithMemberNumber> reservationsByStartDate = reservationRetrieveService.getReservationsByStartDate(
			LocalDate.now());

		// then
		assertThat(reservationsByStartDate).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(equipmentReservation);
	}

	@Test
	@DisplayName("존재하지 않은 대여 예약 조회 시 예외 발생")
	void getReservationWithReservationSpecsById_notExists() {
		// given
		given(reservationRepository.findByIdWithSpecs(any()))
			.willReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> reservationRetrieveService.getReservationWithReservationSpecsById(1L))
			.isExactlyInstanceOf(ReservationNotFoundException.class);
	}
}