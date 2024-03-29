package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
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
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
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
	@Mock
	private ReservationValidator reservationValidator;
	@InjectMocks
	private ReservationRetrieveService reservationRetrieveService;

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

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수를 기자재 별로 분류한다.")
	void groupPropertyNumbersCountByEquipmentId() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when
		final Map<Long, Set<String>> propertyNumbersByEquipmentId = reservationRetrieveService.groupPropertyNumbersByEquipmentId(
			reservation.getId(), Map.of(reservationSpec.getId(), Set.of("1111111", "222222222"))
		);

		// then
		assertThat(propertyNumbersByEquipmentId.get(equipment.getId())).containsExactlyInAnyOrder("1111111",
			"222222222");
	}
}