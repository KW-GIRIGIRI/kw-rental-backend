package com.girigiri.kwrental.rental.service.rent.validator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabRoomRentValidator implements RentValidator<CreateLabRoomRentalRequest> {
	private final ReservationRetrieveService reservationRetrieveService;

	@Override
	public void validate(final CreateLabRoomRentalRequest rentalRequest) {
		validateLabRoomReservationForAccept(rentalRequest);
	}

	private void validateLabRoomReservationForAccept(final CreateLabRoomRentalRequest createLabRoomRentalRequest) {
		final List<LabRoomReservation> labRoomReservations = reservationRetrieveService.getLabRoomReservationBySpecIds(
			createLabRoomRentalRequest.reservationSpecIds());
		labRoomReservations.forEach(labRoomReservation -> {
			labRoomReservation.validateLabRoomName(createLabRoomRentalRequest.name());
			labRoomReservation.validateCanRentNow();
		});
	}
}
