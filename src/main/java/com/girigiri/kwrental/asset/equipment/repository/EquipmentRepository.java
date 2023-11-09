package com.girigiri.kwrental.asset.equipment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;

public interface EquipmentRepository extends Repository<Equipment, Long>, EquipmentRepositoryCustom {

	Equipment save(Equipment equipment);

	Optional<Equipment> findById(Long id);

	List<Equipment> findByName(String name);

}
