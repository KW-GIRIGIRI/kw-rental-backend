package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ReservationRepository extends Repository<Reservation, Long>, ReservationRepositoryCustom {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);
}
