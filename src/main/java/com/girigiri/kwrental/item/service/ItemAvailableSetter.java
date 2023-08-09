package com.girigiri.kwrental.item.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemAvailableSetter {

	private static final int SINGLE_ITEM_COUNT = 1;

	private final ItemRepository itemRepository;
	private final EquipmentAdjuster equipmentAdjuster;

	public void updateAvailable(final boolean available, final Item item) {
		int operand = getOperandOfRentableQuantity(item, available);
		item.setAvailable(available);
		equipmentAdjuster.adjustRentableQuantity(item.getAssetId(), operand);
	}

	private int getOperandOfRentableQuantity(final Item item, final boolean rentalAvailable) {
		final boolean itemAvailable = item.isAvailable();
		if (itemAvailable && rentalAvailable)
			return 0;
		if (itemAvailable)
			return -1;
		if (rentalAvailable)
			return 1;
		return 0;
	}

	public void updateAvailableWhenItemDeleted(final Item item) {
		final int operandOfRentableQuantity = getOperandOfRentableQuantity(item, false);
		item.setAvailable(false);
		equipmentAdjuster.adjustWhenItemDeleted(SINGLE_ITEM_COUNT, operandOfRentableQuantity, item.getAssetId());
	}

	public void batchUpdateAvailableWhenItemsDeleted(final Collection<Item> items) {
		final List<Long> availableItemIds = items.stream()
			.filter(Item::isAvailable)
			.map(Item::getId)
			.toList();
		final int updatedCounts = itemRepository.updateAvailable(availableItemIds, false);
		equipmentAdjuster.adjustWhenItemDeleted(items.size(), -updatedCounts, items.iterator().next().getAssetId());
	}
}
