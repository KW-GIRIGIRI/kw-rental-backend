package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationViewServiceTest {

	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@InjectMocks
	private ReservationViewService reservationViewService;

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
		final ReservationsByEquipmentPerYearMonthResponse expect = reservationViewService.getReservationsByEquipmentsPerYearMonth(
			equipment.getId(), YearMonth.now());

		// then
		assertThat(
			expect.reservations().get(reservationSpec.getStartDate().getDayOfMonth())).containsExactlyInAnyOrder(
			reservation.getName(), reservation.getName());
	}
}