package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import org.springframework.data.repository.Repository;

public interface RentalSpecRepository extends Repository<EquipmentRentalSpec, Long>, RentalSpecRepositoryCustom {
    <S extends EquipmentRentalSpec> Iterable<S> saveAll(Iterable<S> entities);
}
