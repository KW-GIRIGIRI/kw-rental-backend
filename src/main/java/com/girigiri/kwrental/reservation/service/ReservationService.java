package com.girigiri.kwrental.reservation.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.Reservation;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationCancelService reservationCancelService;
	private final ReservationReserveService reservationReserveService;
	private final ReservationRetrieveService reservationRetrieveService;
	private final ReservationValidateService reservationValidateService;
	private final ReservationRentalService reservationRentalService;

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
		return reservationRetrieveService.getReservationsByEquipmentsPerYearMonth(equipmentId, yearMonth);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
		return reservationRetrieveService.getReservationsByStartDate(startDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		reservationValidateService.validateReservationSpecAmount(reservationId, propertyNumbersByReservationSpecId);
		reservationValidateService.validateReservationSpecIdContainsAll(reservationId,
			propertyNumbersByReservationSpecId.keySet());
		return reservationRetrieveService.groupPropertyNumbersCountByEquipmentId(reservationId,
			propertyNumbersByReservationSpecId);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public List<Reservation> rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
		return reservationRentalService.rentLabRoom(rentLabRoomRequest);
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
	public List<Reservation> returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
		return reservationRentalService.returnLabRoom(returnLabRoomRequest);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelAll(final Long memberId) {
		reservationCancelService.cancelAll(memberId);
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
}
