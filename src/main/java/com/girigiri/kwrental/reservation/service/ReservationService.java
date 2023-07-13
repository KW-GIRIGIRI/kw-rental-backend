package com.girigiri.kwrental.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.RelatedReservationsInfoResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationPurposeResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.exception.NotSameRentableRentException;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;
	private final ReservationCancelService reservationCancelService;
	private final ReservationReserveService reservationReserveService;

	public ReservationService(final ReservationRepository reservationRepository,
		final ReservationSpecRepository reservationSpecRepository,
		final ReservationCancelService reservationCancelService,
		final ReservationReserveService reservationReserveService) {
		this.reservationRepository = reservationRepository;
		this.reservationSpecRepository = reservationSpecRepository;
		this.reservationCancelService = reservationCancelService;
		this.reservationReserveService = reservationReserveService;
	}

	@Transactional
	public void reserveEquipment(final Long memberId, final AddEquipmentReservationRequest addReservationRequest) {
		reservationReserveService.reserveEquipment(memberId, addReservationRequest);
	}

	@Transactional
	public Long reserveLabRoom(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		return reservationReserveService.reserveLabRoom(memberId, addLabRoomReservationRequest);
	}

	@Transactional(readOnly = true)
	public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentsPerYearMonth(final Long equipmentId,
		final YearMonth yearMonth) {
		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();
		final List<ReservationSpec> reservationSpecs = reservationSpecRepository.findNotCanceldByStartDateBetween(
			equipmentId, startOfMonth, endOfMonth);
		final ReservationCalendar calendar = ReservationCalendar.from(startOfMonth, endOfMonth);
		calendar.addAll(reservationSpecs);
		return ReservationsByEquipmentPerYearMonthResponse.from(calendar);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
		return reservationSpecRepository.findEquipmentReservationWhenAccept(startDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
		validateReservationSpecIdContainsAll(reservation, propertyNumbersByReservationSpecId.keySet());
		Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
		for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
			reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
			collectedByEquipmentId.put(reservationSpec.getRentable().getId(),
				propertyNumbersByReservationSpecId.get(reservationSpec.getId()));
		}
		return collectedByEquipmentId;
	}

	private void validateReservationSpecIdContainsAll(final Reservation reservation,
		final Set<Long> reservationSpecIdsFromInput) {
		final Set<Long> reservationSpecIdsFromReservation = reservation.getReservationSpecs().stream()
			.filter(ReservationSpec::isReserved)
			.map(ReservationSpec::getId)
			.collect(Collectors.toSet());
		if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
			reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput))
			return;
		throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public List<Reservation> rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			rentLabRoomRequest.reservationSpecIds());
		validateSameLabRoom(rentLabRoomRequest.name(), reservations);
		for (Reservation reservation : reservations) {
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			labRoomReservation.validateWhenRent();
			acceptReservation(labRoomReservation.getId(), List.of(labRoomReservation.getReservationSpecId()));
		}
		return reservations;
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
		return reservationSpecRepository.findOverdueEquipmentReservationWhenReturn(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(
		final LocalDate localDate) {
		return reservationSpecRepository.findEquipmentReservationWhenReturn(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Reservation getReservationWithReservationSpecsById(final Long id) {
		return reservationRepository.findByIdWithSpecs(id)
			.orElseThrow(ReservationNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public UnterminatedEquipmentReservationsResponse getUnterminatedEquipmentReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedEquipmentReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedEquipmentReservationsResponse.from(reservationList);
	}

	@Transactional(readOnly = true)
	public UnterminatedLabRoomReservationsResponse getUnterminatedLabRoomReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedLabRoomReservationsResponse.from(reservationList);
	}

	@Transactional
	public Long cancelReservationSpec(final Long reservationSpecId, final Integer amount) {
		return reservationCancelService.cancelReservationSpec(reservationSpecId, amount);
	}

	@Transactional(readOnly = true)
	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForAccept(final LocalDate date) {
		return reservationSpecRepository.findLabRoomReservationsWhenAccept(date);
	}

	@Transactional(readOnly = true)
	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForReturn(final LocalDate date) {
		return reservationSpecRepository.findLabRoomReservationWhenReturn(date);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void acceptReservation(final Long id, final List<Long> rentedReservationSpecIds) {
		final Reservation reservation = getReservationById(id);
		reservation.acceptAt(LocalDateTime.now());
		reservationSpecRepository.updateStatusByIds(rentedReservationSpecIds, ReservationSpecStatus.RENTED);
	}

	private Reservation getReservationById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(ReservationNotFoundException::new);
	}

	private void validateSameLabRoom(final String labRoomName, final List<Reservation> reservations) {
		final boolean isSameRentable = reservations.stream()
			.allMatch(reservation -> reservation.isOnlyRentFor(labRoomName));
		if (!isSameRentable)
			throw new NotSameRentableRentException();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public List<Reservation> returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			returnLabRoomRequest.reservationSpecIds());
		validateSameLabRoom(returnLabRoomRequest.name(), reservations);
		for (Reservation reservation : reservations) {
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			labRoomReservation.validateWhenReturn();
			labRoomReservation.normalReturnAll();
		}
		return reservations;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelAll(final Long memberId) {
		reservationCancelService.cancelAll(memberId);
	}

	@Transactional(readOnly = true)
	public RelatedReservationsInfoResponse getRelatedReservationsInfo(Long id) {
		Reservation reservation = getReservationById(id);
		LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
		final List<Reservation> reservations = reservationRepository.findNotTerminatedRelatedReservation(
			labRoomReservation);
		return RelatedReservationsInfoResponse.from(reservations);
	}

	@Transactional(readOnly = true)
	public HistoryStatResponse getHistoryStat(String name, LocalDate startDate, LocalDate endDate) {
		return reservationSpecRepository.findHistoryStat(name, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public ReservationPurposeResponse getPurpose(Long id) {
		return new ReservationPurposeResponse(getReservationById(id).getPurpose());
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelByAssetId(Long assetId) {
		reservationCancelService.cancelByAssetId(assetId);
	}
}
