package com.girigiri.kwrental.rental.service.rent.acceptor;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomReservationAcceptorTest {

	@Mock
	private ReservationService reservationService;
	@InjectMocks
	private LabRoomReservationAcceptor labRoomReservationAcceptor;

	@Test
	@DisplayName("대여된 랩실 대여를 수령 처리한다.")
	void acceptReservations() {
		// given
		final LabRoomRentalSpec spec1 = LabRoomRentalSpecFixture.builder()
			.reservationId(1L)
			.reservationSpecId(2L)
			.build();
		final LabRoomRentalSpec spec2 = LabRoomRentalSpecFixture.builder()
			.reservationId(3L)
			.reservationSpecId(4L)
			.build();

		doNothing().when(reservationService).acceptReservation(any(), anyList());

		// when
		assertThatCode(() -> labRoomReservationAcceptor.acceptReservationsBy(List.of(spec1, spec2)))
			.doesNotThrowAnyException();
		verify(reservationService, times(2)).acceptReservation(any(), anyList());
	}
}