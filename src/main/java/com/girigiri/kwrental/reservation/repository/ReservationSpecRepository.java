package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ReservationSpecRepository extends Repository<ReservationSpec, Long>, ReservationSpecRepositoryCustom {

    ReservationSpec save(ReservationSpec reservationSpec);

    Optional<ReservationSpec> findById(Long id);
}
