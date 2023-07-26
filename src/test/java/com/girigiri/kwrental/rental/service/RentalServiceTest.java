package com.girigiri.kwrental.rental.service;

import static com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse.EquipmentReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse.EquipmentReservationWithRentalSpecsResponse.EquipmentReservationSpecWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.service.PenaltyService;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

	private final ArgumentCaptor<List<EquipmentRentalSpec>> rentalSpecListArgumentCaptor = ArgumentCaptor.forClass(
		List.class);

	@Mock
	private ItemService itemService;
	@Mock
	private ReservationService reservationService;
	@Mock
	private RentalSpecRepository rentalSpecRepository;
	@Mock
	private PenaltyService penaltyService;
	@InjectMocks
	private RentalService rentalService;

	@Test
	@DisplayName("특정 날짜가 대여 수령일인 대여 예약을 대여 수령 시간과 대여 상세를 함께 조회한다.")
	void getReservationsWithRentalSpecsByStartDate() {
		// given
		final Equipment equipment1 = EquipmentFixture.create();
		final Equipment equipment2 = EquipmentFixture.create();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.id(1L)
			.status(ReservationSpecStatus.RESERVED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.id(2L)
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
			.id(1L)
			.acceptDateTime(RentalDateTime.now())
			.build();
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final EquipmentReservationWithMemberNumber equipmentReservation =
			new EquipmentReservationWithMemberNumber(reservation.getId(), reservation.getName(), "11111111",
				reservation.getAcceptDateTime(), List.of(reservationSpec1));
		given(reservationService.getReservationsByStartDate(any())).willReturn(Set.of(equipmentReservation));
		given(rentalSpecRepository.findByReservationSpecIds(Set.of(reservationSpec1.getId()))).willReturn(
			List.of(rentalSpec1));

		// when
		final EquipmentReservationsWithRentalSpecsResponse response = rentalService.getReservationsWithRentalSpecsByStartDate(
			LocalDate.now());

		assertThat(response.reservations()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				EquipmentReservationWithRentalSpecsResponse.of(equipmentReservation, List.of(rentalSpec1)));
		assertThat(
			response.reservations().get(0).reservationSpecs()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				EquipmentReservationSpecWithRentalSpecsResponse.of(reservationSpec1, List.of(rentalSpec1)));
	}

	@Test
	@DisplayName("연체중과 불량 반납을 포함하여 대여를 반납한다.")
	void returnRental() {
		// given
		final long reservationId = 1L;
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null)
			.period(new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now()))
			.amount(RentalAmount.ofPositive(1))
			.id(1L)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(null)
			.period(new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now()))
			.amount(RentalAmount.ofPositive(2))
			.id(2L)
			.build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(null)
			.period(new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now()))
			.amount(RentalAmount.ofPositive(1))
			.id(3L)
			.build();
		final Reservation reservation = ReservationFixture.builder(
			List.of(reservationSpec1, reservationSpec2, reservationSpec3)).id(reservationId).build();
		given(reservationService.getReservationWithReservationSpecsById(reservationId)).willReturn(reservation);

		final ReturnRentalSpecRequest rentalSpecRequest1 = ReturnRentalSpecRequest.builder()
			.id(2L)
			.status(RentalSpecStatus.RETURNED)
			.build();
		final ReturnRentalSpecRequest rentalSpecRequest2 = ReturnRentalSpecRequest.builder()
			.id(3L)
			.status(RentalSpecStatus.LOST)
			.build();
		final ReturnRentalSpecRequest rentalSpecRequest3 = ReturnRentalSpecRequest.builder()
			.id(4L)
			.status(RentalSpecStatus.OVERDUE_RENTED)
			.build();
		final ReturnRentalSpecRequest rentalSpecRequest4 = ReturnRentalSpecRequest.builder()
			.id(5L)
			.status(RentalSpecStatus.BROKEN)
			.build();
		final ReturnRentalRequest returnReturnRequest = builder()
			.reservationId(reservationId)
			.rentalSpecs(List.of(rentalSpecRequest1, rentalSpecRequest2, rentalSpecRequest3, rentalSpecRequest4))
			.build();

		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec1.getId())
			.id(rentalSpecRequest1.id())
			.propertyNumber("11111111")
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec2.getId())
			.id(rentalSpecRequest2.id())
			.propertyNumber("22222222")
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec2.getId())
			.id(rentalSpecRequest3.id())
			.propertyNumber("33333333")
			.build();
		final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec3.getId())
			.id(rentalSpecRequest4.id())
			.propertyNumber("44444444")
			.build();

		given(rentalSpecRepository.findByReservationId(reservationId)).willReturn(
			List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

		doNothing().when(itemService).setAvailable(rentalSpec2.getPropertyNumber(), false);
		doNothing().when(penaltyService)
			.createOrUpdate(reservation.getMemberId(), reservationId, rentalSpec2.getReservationSpecId(),
				rentalSpec2.getId(), RentalSpecStatus.LOST);
		doNothing().when(itemService).setAvailable(rentalSpec3.getPropertyNumber(), false);
		doNothing().when(penaltyService)
			.createOrUpdate(reservation.getMemberId(), reservationId, rentalSpec3.getReservationSpecId(),
				rentalSpec3.getId(), RentalSpecStatus.OVERDUE_RENTED);
		doNothing().when(itemService).setAvailable(rentalSpec4.getPropertyNumber(), false);
		doNothing().when(penaltyService)
			.createOrUpdate(reservation.getMemberId(), reservationId, rentalSpec4.getReservationSpecId(),
				rentalSpec4.getId(), RentalSpecStatus.BROKEN);

		// when
		assertThatCode(() -> rentalService.returnRental(returnReturnRequest))
			.doesNotThrowAnyException();

		// then
		assertAll(
			() -> assertThat(reservation.isTerminated()).isFalse(),
			() -> assertThat(rentalSpec1.getStatus()).isEqualTo(RentalSpecStatus.RETURNED),
			() -> assertThat(rentalSpec2.getStatus()).isEqualTo(RentalSpecStatus.LOST),
			() -> assertThat(rentalSpec3.getStatus()).isEqualTo(RentalSpecStatus.OVERDUE_RENTED),
			() -> assertThat(rentalSpec4.getStatus()).isEqualTo(RentalSpecStatus.BROKEN),
			() -> assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.RETURNED),
			() -> assertThat(reservationSpec2.getStatus()).isEqualTo(ReservationSpecStatus.OVERDUE_RENTED)
		);
	}

	@Test
	@DisplayName("연체 반납을 포함하여 반납한다.")
	void returnRental_withOverdueReturned() {
		// given
		final long reservationId = 1L;
		final RentalPeriod overduePeriod = new RentalPeriod(LocalDate.now().minusDays(2), LocalDate.now().minusDays(1));
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.RETURNED)
			.period(overduePeriod)
			.amount(RentalAmount.ofPositive(1))
			.id(1L)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.period(overduePeriod)
			.amount(RentalAmount.ofPositive(2))
			.id(2L)
			.build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.ABNORMAL_RETURNED)
			.period(overduePeriod)
			.amount(RentalAmount.ofPositive(1))
			.id(3L)
			.build();
		final Reservation reservation = ReservationFixture.builder(
			List.of(reservationSpec1, reservationSpec2, reservationSpec3)).id(reservationId).build();
		given(reservationService.getReservationWithReservationSpecsById(reservationId)).willReturn(reservation);

		final ReturnRentalSpecRequest rentalSpecRequest3 = ReturnRentalSpecRequest.builder()
			.id(4L)
			.status(RentalSpecStatus.RETURNED)
			.build();
		final ReturnRentalRequest returnReturnRequest = builder()
			.reservationId(reservationId)
			.rentalSpecs(List.of(rentalSpecRequest3)).build();

		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec1.getId())
			.status(RentalSpecStatus.RETURNED)
			.id(2L)
			.propertyNumber("11111111")
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec2.getId())
			.status(RentalSpecStatus.LOST)
			.id(3L)
			.propertyNumber("22222222")
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec2.getId())
			.status(RentalSpecStatus.OVERDUE_RENTED)
			.id(rentalSpecRequest3.id())
			.propertyNumber("33333333")
			.build();
		final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec3.getId())
			.status(RentalSpecStatus.BROKEN)
			.id(5L)
			.propertyNumber("44444444")
			.build();

		given(rentalSpecRepository.findByReservationId(reservationId)).willReturn(
			List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));
		doNothing().when(penaltyService)
			.createOrUpdate(reservation.getMemberId(), reservationId, reservationSpec2.getId(), rentalSpec3.getId(),
				RentalSpecStatus.OVERDUE_RETURNED);
		doNothing().when(itemService).setAvailable(rentalSpec3.getPropertyNumber(), true);

		// when
		assertThatCode(() -> rentalService.returnRental(returnReturnRequest))
			.doesNotThrowAnyException();

		// then
		assertAll(
			() -> assertThat(reservation.isTerminated()).isTrue(),
			() -> assertThat(rentalSpec1.getStatus()).isEqualTo(RentalSpecStatus.RETURNED),
			() -> assertThat(rentalSpec2.getStatus()).isEqualTo(RentalSpecStatus.LOST),
			() -> assertThat(rentalSpec3.getStatus()).isEqualTo(RentalSpecStatus.OVERDUE_RETURNED),
			() -> assertThat(rentalSpec4.getStatus()).isEqualTo(RentalSpecStatus.BROKEN),
			() -> assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.RETURNED),
			() -> assertThat(reservationSpec2.getStatus()).isEqualTo(ReservationSpecStatus.ABNORMAL_RETURNED)
		);
	}

	@Test
	@DisplayName("정상 반납 처리한다.")
	void return_normal() {
		// given
		final RentalPeriod period = new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.RETURNED)
			.period(period)
			.amount(RentalAmount.ofPositive(1))
			.id(1L)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.period(period)
			.amount(RentalAmount.ofPositive(2))
			.id(2L)
			.build();
		final Long reservationId = 1L;
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
			.id(reservationId)
			.build();
		given(reservationService.getReservationWithReservationSpecsById(reservationId)).willReturn(reservation);

		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec1.getId())
			.status(RentalSpecStatus.RETURNED)
			.id(1L)
			.propertyNumber("11111111")
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpec2.getId())
			.status(RentalSpecStatus.LOST)
			.id(2L)
			.propertyNumber("22222222")
			.build();

		given(rentalSpecRepository.findByReservationId(reservationId)).willReturn(List.of(rentalSpec1, rentalSpec2));

		final ReturnRentalSpecRequest rentalSpecRequest1 = ReturnRentalSpecRequest.builder()
			.id(rentalSpec1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final ReturnRentalSpecRequest rentalSpecRequest2 = ReturnRentalSpecRequest.builder()
			.id(rentalSpec2.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();

		final ReturnRentalRequest returnReturnRequest = builder()
			.reservationId(reservationId)
			.rentalSpecs(List.of(rentalSpecRequest1, rentalSpecRequest2)).build();

		// when
		assertThatCode(() -> rentalService.returnRental(returnReturnRequest))
			.doesNotThrowAnyException();
	}
}