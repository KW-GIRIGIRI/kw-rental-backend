package com.girigiri.kwrental.equipment.repository;

import com.girigiri.kwrental.equipment.domain.Equipment;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

public interface EquipmentRepository extends Repository<Equipment, Long>, EquipmentRepositoryCustom {
    void deleteById(@NonNull Long id);

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long id);
}
