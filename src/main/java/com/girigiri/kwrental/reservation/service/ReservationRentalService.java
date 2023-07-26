package com.girigiri.kwrental.reservation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.exception.NotSameRentableRentException;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationRentalService {
	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

	List<Reservation> returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
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

	List<Reservation> rentLabRoom(final CreateLabRoomRentalRequest createLabRoomRentalRequest) {
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			createLabRoomRentalRequest.reservationSpecIds());
		validateSameLabRoom(createLabRoomRentalRequest.name(), reservations);
		for (Reservation reservation : reservations) {
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			labRoomReservation.validateCanRentNow();
			rentReservation(labRoomReservation.getId(), List.of(labRoomReservation.getReservationSpecId()));
		}
		return reservations;
	}

	private void validateSameLabRoom(final String labRoomName, final List<Reservation> reservations) {
		final boolean isSameRentable = reservations.stream()
			.allMatch(reservation -> reservation.isOnlyRentFor(labRoomName));
		if (!isSameRentable)
			throw new NotSameRentableRentException();
	}

	void rentReservation(final Long id, final List<Long> rentedReservationSpecIds) {
		final Reservation reservation = getReservationById(id);
		reservation.acceptAt(LocalDateTime.now());
		reservationSpecRepository.updateStatusByIds(rentedReservationSpecIds, ReservationSpecStatus.RENTED);
	}

	private Reservation getReservationById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(ReservationNotFoundException::new);
	}
}
