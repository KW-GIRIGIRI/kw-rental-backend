package com.girigiri.kwrental.reservation.service.remainquantity;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class RemainingQuantityServiceImplTest {

	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@InjectMocks
	private RemainingQuantityServiceImpl remainingQuantityService;

	@Test
	@DisplayName("기간 사이의 대여 신청 대여 갯수를 센다.")
	void getReservedAmountBetween() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(10).build();
		final LocalDate monday = LocalDate.of(2023, 5, 15);
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(monday, monday.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(2))
			.period(new RentalPeriod(monday.plusDays(1), monday.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(3))
			.period(new RentalPeriod(monday.plusDays(2), monday.plusDays(3)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(4))
			.period(new RentalPeriod(monday.plusDays(3), monday.plusDays(4)))
			.build();
		given(reservationSpecRepository.findOverlappedReservedOrRentedInclusive(any(), any(), any()))
			.willReturn(List.of(reservationSpec1, reservationSpec2, reservationSpec3, reservationSpec4));

		// when
		final Map<LocalDate, Integer> reservedAmount = remainingQuantityService.getReservedAmountInclusive(
			equipment.getId(), monday, monday.plusDays(4));

		// then
		Assertions.assertThat(reservedAmount)
			.containsEntry(monday, 1)
			.containsEntry(monday.plusDays(1), 2)
			.containsEntry(monday.plusDays(2), 3)
			.containsEntry(monday.plusDays(3), 4)
			.containsEntry(monday.plusDays(4), 0);
	}

	@Test
	@DisplayName("기간 사이의 대여 신청 수를 센다.")
	void getReservationCountInclusive() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).totalQuantity(10).build();
		final LocalDate monday = LocalDate.of(2023, 5, 15);
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(monday, monday.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(2))
			.period(new RentalPeriod(monday.plusDays(1), monday.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(3))
			.period(new RentalPeriod(monday.plusDays(2), monday.plusDays(3)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment)
			.amount(RentalAmount.ofPositive(4))
			.period(new RentalPeriod(monday.plusDays(3), monday.plusDays(4)))
			.build();
		given(reservationSpecRepository.findOverlappedReservedOrRentedInclusive(any(), any(), any()))
			.willReturn(List.of(reservationSpec1, reservationSpec2, reservationSpec3, reservationSpec4));

		// when
		final Map<LocalDate, Integer> reservedAmount = remainingQuantityService.getReservationCountInclusive(
			equipment.getId(), monday, monday.plusDays(4));

		// then
		Assertions.assertThat(reservedAmount)
			.containsEntry(monday, 1)
			.containsEntry(monday.plusDays(1), 1)
			.containsEntry(monday.plusDays(2), 1)
			.containsEntry(monday.plusDays(3), 1);
	}
}