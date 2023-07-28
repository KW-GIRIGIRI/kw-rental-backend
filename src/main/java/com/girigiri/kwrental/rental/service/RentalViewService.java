package com.girigiri.kwrental.rental.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationsResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.OverdueEquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalViewService {

	private final ReservationRetrieveService reservationRetrieveService;
	private final RentalSpecRepository rentalSpecRepository;

	public EquipmentReservationsWithRentalSpecsResponse getReservationsWithRentalSpecsByStartDate(
		final LocalDate localDate) {
		final Set<EquipmentReservationWithMemberNumber> reservations = reservationRetrieveService.getReservationsByStartDate(
			localDate);
		final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(reservations);
		final List<EquipmentRentalSpec> rentalSpecs = getEquipmentRentalSpecsByReservationIds(reservationSpecIds);
		return EquipmentReservationsWithRentalSpecsResponse.of(reservations, rentalSpecs);
	}

	private List<EquipmentRentalSpec> getEquipmentRentalSpecsByReservationIds(final Set<Long> reservationSpecIds) {
		return rentalSpecRepository.findByReservationSpecIds(reservationSpecIds)
			.stream().map(it -> it.as(EquipmentRentalSpec.class)).toList();
	}

	private Set<Long> getAcceptedReservationSpecIds(
		final Set<EquipmentReservationWithMemberNumber> reservationsWithMemberNumber) {
		return reservationsWithMemberNumber.stream()
			.filter(EquipmentReservationWithMemberNumber::isAccepted)
			.map(EquipmentReservationWithMemberNumber::getReservationSpecIds)
			.flatMap(List::stream)
			.collect(Collectors.toSet());
	}

	public ReservationsWithRentalSpecsByEndDateResponse getReservationsWithRentalSpecsByEndDate(
		final LocalDate endDate) {
		final OverdueEquipmentReservationsWithRentalSpecsResponse overdueReservations = getOverdueReservationsWithRentalSpecs(
			endDate);
		final EquipmentReservationsWithRentalSpecsResponse equipmentReservations = getReservationWithRentalSpecsByEndDate(
			endDate);
		return new ReservationsWithRentalSpecsByEndDateResponse(overdueReservations, equipmentReservations);
	}

	private OverdueEquipmentReservationsWithRentalSpecsResponse getOverdueReservationsWithRentalSpecs(
		final LocalDate localDate) {
		Set<EquipmentReservationWithMemberNumber> overdueEquipmentReservations = reservationRetrieveService.getOverdueReservationsWithMemberNumber(
			localDate);
		final Set<Long> overdueReservationSpecsIds = getAcceptedReservationSpecIds(overdueEquipmentReservations);
		final List<EquipmentRentalSpec> overdueRentalSpecs = getEquipmentRentalSpecsByReservationIds(
			overdueReservationSpecsIds);
		return OverdueEquipmentReservationsWithRentalSpecsResponse.of(overdueEquipmentReservations, overdueRentalSpecs);
	}

	private EquipmentReservationsWithRentalSpecsResponse getReservationWithRentalSpecsByEndDate(
		final LocalDate localDate) {
		Set<EquipmentReservationWithMemberNumber> equipmentReservations = reservationRetrieveService.getReservationsWithMemberNumberByEndDate(
			localDate);
		final Set<Long> reservationSpecIds = getAcceptedReservationSpecIds(equipmentReservations);
		final List<EquipmentRentalSpec> rentalSpecs = getEquipmentRentalSpecsByReservationIds(reservationSpecIds);
		return EquipmentReservationsWithRentalSpecsResponse.of(equipmentReservations, rentalSpecs);
	}

	public EquipmentRentalsDto getEquipmentRentalsBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		final List<EquipmentRentalDto> rentalDtosBetweenDate = rentalSpecRepository.findEquipmentRentalDtosBetweenDate(
			memberId, from, to);
		return new EquipmentRentalsDto(new LinkedHashSet<>(rentalDtosBetweenDate));
	}

	public LabRoomRentalsDto getLabRoomRentalsBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		final List<LabRoomRentalsDto.LabRoomRentalDto> rentalDtosBetweenDate = rentalSpecRepository.findLabRoomRentalDtosBetweenDate(
			memberId, from, to);
		return new LabRoomRentalsDto(new LinkedHashSet<>(rentalDtosBetweenDate));
	}

	public EquipmentRentalSpecsResponse getReturnedRentalSpecs(final String propertyNumber) {
		final List<RentalSpecWithName> rentalSpecsWithName = rentalSpecRepository.findTerminatedWithNameByPropertyNumber(
			propertyNumber);
		Collections.reverse(rentalSpecsWithName);
		return EquipmentRentalSpecsResponse.from(rentalSpecsWithName);
	}

	public LabRoomReservationsResponse getReturnedLabRoomReservation(final String labRoomName, final LocalDate date) {
		final List<LabRoomReservationResponse> labRoomReservationWithRentalSpecs = rentalSpecRepository.getReturnedLabRoomReservationResponse(
			labRoomName, date);
		return new LabRoomReservationsResponse(labRoomReservationWithRentalSpecs);
	}

	public Page<LabRoomReservationResponse> getLabRoomHistory(final String labRoomName, final LocalDate startDate,
		final LocalDate endDate, Pageable pageable) {
		return rentalSpecRepository.getReturnedLabRoomReservationResponse(labRoomName, startDate, endDate, pageable);
	}

	public EquipmentRentalSpecsResponse getReturnedRentalSpecsInclusive(final String propertyNumber,
		final LocalDate startDate, final LocalDate endDate) {
		List<RentalSpecWithName> rentalSpecWithNames = rentalSpecRepository.findTerminatedWithNameByPropertyAndInclusive(
			propertyNumber, RentalDateTime.from(startDate), RentalDateTime.from(endDate));
		Collections.reverse(rentalSpecWithNames);
		return EquipmentRentalSpecsResponse.from(rentalSpecWithNames);
	}
}
