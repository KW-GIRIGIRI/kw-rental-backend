package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RentalSpecRepository extends Repository<AbstractRentalSpec, Long>, RentalSpecRepositoryCustom {
    <S extends AbstractRentalSpec> Iterable<S> saveAll(Iterable<S> entities);

    Optional<AbstractRentalSpec> findById(Long id);
}
