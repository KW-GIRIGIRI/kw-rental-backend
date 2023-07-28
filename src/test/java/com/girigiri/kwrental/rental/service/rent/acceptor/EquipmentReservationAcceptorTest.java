package com.girigiri.kwrental.rental.service.rent.acceptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.exception.ReservationIdNotSingleValueWhenEquipmentAcceptException;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentReservationAcceptorTest {

	@Mock
	private ReservationService reservationService;
	@InjectMocks
	private EquipmentReservationAcceptor equipmentReservationAcceptor;

	@Test
	@DisplayName("대여된 대여 예약을 수령 처리한다.")
	void acceptReservations() {
		// given
		final EquipmentRentalSpec spec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(1L)
			.reservationSpecId(2L)
			.build();
		final EquipmentRentalSpec spec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(1L)
			.reservationSpecId(3L)
			.build();

		doNothing().when(reservationService).acceptReservation(1L, List.of(2L, 3L));

		// when
		assertThatCode(() -> equipmentReservationAcceptor.acceptReservationsBy(List.of(spec1, spec2)))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("대여된 대여 예약이 여러개이면 예외 발생한다.")
	void acceptReservations_reservationIdIsNotSingleValue() {
		// given
		final EquipmentRentalSpec spec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(1L)
			.reservationSpecId(2L)
			.build();
		final EquipmentRentalSpec spec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(4L)
			.reservationSpecId(3L)
			.build();

		// when
		assertThatThrownBy(() -> equipmentReservationAcceptor.acceptReservationsBy(List.of(spec1, spec2)))
			.isExactlyInstanceOf(ReservationIdNotSingleValueWhenEquipmentAcceptException.class);
	}
}