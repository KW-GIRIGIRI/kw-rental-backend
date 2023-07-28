package com.girigiri.kwrental.reservation.service;

import static com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.RelatedReservationsInfoResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationPurposeResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationViewService {

	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

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

	public UnterminatedEquipmentReservationsResponse getUnterminatedEquipmentReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedEquipmentReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedEquipmentReservationsResponse.from(reservationList);
	}

	public UnterminatedLabRoomReservationsResponse getUnterminatedLabRoomReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedLabRoomReservationsResponse.from(reservationList);
	}

	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForAccept(final LocalDate date) {
		return reservationSpecRepository.findLabRoomReservationsWhenAccept(date);
	}

	public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForReturn(final LocalDate date) {
		return reservationSpecRepository.findLabRoomReservationWhenReturn(date);
	}

	public HistoryStatResponse getHistoryStat(String name, LocalDate startDate, LocalDate endDate) {
		return reservationSpecRepository.findHistoryStat(name, startDate, endDate);
	}

	public ReservationPurposeResponse getPurpose(final Long id) {
		return new ReservationPurposeResponse(getReservationById(id).getPurpose());
	}

	public RelatedReservationsInfoResponse getRelatedReservationsInfo(Long id) {
		Reservation reservation = getReservationById(id);
		LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
		final List<Reservation> reservations = reservationRepository.findNotTerminatedRelatedReservation(
			labRoomReservation);
		return RelatedReservationsInfoResponse.from(reservations);
	}

	private Reservation getReservationById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(ReservationNotFoundException::new);
	}
}
