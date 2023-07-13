package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.exception.AlreadyReservedLabRoomException;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationReserveServiceTest {

	@Mock
	private InventoryService inventoryService;
	@Mock
	private RemainingQuantityServiceImpl remainingQuantityService;
	@Mock
	private PenaltyService penaltyService;
	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private AssetService assetService;
	@InjectMocks
	private ReservationReserveService reservationReserveService;

	@Test
	@DisplayName("기자재 대여 예약을 생성한다.")
	void reserveEquipment() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final Inventory inventory = InventoryFixture.create(equipment, 0L);
		given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory));
		doNothing().when(remainingQuantityService).validateAmount(any(), any(), any());
		doNothing().when(inventoryService).deleteAll(any());
		given(penaltyService.hasOngoingPenalty(any())).willReturn(false);

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final Reservation reservation = Reservation.builder()
			.id(1L)
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(ReservationSpecFixture.create(equipment)))
			.build();
		given(reservationRepository.saveAll(any())).willReturn(List.of(reservation));

		// when, then
		assertThatCode(() -> reservationReserveService.reserveEquipment(1L, addReservationRequest))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("다양한 기간으로 이뤄진 기자재 대여 예약을 생성한다.")
	void reserveEquipment_multiplePeriod() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final LocalDate now = LocalDate.now();
		final RentalPeriod rentalPeriod1 = new RentalPeriod(now, now.plusDays(1));
		final Inventory inventory1 = InventoryFixture.builder(equipment)
			.rentalPeriod(rentalPeriod1)
			.build();
		final RentalPeriod rentalPeriod2 = new RentalPeriod(now.plusDays(1), now.plusDays(2));
		final Inventory inventory2 = InventoryFixture.builder(equipment)
			.rentalPeriod(rentalPeriod2)
			.build();
		given(penaltyService.hasOngoingPenalty(any())).willReturn(false);
		given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory1, inventory2));
		doNothing().when(remainingQuantityService).validateAmount(any(), any(), any());
		doNothing().when(inventoryService).deleteAll(any());

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final Reservation reservation1 = Reservation.builder()
			.id(1L)
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(ReservationSpecFixture.builder(equipment).period(rentalPeriod1).build()))
			.build();
		final Reservation reservation2 = Reservation.builder()
			.id(2L)
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(ReservationSpecFixture.builder(equipment).period(rentalPeriod2).build()))
			.build();
		given(reservationRepository.saveAll(anyList()))
			.willReturn(List.of(reservation1, reservation2));

		// when, then
		assertThatCode(() -> reservationReserveService.reserveEquipment(1L, addReservationRequest))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("이미 진행 중인 페널티가 있으면 기자재 대여를 할 수 없다.")
	void reserveEquipment_hasOngoingPenalty() {
		// given
		given(penaltyService.hasOngoingPenalty(any())).willReturn(true);

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();

		// when, then
		assertThatThrownBy(() -> reservationReserveService.reserveEquipment(1L, addReservationRequest))
			.isExactlyInstanceOf(ReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약을 생성한다.")
	void reserveLabRoom() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().id(1L).build();
		final Inventory inventory = InventoryFixture.create(labRoom, 0L);
		given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory));
		doNothing().when(remainingQuantityService).validateAmount(any(), any(), any());
		doNothing().when(inventoryService).deleteAll(any());
		given(penaltyService.hasOngoingPenalty(any())).willReturn(false);

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final Reservation reservation = Reservation.builder()
			.id(1L)
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(ReservationSpecFixture.create(labRoom)))
			.build();
		given(reservationRepository.saveAll(any())).willReturn(List.of(reservation));

		// when, then
		assertThatCode(() -> reservationReserveService.reserveEquipment(1L, addReservationRequest))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("이미 진행 중인 페널티가 있으면 랩실 대여를 할 수 없다.")
	void reserveLabRoom_hasOngoingPenalty() {
		// given
		given(penaltyService.hasOngoingPenalty(any())).willReturn(true);

		final AddLabRoomReservationRequest addReservationRequest = AddLabRoomReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name")
			.labRoomName("hanul")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.renterCount(5)
			.build();

		// when, then
		assertThatThrownBy(() -> reservationReserveService.reserveLabRoom(1L, addReservationRequest))
			.isExactlyInstanceOf(ReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여를 할 때 랩실을 해당 기간동안 해당 회원이 대여 했으면 안된다.")
	void reserveLabRoom_alreadyLabRoomReserved() {
		// given
		LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		LabRoom labRoom2 = LabRoomFixture.builder().id(2L).build();
		given(assetService.getRentableByName(any())).willReturn(labRoom1);
		ReservationSpec spec = ReservationSpecFixture.builder(labRoom2).build();
		given(reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(anyLong()))
			.willReturn(Set.of(ReservationFixture.create(List.of(spec))));
		given(penaltyService.hasOngoingPenalty(any())).willReturn(false);
		AddLabRoomReservationRequest request = AddLabRoomReservationRequest.builder()
			.renterCount(1)
			.labRoomName(labRoom1.getName())
			.startDate(spec.getStartDate())
			.endDate(spec.getEndDate())
			.build();

		// when, then
		assertThatThrownBy(() -> reservationReserveService.reserveLabRoom(1L, request))
			.isExactlyInstanceOf(AlreadyReservedLabRoomException.class);
	}
}