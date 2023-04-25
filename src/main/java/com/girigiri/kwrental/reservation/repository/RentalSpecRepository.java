package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import org.springframework.data.repository.Repository;

public interface RentalSpecRepository extends Repository<ReservationSpec, Long>, RentalSpecRepositoryCustom {

    ReservationSpec save(ReservationSpec reservationSpec);
}
