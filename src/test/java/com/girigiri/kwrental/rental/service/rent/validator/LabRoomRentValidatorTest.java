package com.girigiri.kwrental.rental.service.rent.validator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationException;
import com.girigiri.kwrental.reservation.service.ReservationService;

@ExtendWith(MockitoExtension.class)
class LabRoomRentValidatorTest {

	@Mock
	private ReservationService reservationService;
	@InjectMocks
	private LabRoomRentValidator labRoomRentValidator;

	@Test
	@DisplayName("랩실 대여 예약을 대여 처리하기 위해 검증한다.")
	void validate_labRoomReservationInvalidForAccept() {
		// given
		final CreateLabRoomRentalRequest createLabRoomRequest = CreateLabRoomRentalRequest.builder()
			.name("name")
			.reservationSpecIds(List.of(1L, 2L))
			.build();

		doThrow(LabRoomReservationException.class).when(reservationService)
			.validateLabRoomReservationForAccept(createLabRoomRequest.name(),
				createLabRoomRequest.reservationSpecIds());

		// when, then
		assertThatCode(() -> labRoomRentValidator.validate(createLabRoomRequest))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}
}