package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface ReservationRepositoryCustom {

    Set<ReservationWithMemberNumber> findReservationsWithSpecsByStartDate(LocalDate startDate);

    Optional<Reservation> findByIdWithSpecs(Long id);

    Set<ReservationWithMemberNumber> findOverdueReservationWithSpecs(LocalDate returnDate);

    Set<ReservationWithMemberNumber> findReservationsWithSpecsByEndDate(LocalDate localDate);

    Set<Reservation> findNotTerminatedReservationsByMemberId(Long memberId);
}
