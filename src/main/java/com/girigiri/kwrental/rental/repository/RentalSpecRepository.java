package com.girigiri.kwrental.rental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;

public interface RentalSpecRepository extends Repository<AbstractRentalSpec, Long>, RentalSpecRepositoryCustom {
    <S extends AbstractRentalSpec> Iterable<S> saveAll(Iterable<S> entities);

    Optional<AbstractRentalSpec> findById(Long id);

    List<AbstractRentalSpec> findByReservationSpecId(Long reservationSpecId);
}
