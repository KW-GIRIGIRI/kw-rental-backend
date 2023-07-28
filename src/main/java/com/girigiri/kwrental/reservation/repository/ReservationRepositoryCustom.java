package com.girigiri.kwrental.reservation.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;

public interface ReservationRepositoryCustom {

    Optional<Reservation> findByIdWithSpecs(Long id);

    List<Reservation> findByIdsWithSpecs(List<Long> ids);

    Set<Reservation> findNotTerminatedEquipmentReservationsByMemberId(Long memberId);

    Set<Reservation> findNotTerminatedLabRoomReservationsByMemberId(Long memberId);

    void adjustTerminated(Reservation reservation);

    List<Reservation> findByReservationSpecIds(List<Long> reservationSpecIds);

    Set<Reservation> findNotTerminatedReservationsByMemberId(Long memberId);

    List<Reservation> findNotTerminatedRelatedReservation(LabRoomReservation from);
}
