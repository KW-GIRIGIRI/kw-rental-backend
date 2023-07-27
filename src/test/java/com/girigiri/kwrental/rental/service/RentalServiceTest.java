package com.girigiri.kwrental.rental.service;

import static org.assertj.core.api.Assertions.*;
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
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse.EquipmentReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse.EquipmentReservationWithRentalSpecsResponse.EquipmentReservationSpecWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
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
}