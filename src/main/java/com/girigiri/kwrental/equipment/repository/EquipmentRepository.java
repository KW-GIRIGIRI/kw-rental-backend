package com.girigiri.kwrental.equipment.repository;

import com.girigiri.kwrental.equipment.domain.Equipment;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface EquipmentRepository extends Repository<Equipment, Long>, EquipmentRepositoryCustom {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long id);
}
