package com.girigiri.kwrental.equipment;

import com.girigiri.kwrental.equipment.domain.Equipment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface EquipmentRepository extends Repository<Equipment, Long> {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long id);

    Page<Equipment> findEquipmentsBy(Pageable pageable);
}
