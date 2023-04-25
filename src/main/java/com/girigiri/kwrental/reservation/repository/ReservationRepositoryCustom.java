package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepositoryCustom {

    List<Reservation> findReservationsWithSpecsByStartDate(LocalDate startDate);
}
