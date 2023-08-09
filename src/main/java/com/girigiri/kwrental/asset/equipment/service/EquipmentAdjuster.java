package com.girigiri.kwrental.asset.equipment.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class EquipmentAdjuster {

	private final EquipmentRetriever equipmentRetriever;

	public void adjustWhenItemDeleted(final int deletedCount, final int operandOfRentableQuantity, final Long id) {
		final Equipment equipment = equipmentRetriever.getEquipment(id);
		equipment.adjustToRentalQuantity(operandOfRentableQuantity);
		equipment.reduceTotalCount(deletedCount);
	}

	public void adjustWhenItemSaved(final int savedCount, final Long equipmentId) {
		final Equipment equipment = equipmentRetriever.getEquipment(equipmentId);
		equipment.addTotalCount(savedCount);
		equipment.adjustToRentalQuantity(savedCount);
	}

	public void adjustRentableQuantity(Long equipmentId, int operand) {
		final Equipment equipment = equipmentRetriever.getEquipment(equipmentId);
		equipment.adjustToRentalQuantity(operand);
	}
}
