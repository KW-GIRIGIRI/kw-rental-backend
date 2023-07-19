package com.girigiri.kwrental.reservation.service.creator;

import static org.assertj.core.api.Assertions.*;
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
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentReservationCreatorTest {

	@Mock
	private ReservationSpecMapper reservationSpecMapper;
	@InjectMocks
	private EquipmentReservationCreator equipmentReservationCreator;

	@Test
	@DisplayName("기자재 대여 예약 객체를 생성한다.")
	void create() {
		// given
		final Long memberId = 1L;
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final ReservationSpec spec = ReservationSpecFixture.builder(equipment)
			.rentable(equipment)
			.build();
		given(reservationSpecMapper.map(memberId)).willReturn(List.of(spec));

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final Reservation reservation = createReservation(memberId, spec, addReservationRequest);

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
		final Long memberId = 1L;
		final ReservationSpec spec1 = ReservationSpecFixture.builder(equipment)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(equipment)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();
		given(reservationSpecMapper.map(memberId)).willReturn(List.of(spec1, spec2));

		final AddEquipmentReservationRequest addReservationRequest = AddEquipmentReservationRequest.builder()
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("01012341234")
			.renterName("name").build();
		final Reservation reservation1 = createReservation(memberId, spec1, addReservationRequest);
		final Reservation reservation2 = createReservation(memberId, spec2, addReservationRequest);

		// when
		final List<Reservation> actual = equipmentReservationCreator.create(memberId, addReservationRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(reservation1, reservation2);
	}

	private Reservation createReservation(final Long memberId, final ReservationSpec spec,
		final AddEquipmentReservationRequest addReservationRequest) {
		return Reservation.builder()
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.purpose(addReservationRequest.rentalPurpose())
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.reservationSpecs(List.of(spec))
			.memberId(memberId)
			.build();
	}
}