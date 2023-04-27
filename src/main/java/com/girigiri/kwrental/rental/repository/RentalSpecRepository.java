package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import org.springframework.data.repository.Repository;

public interface RentalSpecRepository extends Repository<RentalSpec, Long>, RentalSpecRepositoryCustom {
}
