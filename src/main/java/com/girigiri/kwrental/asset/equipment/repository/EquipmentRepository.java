package com.girigiri.kwrental.asset.equipment.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;

public interface EquipmentRepository extends Repository<Equipment, Long>, EquipmentRepositoryCustom {

	Equipment save(Equipment equipment);

	Optional<Equipment> findById(Long id);

	Optional<Equipment> findByName(String name);
}
