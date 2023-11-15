package com.girigiri.kwrental.item.service.propertynumberupdate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.exception.ItemsNotSameEquipmentException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.item.service.RentedItemService;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class ItemPropertyNumberUpdaterPerEquipment {

	private final ItemRepository itemRepository;
	private final RentedItemService rentedItemService;

	public int execute(final List<ToBeUpdatedItem> toBeUpdatedItems) {
		if (toBeUpdatedItems == null || toBeUpdatedItems.isEmpty()) return 0;
		validateSameEquipment(toBeUpdatedItems);
		final int updatedCount = itemRepository.updatePropertyNumbers(toBeUpdatedItems);
		rentedItemService.updatePropertyNumbers(toBeUpdatedItems);
		return updatedCount;
	}

	private void validateSameEquipment(final List<ToBeUpdatedItem> toBeUpdatedItems) {
		final Set<Long> equipmentIds = toBeUpdatedItems.stream()
			.map(ToBeUpdatedItem::assetId)
			.collect(Collectors.toSet());
		if (equipmentIds.size() != 1) throw new ItemsNotSameEquipmentException();
	}
}
