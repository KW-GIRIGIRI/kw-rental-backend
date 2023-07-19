package com.girigiri.kwrental.reservation.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;

public interface ReservationSpecRepository extends Repository<ReservationSpec, Long>, ReservationSpecRepositoryCustom {

	ReservationSpec save(ReservationSpec reservationSpec);

	Optional<ReservationSpec> findById(Long id);
}
