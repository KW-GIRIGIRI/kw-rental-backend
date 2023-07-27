package com.girigiri.kwrental.reservation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationRentalService {
	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

	void acceptReservation(final Long id, final List<Long> rentedReservationSpecIds) {
		final Reservation reservation = getReservationById(id);
		reservation.acceptAt(LocalDateTime.now());
		reservationSpecRepository.updateStatusByIds(rentedReservationSpecIds, ReservationSpecStatus.RENTED);
	}

	private Reservation getReservationById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(ReservationNotFoundException::new);
	}
}
