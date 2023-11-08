package com.girigiri.kwrental.asset.equipment.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.exception.DuplicateAssetNameException;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentException;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class EquipmentValidator {

	private final EquipmentRetriever equipmentRetriever;
	private final EquipmentRepository equipmentRepository;

	public void validateExistsById(final Long id) {
		boolean deleted = equipmentRetriever.getEquipment(id).isDeleted();
		if (deleted) {
			throw new EquipmentNotFoundException();
		}
	}

	public void validateNotExistsByName(final String name) {
		equipmentRepository.findByName(name)
			.ifPresent(found -> {
				if (!found.isDeleted())
					throw new DuplicateAssetNameException(name);
			});
	}

	public Equipment validateRentalDays(final Long id, final Integer rentalDays) {
		final Equipment equipment = equipmentRetriever.getEquipment(id);
		if (!equipment.canRentDaysFor(rentalDays)) {
			throw new EquipmentException("최대 대여일 보다 더 길게 대여할 수 없습니다.");
		}
		return equipment;
	}
}
