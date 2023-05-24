package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReservationRepositoryCustom {

    Optional<Reservation> findByIdWithSpecs(Long id);

    Set<Reservation> findNotTerminatedEquipmentReservationsByMemberId(Long memberId);

    void adjustTerminated(Reservation reservation);

    List<Reservation> findByReservationSpecIds(List<Long> reservationSpecIds);
}
