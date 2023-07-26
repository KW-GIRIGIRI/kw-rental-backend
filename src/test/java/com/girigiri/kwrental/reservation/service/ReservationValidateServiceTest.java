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
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.exception.AlreadyReservedLabRoomException;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationValidateServiceTest {
	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@InjectMocks
	private ReservationValidateService reservationValidateService;

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증한다.")
	void validatePropertyNumbersCountAndGroupByEquipmentId() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when, then
		assertThatCode(() -> reservationValidateService.validateReservationSpecIdContainsAll(
			reservation.getId(), Set.of(2L)))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 예약 신청한 갯수와 일치하지 않으면 예외발생")
	void validateAmountIsSame_notSameAmount() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(1))
			.build();
		given(reservationSpecRepository.findByIdIn(Set.of(reservationSpec.getId()))).willReturn(
			List.of(reservationSpec));

		// when, then
		assertThatThrownBy(() -> reservationValidateService.validateAmountIsSame(Map.of(reservationSpec.getId(), 2)))
			.isExactlyInstanceOf(ReservationSpecException.class);
	}

	@Test
	@DisplayName("대여 예약 상세에 해당하는 품목 자산 번호의 갯수가 적절한지 검증하려는데 예약 상세가 존재하지 않으면 예외발생")
	void validateReservationSpecIdContainsAll_notFoundReservationSpec() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment)
			.id(2L)
			.amount(RentalAmount.ofPositive(1))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(3L).build();
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when, then
		assertThatThrownBy(
			() -> reservationValidateService.validateReservationSpecIdContainsAll(reservation.getId(), Set.of(4L, 2L)))
			.isExactlyInstanceOf(ReservationSpecException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약하려는 기간에 이미 같은 사용자가 대여 예약하였는지 검증한다.")
	void validateAlreadyReservedSamePeriod_alreadyReserved() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final LabRoom labRoom2 = LabRoomFixture.builder().id(2L).build();
		final ReservationSpec specToValidate = ReservationSpecFixture.create(labRoom1);
		final Reservation reservationToValidate = ReservationFixture.create(List.of(specToValidate));

		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom2).build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		given(reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(reservationToValidate.getMemberId()))
			.willReturn(Set.of(reservation));

		// when, then
		assertThatThrownBy(() -> reservationValidateService.validateAlreadyReservedSamePeriod(reservationToValidate))
			.isExactlyInstanceOf(AlreadyReservedLabRoomException.class);
	}

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 모두 같은 랩실이어야 한다.")
	void validateLabRoomReservationForRent_notSameLabRoomName() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final LabRoom labRoom2 = LabRoomFixture.builder().id(2L).build();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1).id(1L).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom2).id(2L).build();
		final Reservation reservation1 = ReservationFixture.create(List.of(spec1));
		final Reservation reservation2 = ReservationFixture.create(List.of(spec2));
		reservationRepository.saveAll(List.of(reservation1, reservation2));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRepository.findByReservationSpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		// when, then
		assertThatCode(() -> reservationValidateService.validateLabRoomReservationForAccept(labRoom1.getName(), ids))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 모두 현재 대여 가능한 상태여야 한다.")
	void validateLabRoomReservationForRent_statusNotReserved() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1)
			.id(1L)
			.status(ReservationSpecStatus.RESERVED)
			.build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom1)
			.id(2L)
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final Reservation reservation1 = ReservationFixture.create(List.of(spec1));
		final Reservation reservation2 = ReservationFixture.create(List.of(spec2));
		reservationRepository.saveAll(List.of(reservation1, reservation2));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRepository.findByReservationSpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		// when, then
		assertThatCode(() -> reservationValidateService.validateLabRoomReservationForAccept(labRoom1.getName(), ids))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 현재 날짜를 포함해야 한다.")
	void validateLabRoomReservationForRent_periodNotContainsNow() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final LocalDate now = LocalDate.now();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1).id(1L)
			.period(new RentalPeriod(now, now.plusDays(1))).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom1).id(2L)
			.period(new RentalPeriod(now.minusDays(2), now.minusDays(1))).build();
		final Reservation reservation1 = ReservationFixture.create(List.of(spec1));
		final Reservation reservation2 = ReservationFixture.create(List.of(spec2));
		reservationRepository.saveAll(List.of(reservation1, reservation2));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRepository.findByReservationSpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		// when, then
		assertThatCode(() -> reservationValidateService.validateLabRoomReservationForAccept(labRoom1.getName(), ids))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}
}