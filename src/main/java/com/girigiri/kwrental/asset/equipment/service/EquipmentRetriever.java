package com.girigiri.kwrental.asset.equipment.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class EquipmentRetriever {

	private final EquipmentRepository equipmentRepository;

	public Equipment getEquipment(Long id) {
		return equipmentRepository.findById(id)
			.orElseThrow(EquipmentNotFoundException::new);
	}
}
