package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.reservation.domain.RentalSpec;
import org.springframework.data.repository.Repository;

public interface RentalSpecRepository extends Repository<RentalSpec, Long>, RentalSpecRepositoryCustom {

    RentalSpec save(RentalSpec rentalSpec);
}
