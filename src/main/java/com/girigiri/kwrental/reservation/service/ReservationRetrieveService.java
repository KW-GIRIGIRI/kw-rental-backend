package com.girigiri.kwrental.reservation.service;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class ReservationRetrieveService {
	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;
	private final ReservationValidator reservationValidator;

	public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
		return reservationSpecRepository.findEquipmentReservationWhenAccept(startDate);
	}

	public Set<EquipmentReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
		return reservationSpecRepository.findOverdueEquipmentReservationWhenReturn(localDate);
	}

	public Set<EquipmentReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(
		final LocalDate localDate) {
		return reservationSpecRepository.findEquipmentReservationWhenReturn(localDate);
	}

	public Reservation getReservationWithReservationSpecsById(final Long id) {
		return reservationRepository.findByIdWithSpecs(id).orElseThrow(ReservationNotFoundException::new);
	}

	public List<Reservation> getReservationsWithSpecsByIds(final List<Long> ids) {
		return reservationRepository.findByIdsWithSpecs(ids);
	}

	public Map<Long, Set<String>> groupPropertyNumbersByEquipmentId(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		reservationValidator.validateReservationSpecIdContainsAll(reservationId,
			propertyNumbersByReservationSpecId.keySet());
		final Reservation reservation = getReservationWithSpecs(reservationId);
		return groupByEquipmentId(propertyNumbersByReservationSpecId, reservation);
	}

	private Reservation getReservationWithSpecs(final Long reservationId) {
		return reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
	}

	private Map<Long, Set<String>> groupByEquipmentId(final Map<Long, Set<String>> propertyNumbersByReservationSpecId,
		final Reservation reservation) {
		Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
		for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
			final Set<String> propertyNumbers = propertyNumbersByReservationSpecId.get(reservationSpec.getId());
			final Long equipmentId = reservationSpec.getAsset().getId();
			log.info("[DEBUGGING] reservation id : {} \n[DEBUGGER] reservation spec id : {}\n[DEBUGGER] property number is null? {}\n[DEBUGGER] property numbers are {}", reservation.getId(), reservationSpec.getId(), Objects.isNull(propertyNumbers), String.join(", ", propertyNumbers));
			collectedByEquipmentId.put(equipmentId, propertyNumbers);
		}
		return collectedByEquipmentId;
	}

	public List<LabRoomReservation> getLabRoomReservationBySpecIds(List<Long> reservationSpecIds) {
		return reservationRepository.findByReservationSpecIds(reservationSpecIds)
			.stream()
			.map(LabRoomReservation::new)
			.toList();
	}

	public Map<Long, Long> findLabRoomReservationIdsBySpecIds(final List<Long> specIds) {
		return reservationRepository.findByReservationSpecIds(specIds)
			.stream()
			.map(LabRoomReservation::new)
			.collect(toMap(LabRoomReservation::getReservationSpecId, LabRoomReservation::getId));
	}
}
