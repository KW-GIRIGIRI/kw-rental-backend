package com.girigiri.kwrental.inventory.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.service.reserve.creator.ReservationSpecMapper;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class InventoryReservationSpecMapper implements ReservationSpecMapper {
	private final InventoryService inventoryService;

	@Override
	public List<ReservationSpec> map(final Long memberId) {
		final List<Inventory> inventories = inventoryService.getInventoriesWithEquipment(memberId);
		inventoryService.deleteAll(memberId);
		return mapToSpecs(inventories);
	}

	private List<ReservationSpec> mapToSpecs(final List<Inventory> inventories) {
		return inventories.stream()
			.map(this::mapToReservationSpec)
			.toList();
	}

	private ReservationSpec mapToReservationSpec(final Inventory inventory) {
		return ReservationSpec.builder()
			.period(inventory.getRentalPeriod())
			.amount(inventory.getRentalAmount())
			.asset(inventory.getAsset())
			.build();
	}
}
