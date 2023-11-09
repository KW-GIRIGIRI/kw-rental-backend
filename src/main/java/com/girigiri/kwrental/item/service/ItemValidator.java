package com.girigiri.kwrental.item.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.domain.ItemsPerEquipments;
import com.girigiri.kwrental.item.exception.NotEnoughAvailableItemException;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class ItemValidator {

	private final ItemRepository itemRepository;

	public void validateAvailableCount(final Long equipmentId, final int amount) {
		final int availableCount = itemRepository.countAvailable(equipmentId);
		if (availableCount < amount) {
			throw new NotEnoughAvailableItemException();
		}
	}

	public void validatePropertyNumbers(final Map<Long, Set<String>> propertyNumbersPerEquipmentId) {
		final Set<Long> equipmentIds = propertyNumbersPerEquipmentId.keySet();
		List<Item> itemsByEquipmentIds = itemRepository.findByEquipmentIds(equipmentIds);
		log.info("founded items : {}", String.join(", ", itemsByEquipmentIds.stream().map(Item::getPropertyNumber).toList()));
		ItemsPerEquipments items = ItemsPerEquipments.from(itemsByEquipmentIds);
		for (final Map.Entry<Long, Set<String>> propertyNumbersByEquipmentId : propertyNumbersPerEquipmentId.entrySet()) {
			final Long equipmentId = propertyNumbersByEquipmentId.getKey();
			final Set<String> propertyNumbers = propertyNumbersByEquipmentId.getValue();
			items.validatePropertyNumbersAvailable(equipmentId, propertyNumbers);
		}
	}
}
