package com.girigiri.kwrental.rental.service.restore;

import static com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusesRequest.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.rental.domain.Rental;
import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusesRequest;
import com.girigiri.kwrental.rental.exception.LabRoomRentalSpecNotOneException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.dto.request.RestoreLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LabRoomRentalRestoreService {

	private final ReservationRetrieveService reservationRetrieveService;
	private final PenaltySetter penaltySetter;
	private final RentalSpecRepository rentalSpecRepository;

	public void normalRestoreAll(final RestoreLabRoomRentalRequest restoreLabRoomRentalRequest) {
		final List<LabRoomReservation> labRoomReservations = getLabRoomReservations(restoreLabRoomRentalRequest);
		final List<AbstractRentalSpec> rentalSpecs = getRentalSpecs(labRoomReservations);
		final List<Rental> rentals = getRentals(labRoomReservations, rentalSpecs);
		rentals.forEach(Rental::normalRestoreAll);
	}

	private List<LabRoomReservation> getLabRoomReservations(
		final RestoreLabRoomRentalRequest restoreLabRoomRentalRequest) {
		final List<LabRoomReservation> labRoomReservations = reservationRetrieveService.getLabRoomReservationBySpecIds(
			restoreLabRoomRentalRequest.reservationSpecIds());
		labRoomReservations.forEach(reservation -> reservation.validateLabRoomName(restoreLabRoomRentalRequest.name()));
		return labRoomReservations;
	}

	private List<AbstractRentalSpec> getRentalSpecs(final Collection<LabRoomReservation> labRoomReservations) {
		final Set<Long> reservationSpecIds = labRoomReservations.stream()
			.map(LabRoomReservation::getReservationSpecId)
			.collect(Collectors.toSet());
		return rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
	}

	private List<Rental> getRentals(final List<LabRoomReservation> labRoomReservations,
		final List<AbstractRentalSpec> rentalSpecs) {
		final Map<Long, List<AbstractRentalSpec>> rentalSpecsByReservationId = rentalSpecs.stream()
			.collect(Collectors.groupingBy(AbstractRentalSpec::getReservationId));
		return labRoomReservations.stream()
			.map(labRoomReservation -> getRental(rentalSpecsByReservationId, labRoomReservation))
			.toList();
	}

	private Rental getRental(final Map<Long, List<AbstractRentalSpec>> rentalSpecsByReservationId,
		final LabRoomReservation labRoomReservation) {
		final List<AbstractRentalSpec> rentalSpecs = rentalSpecsByReservationId.get(labRoomReservation.getId());
		if (rentalSpecs.size() != 1) {
			throw new LabRoomRentalSpecNotOneException();
		}
		return Rental.of(rentalSpecs,
			labRoomReservation.getReservation());
	}

	public void updateRentals(final UpdateLabRoomRentalSpecStatusesRequest updateLabRoomRentalSpecStatusesRequest) {
		final Map<Long, LabRoomReservation> reservationMap = getLabRoomReservationMap(
			updateLabRoomRentalSpecStatusesRequest);
		final Map<Long, AbstractRentalSpec> rentalSpecsByReservationId = getGroupedRentalSpecsByReservationId(
			reservationMap.values());
		for (UpdateLabRoomRentalSpecStatusRequest updateLabRoomRentalSpecStatusRequest : updateLabRoomRentalSpecStatusesRequest.reservations()) {
			final Long reservationId = updateLabRoomRentalSpecStatusRequest.reservationId();
			final LabRoomReservation labRoomReservation = reservationMap.get(reservationId);
			final AbstractRentalSpec rentalSpec = rentalSpecsByReservationId.get(labRoomReservation.getId());
			final Rental rental = Rental.of(List.of(rentalSpec), labRoomReservation.getReservation());
			rental.update(Map.of(rentalSpec.getId(), updateLabRoomRentalSpecStatusRequest.rentalSpecStatus()),
				penaltySetter::setPenalty);
		}
	}

	private Map<Long, AbstractRentalSpec> getGroupedRentalSpecsByReservationId(
		final Collection<LabRoomReservation> reservations) {
		return getRentalSpecs(reservations)
			.stream()
			.collect(Collectors.toMap(AbstractRentalSpec::getReservationId, Function.identity()));
	}

	private Map<Long, LabRoomReservation> getLabRoomReservationMap(
		final UpdateLabRoomRentalSpecStatusesRequest updateLabRoomRentalSpecStatusesRequest) {
		final List<Long> reservationIds = updateLabRoomRentalSpecStatusesRequest.reservations().stream()
			.map(UpdateLabRoomRentalSpecStatusRequest::reservationId).toList();
		return reservationRetrieveService.getReservationsWithSpecsByIds(reservationIds)
			.stream().map(LabRoomReservation::new).toList().stream()
			.collect(Collectors.toMap(LabRoomReservation::getId, Function.identity()));
	}

}
