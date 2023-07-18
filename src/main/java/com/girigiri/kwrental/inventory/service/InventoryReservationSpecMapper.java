package com.girigiri.kwrental.inventory.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.service.creator.ReservationSpecMapper;

import lombok.RequiredArgsConstructor;

@Component
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
			.rentable(inventory.getRentable())
			.build();
	}
}