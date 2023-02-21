package com.girigiri.kwrental.equipment;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

public interface EquipmentRepository extends Repository<Equipment, Long> {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long id);

    Slice<Equipment> findEquipmentsBy(Pageable pageable);
}
