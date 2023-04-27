package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {

    List<Reservation> findReservationsWithSpecsByStartDate(LocalDate startDate);

    Optional<Reservation> findByIdWithSpecs(Long id);
}
