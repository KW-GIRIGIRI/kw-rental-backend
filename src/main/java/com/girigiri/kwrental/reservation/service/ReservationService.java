package com.girigiri.kwrental.reservation.service;

import static com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.RelatedReservationsInfoResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationPurposeResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.service.creator.EquipmentReservationCreator;
import com.girigiri.kwrental.reservation.service.creator.LabRoomReservationCreator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationCancelService reservationCancelService;
	private final ReservationReserveService reservationReserveService;
	private final ReservationRetrieveService reservationRetrieveService;
	private final ReservationValidateService reservationValidateService;
	private final ReservationRentalService reservationRentalService;
	private final EquipmentReservationCreator equipmentReservationCreator;
	private final LabRoomReservationCreator labRoomReservationCreator;

	@Transactional
	public void reserveEquipment(final Long memberId, final AddEquipmentReservationRequest addReservationRequest) {
		final List<Reservation> reservations = equipmentReservationCreator.create(memberId, addReservationRequest);
		reservationReserveService.reserve(memberId, reservations, ReserveValidator.noExtraValidation());
	}

	@Transactional
	public Long reserveLabRoom(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		final Reservation reservation = labRoomReservationCreator.create(memberId, addLabRoomReservationRequest);
		reservationReserveService.reserve(memberId, List.of(reservation),
			reservationValidateService::validateAlreadyReservedSamePeriod);
		return reservation.getId();
	}

	@Transactional(readOnly = true)
	public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentsPerYearMonth(final Long equipmentId,
		final YearMonth yearMonth) {
		return reservationRetrieveService.getReservationsByEquipmentsPerYearMonth(equipmentId, yearMonth);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
		return reservationRetrieveService.getReservationsByStartDate(startDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Set<String>> groupPropertyNumbersByEquipmentId(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		reservationValidateService.validateReservationSpecIdContainsAll(reservationId,
			propertyNumbersByReservationSpecId.keySet());
		return reservationRetrieveService.groupPropertyNumbersCountByEquipmentId(reservationId,
			propertyNumbersByReservationSpecId);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateReservationSpecHasSameAmount(final Map<Long, Integer> amountBySpecId) {
		reservationValidateService.validateAmountIsSame(amountBySpecId);
	}


	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
		return reservationRetrieveService.getOverdueReservationsWithMemberNumber(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(
		final LocalDate localDate) {
		return reservationRetrieveService.getReservationsWithMemberNumberByEndDate(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Reservation getReservationWithReservationSpecsById(final Long id) {
		return reservationRetrieveService.getReservationWithReservationSpecsById(id);
	}

	@Transactional(readOnly = true)
	public UnterminatedEquipmentReservationsResponse getUnterminatedEquipmentReservations(final Long memberId) {
		return reservationRetrieveService.getUnterminatedEquipmentReservations(memberId);
	}

	@Transactional(readOnly = true)
	public UnterminatedLabRoomReservationsResponse getUnterminatedLabRoomReservations(final Long memberId) {
		return reservationRetrieveService.getUnterminatedLabRoomReservations(memberId);
	}

	@Transactional
	public Long cancelReservationSpec(final Long reservationSpecId, final Integer amount) {
		return reservationCancelService.cancelReservationSpec(reservationSpecId, amount);
	}

	@Transactional(readOnly = true)
	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForAccept(final LocalDate date) {
		return reservationRetrieveService.getLabRoomReservationForAccept(date);
	}

	@Transactional(readOnly = true)
	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForReturn(final LocalDate date) {
		return reservationRetrieveService.getLabRoomReservationForReturn(date);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void acceptReservation(final Long id, final List<Long> rentedReservationSpecIds) {
		reservationRentalService.acceptReservation(id, rentedReservationSpecIds);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelReserved(final Long memberId) {
		reservationCancelService.cancelReserved(memberId);
	}

	@Transactional(readOnly = true)
	public RelatedReservationsInfoResponse getRelatedReservationsInfo(Long id) {
		return reservationRetrieveService.getRelatedReservationsInfo(id);
	}

	@Transactional(readOnly = true)
	public HistoryStatResponse getHistoryStat(String name, LocalDate startDate, LocalDate endDate) {
		return reservationRetrieveService.getHistoryStat(name, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public ReservationPurposeResponse getPurpose(Long id) {
		return reservationRetrieveService.getPurpose(id);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelByAssetId(Long assetId) {
		reservationCancelService.cancelByAssetId(assetId);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Long> getLabRoomReservationIdsByReservationSpecIds(final List<Long> reservationSpecIds) {
		return reservationReserveService.findLabRoomReservationIdsBySpecIds(reservationSpecIds);
	}
}
