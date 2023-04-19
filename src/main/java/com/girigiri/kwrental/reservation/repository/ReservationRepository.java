package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import org.springframework.data.repository.Repository;

public interface ReservationRepository extends Repository<Reservation, Long> {

    Reservation save(Reservation reservation);
}
