package com.girigiri.kwrental.reservation.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.reservation.domain.Reservation;

public interface ReservationRepository extends Repository<Reservation, Long>, ReservationRepositoryCustom {

	Reservation save(Reservation reservation);

	Optional<Reservation> findById(Long id);

	<S extends Reservation> Iterable<S> saveAll(Iterable<S> reservations);
}
