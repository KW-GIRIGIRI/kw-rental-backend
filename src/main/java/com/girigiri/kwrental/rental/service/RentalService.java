package com.girigiri.kwrental.rental.service;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.Rental;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalSpecRequest;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusRequest;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusesRequest;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationsResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.exception.LabRoomRentalSpecNotOneException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.EquipmentRentalDto;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.service.PenaltyService;
import com.girigiri.kwrental.reservation.service.ReservationService;

@Service
public class RentalService {

	private final ItemService itemService;
	private final ReservationService reservationService;
	private final RentalSpecRepository rentalSpecRepository;

	private final PenaltyService penaltyService;

	public RentalService(final ItemService itemService, final ReservationService reservationService,
		final RentalSpecRepository rentalSpecRepository, final PenaltyService penaltyService) {
		this.itemService = itemService;
		this.reservationService = reservationService;
		this.rentalSpecRepository = rentalSpecRepository;
		this.penaltyService = penaltyService;
	}

	@Transactional
	public void rent(final CreateRentalRequest createRentalRequest) {
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId = createRentalRequest.getRentalSpecsRequests()
			.stream()
			.collect(toMap(RentalSpecsRequest::getReservationSpecId, it -> Set.copyOf(it.getPropertyNumbers())));
		Map<Long, Set<String>> collectedByEquipmentId = reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(
			createRentalRequest.getReservationId(), propertyNumbersByReservationSpecId);
		itemService.validatePropertyNumbers(collectedByEquipmentId);
		validateNowRental(collectedByEquipmentId);
		final List<EquipmentRentalSpec> rentalSpecs = mapToRentalSpecs(createRentalRequest);
		rentalSpecRepository.saveAll(rentalSpecs);
		final List<Long> rentedReservationSpecIds = rentalSpecs.stream()
			.map(AbstractRentalSpec::getReservationSpecId)
			.toList();
		reservationService.acceptReservation(createRentalRequest.getReservationId(), rentedReservationSpecIds);
	}

	private void validateNowRental(final Map<Long, Set<String>> collectedByEquipmentId) {
		final Set<String> propertyNumbers = collectedByEquipmentId.values().stream()
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
		final boolean nowRental = rentalSpecRepository.findByPropertyNumbers(propertyNumbers).stream()
			.anyMatch(EquipmentRentalSpec::isNowRental);
		if (nowRental)
			throw new DuplicateRentalException();
	}

	private List<EquipmentRentalSpec> mapToRentalSpecs(final CreateRentalRequest createRentalRequest) {
		return createRentalRequest.getRentalSpecsRequests().stream()
			.map(it -> mapToRentalSpecPerReservationSpec(createRentalRequest.getReservationId(), it))
			.flatMap(List::stream)
			.toList();
	}

	private List<EquipmentRentalSpec> mapToRentalSpecPerReservationSpec(final Long reservationId,
		final RentalSpecsRequest rentalSpecsRequest) {
		final Long reservationSpecId = rentalSpecsRequest.getReservationSpecId();
		return rentalSpecsRequest.getPropertyNumbers().stream()
			.map(propertyNumber -> mapToRentalSpec(reservationId, reservationSpecId, propertyNumber))
			.toList();
	}

	private EquipmentRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId,
		final String propertyNumber) {
		return EquipmentRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.propertyNumber(propertyNumber)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}

	@Transactional(readOnly = true)
	public EquipmentReservationsWithRentalSpecsResponse getReservationsWithRentalSpecsByStartDate(
		final LocalDate localDate) {
		final Set<EquipmentReservationWithMemberNumber> reservations = reservationService.getReservationsByStartDate(
			localDate);
		final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(reservations);
		final List<EquipmentRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
		return EquipmentReservationsWithRentalSpecsResponse.of(reservations, rentalSpecs);
	}

	private Set<Long> getAcceptedReservationSpecIds(
		final Set<EquipmentReservationWithMemberNumber> reservationsWithMemberNumber) {
		return reservationsWithMemberNumber.stream()
			.filter(EquipmentReservationWithMemberNumber::isAccepted)
			.map(EquipmentReservationWithMemberNumber::getReservationSpecIds)
			.flatMap(List::stream)
			.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public ReservationsWithRentalSpecsByEndDateResponse getReservationsWithRentalSpecsByEndDate(
		final LocalDate endDate) {
		final OverdueReservationsWithRentalSpecsResponse overdueReservations = getOverdueReservationsWithRentalSpecs(
			endDate);
		final EquipmentReservationsWithRentalSpecsResponse equipmentReservations = getReservationWithRentalSpecsByEndDate(
			endDate);
		return new ReservationsWithRentalSpecsByEndDateResponse(overdueReservations, equipmentReservations);
	}

	private OverdueReservationsWithRentalSpecsResponse getOverdueReservationsWithRentalSpecs(
		final LocalDate localDate) {
		Set<EquipmentReservationWithMemberNumber> overdueEquipmentReservations = reservationService.getOverdueReservationsWithMemberNumber(
			localDate);
		final Set<Long> overdueReservationSpecsIds = getAcceptedReservationSpecIds(overdueEquipmentReservations);
		final List<EquipmentRentalSpec> overdueRentalSpecs = rentalSpecRepository.findByReservationSpecIds(
			overdueReservationSpecsIds);
		return OverdueReservationsWithRentalSpecsResponse.of(overdueEquipmentReservations, overdueRentalSpecs);
	}

	private EquipmentReservationsWithRentalSpecsResponse getReservationWithRentalSpecsByEndDate(
		final LocalDate localDate) {
		Set<EquipmentReservationWithMemberNumber> equipmentReservations = reservationService.getReservationsWithMemberNumberByEndDate(
			localDate);
		final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(equipmentReservations);
		final List<EquipmentRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecIds(reservationSpecIds);
		return EquipmentReservationsWithRentalSpecsResponse.of(equipmentReservations, rentalSpecs);
	}

	@Transactional
	public void returnRental(final ReturnRentalRequest returnRentalRequest) {
		final Reservation reservation = reservationService.getReservationWithReservationSpecsById(
			returnRentalRequest.getReservationId());
		Rental rental = getRental(reservation, returnRentalRequest);
		final Map<Long, RentalSpecStatus> returnRequest = returnRentalRequest.getRentalSpecs().stream()
			.collect(toMap(ReturnRentalSpecRequest::getId, ReturnRentalSpecRequest::getStatus));
		for (Long rentalSpecId : returnRequest.keySet()) {
			final RentalSpecStatus status = returnRequest.get(rentalSpecId);
			rental.returnByRentalSpecId(rentalSpecId, status);
			final EquipmentRentalSpec rentalSpec = rental.getRentalSpecAs(rentalSpecId, EquipmentRentalSpec.class);
			setPenalty(rentalSpec, reservation.getMemberId());
			setItemAvailable(rentalSpec);
		}
		final boolean hasPenalty = penaltyService.hasOngoingPenalty(reservation.getMemberId());
		if (hasPenalty) {
			reservationService.cancelAll(reservation.getMemberId());
		}
		rental.setReservationStatusAfterModification();
	}

	private Rental getRental(final Reservation reservation, final ReturnRentalRequest returnRentalRequest) {
		final List<EquipmentRentalSpec> rentalSpecList = rentalSpecRepository.findByReservationId(
			returnRentalRequest.getReservationId());
		return Rental.of(rentalSpecList, reservation);
	}

	private void setPenalty(final RentalSpec rentalSpec, final Long memberId) {
		if (rentalSpec.isOverdueReturned() || rentalSpec.isUnavailableAfterReturn()) {
			penaltyService.createOrUpdate(memberId, rentalSpec.getReservationId(), rentalSpec.getReservationSpecId(),
				rentalSpec.getId(), rentalSpec.getStatus());
		} else {
			penaltyService.deleteByRentalSpecIdIfExists(rentalSpec.getId());
		}
	}

	private void setItemAvailable(final EquipmentRentalSpec rentalSpec) {
		if (rentalSpec.isUnavailableAfterReturn()) {
			itemService.setAvailable(rentalSpec.getPropertyNumber(), false);
		}
		if (rentalSpec.isOverdueReturned()) {
			itemService.setAvailable(rentalSpec.getPropertyNumber(), true);
		}
	}

	@Transactional(readOnly = true)
	public EquipmentRentalsDto getEquipmentRentalsBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		final List<EquipmentRentalDto> rentalDtosBetweenDate = rentalSpecRepository.findEquipmentRentalDtosBetweenDate(
			memberId, from, to);
		return new EquipmentRentalsDto(new LinkedHashSet<>(rentalDtosBetweenDate));
	}

	@Transactional(readOnly = true)
	public LabRoomRentalsDto getLabRoomRentalsBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		final List<LabRoomRentalDto> rentalDtosBetweenDate = rentalSpecRepository.findLabRoomRentalDtosBetweenDate(
			memberId, from, to);
		return new LabRoomRentalsDto(new LinkedHashSet<>(rentalDtosBetweenDate));
	}

	@Transactional(readOnly = true)
	public EquipmentRentalSpecsResponse getReturnedRentalSpecs(final String propertyNumber) {
		final List<RentalSpecWithName> rentalSpecsWithName = rentalSpecRepository.findTerminatedWithNameByPropertyNumber(
			propertyNumber);
		Collections.reverse(rentalSpecsWithName);
		return EquipmentRentalSpecsResponse.from(rentalSpecsWithName);
	}

	@Transactional
	public void rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
		final List<Reservation> rentedReservations = reservationService.rentLabRoom(rentLabRoomRequest);
		final List<LabRoomRentalSpec> labRoomRentalSpecs = rentedReservations.stream()
			.map(reservation -> mapToRentalSpec(reservation.getId(), reservation.getReservationSpecs().get(0).getId()))
			.toList();
		rentalSpecRepository.saveAll(labRoomRentalSpecs);
	}

	private LabRoomRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId) {
		return LabRoomRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}

	@Transactional
	public void returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
		final List<Reservation> returnedReservations = reservationService.returnLabRoom(returnLabRoomRequest);
		final List<Long> reservationIds = returnedReservations.stream()
			.map(Reservation::getId)
			.toList();
		rentalSpecRepository.updateNormalReturnedByReservationIds(reservationIds, RentalDateTime.now());
	}

	@Transactional(readOnly = true)
	public LabRoomReservationsResponse getReturnedLabRoomReservation(final String labRoomName, final LocalDate date) {
		final List<LabRoomReservationResponse> labRoomReservationWithRentalSpecs = rentalSpecRepository.getReturnedLabRoomReservationResponse(
			labRoomName, date);
		return new LabRoomReservationsResponse(labRoomReservationWithRentalSpecs);
	}

	@Transactional
	public void updateLabRoomRentalSpecStatuses(
		final UpdateLabRoomRentalSpecStatusesRequest updateLabRoomRentalSpecStatusesRequest) {
		final List<UpdateLabRoomRentalSpecStatusRequest> updateLabRoomRentalSpecStatusRequests = updateLabRoomRentalSpecStatusesRequest.getReservations();
		for (UpdateLabRoomRentalSpecStatusRequest request : updateLabRoomRentalSpecStatusRequests) {
			final Reservation reservation = reservationService.getReservationWithReservationSpecsById(
				request.getReservationId());
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			final Long reservationSpecId = labRoomReservation.getReservationSpecId();
			final List<AbstractRentalSpec> rentalSpecs = rentalSpecRepository.findByReservationSpecId(
				reservationSpecId);
			if (rentalSpecs.size() != 1)
				throw new LabRoomRentalSpecNotOneException();
			final AbstractRentalSpec rentalSpec = rentalSpecs.iterator().next();
			final Rental rental = Rental.of(rentalSpecs, labRoomReservation.getReservation());
			rental.updateStatusByRentalSpecId(rentalSpec.getId(), request.getRentalSpecStatus());
			final LabRoomRentalSpec labRoomRentalSpec = rental.getRentalSpecAs(rentalSpec.getId(),
				LabRoomRentalSpec.class);
			setPenalty(labRoomRentalSpec, labRoomReservation.getReservation().getMemberId());
			rental.setReservationStatusAfterModification();
		}
	}

	@Transactional(readOnly = true)
	public Page<LabRoomReservationResponse> getLabRoomHistory(final String labRoomName, final LocalDate startDate,
		final LocalDate endDate, Pageable pageable) {
		return rentalSpecRepository.getReturnedLabRoomReservationResponse(labRoomName, startDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public EquipmentRentalSpecsResponse getReturnedRentalSpecsInclusive(final String propertyNumber,
		final LocalDate startDate, final LocalDate endDate) {
		List<RentalSpecWithName> rentalSpecWithNames = rentalSpecRepository.findTerminatedWithNameByPropertyAndInclusive(
			propertyNumber, RentalDateTime.from(startDate), RentalDateTime.from(endDate));
		Collections.reverse(rentalSpecWithNames);
		return EquipmentRentalSpecsResponse.from(rentalSpecWithNames);
	}
}
