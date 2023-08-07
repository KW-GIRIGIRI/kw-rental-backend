package com.girigiri.kwrental.rental.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;

public interface RentalSpecRepository extends Repository<RentalSpec, Long>, RentalSpecRepositoryCustom {
	<S extends RentalSpec> Iterable<S> saveAll(Iterable<S> entities);

	Optional<RentalSpec> findById(Long id);
}
