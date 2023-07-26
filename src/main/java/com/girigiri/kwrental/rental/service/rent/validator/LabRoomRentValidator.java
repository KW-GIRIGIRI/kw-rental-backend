package com.girigiri.kwrental.rental.service.rent.validator;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabRoomRentValidator implements RentValidator<CreateLabRoomRentalRequest> {
	private final ReservationService reservationService;

	@Override
	public void validate(final CreateLabRoomRentalRequest rentalRequest) {
		validateLabRoomReservationForAccept(rentalRequest);
	}

	private void validateLabRoomReservationForAccept(final CreateLabRoomRentalRequest createLabRoomRentalRequest) {
		reservationService.validateLabRoomReservationForAccept(createLabRoomRentalRequest.name(),
			createLabRoomRentalRequest.reservationSpecIds());
	}
}
