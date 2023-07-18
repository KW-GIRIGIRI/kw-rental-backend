package com.girigiri.kwrental.reservation.service.creator;

import static org.assertj.core.api.Assertions.*;
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
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentReservationCreatorTest {

	@Mock
	private InventoryService inventoryService;
	@InjectMocks
	private EquipmentReservationCreator equipmentReservationCreator;

	@Test
	@DisplayName("기자재 대여 예약 객체를 생성한다.")
	void create() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final Inventory inventory = InventoryFixture.create(equipment, 1L);
		given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory));

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final ReservationSpec spec = ReservationSpecFixture.builder(equipment)
			.period(inventory.getRentalPeriod())
			.amount(inventory.getRentalAmount())
			.rentable(inventory.getRentable())
			.build();
		final Reservation reservation = Reservation.builder()
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(spec))
			.memberId(1L)
			.build();

		// when
		final List<Reservation> actual = equipmentReservationCreator.create(1L, addReservationRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(reservation);
	}

	@Test
	@DisplayName("다양한 기간으로 이뤄진 기자재 대여 예약을 생성한다.")
	void create_multiplePeriod() {
		// given
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final LocalDate now = LocalDate.now();
		final RentalPeriod rentalPeriod1 = new RentalPeriod(now, now.plusDays(1));
		final Long memberId = 1L;
		final Inventory inventory1 = InventoryFixture.builder(equipment)
			.rentalPeriod(rentalPeriod1)
			.memberId(memberId)
			.build();
		final RentalPeriod rentalPeriod2 = new RentalPeriod(now.plusDays(1), now.plusDays(2));
		final Inventory inventory2 = InventoryFixture.builder(equipment)
			.rentalPeriod(rentalPeriod2)
			.memberId(memberId)
			.build();
		given(inventoryService.getInventoriesWithEquipment(any())).willReturn(List.of(inventory1, inventory2));

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(equipment)
			.period(inventory1.getRentalPeriod())
			.amount(inventory1.getRentalAmount())
			.rentable(inventory1.getRentable())
			.build();
		final Reservation reservation1 = Reservation.builder()
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.memberId(memberId)
			.reservationSpecs(List.of(spec1))
			.build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(equipment)
			.period(inventory2.getRentalPeriod())
			.amount(inventory2.getRentalAmount())
			.rentable(inventory2.getRentable())
			.build();
		final Reservation reservation2 = Reservation.builder()
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.memberId(memberId)
			.reservationSpecs(List.of(spec2))
			.build();

		// when
		final List<Reservation> actual = equipmentReservationCreator.create(memberId, addReservationRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(reservation1, reservation2);
	}
}