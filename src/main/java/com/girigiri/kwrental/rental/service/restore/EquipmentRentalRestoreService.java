package com.girigiri.kwrental.rental.service.restore;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.Rental;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.RestoreEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RestoreEquipmentRentalRequest.ReturnRentalSpecRequest;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentRentalRestoreService {
	private final ReservationService reservationService;
	private final ItemService itemService;
	private final PenaltySetter penaltySetter;
	private final RentalSpecRepository rentalSpecRepository;

	public void restore(final RestoreEquipmentRentalRequest restoreEquipmentRentalRequest) {
		final Rental rental = getRental(restoreEquipmentRentalRequest.reservationId());
		final Map<Long, RentalSpecStatus> returnRequest = restoreEquipmentRentalRequest.rentalSpecs().stream()
			.collect(toMap(ReturnRentalSpecRequest::id, ReturnRentalSpecRequest::status));
		rental.restore(returnRequest, penaltySetter::setPenalty, this::setIteAvailability);
	}

	private Rental getRental(final Long reservationId) {
		final Reservation reservation = reservationService.getReservationWithReservationSpecsById(
			reservationId);
		final List<EquipmentRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationId(reservationId);
		return Rental.of(rentalSpecs, reservation);
	}

	private void setIteAvailability(final RentalSpec rentalSpec, final Reservation reservation) {
		final EquipmentRentalSpec equipmentRentalSpec = rentalSpec.as(EquipmentRentalSpec.class);
		if (equipmentRentalSpec.isUnavailableAfterReturn()) {
			itemService.setAvailable(equipmentRentalSpec.getPropertyNumber(), false);
		}
		if (rentalSpec.isOverdueReturned()) {
			itemService.setAvailable(equipmentRentalSpec.getPropertyNumber(), true);
		}
	}
}
