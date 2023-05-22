package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface ReservationRepositoryCustom {

    Optional<Reservation> findByIdWithSpecs(Long id);

    Set<ReservationWithMemberNumber> findUnterminatedReservationsWithSpecsByEndDate(LocalDate localDate);

    Set<Reservation> findNotTerminatedReservationsByMemberId(Long memberId);

    void adjustTerminated(Reservation reservation);
}
