package com.girigiri.kwrental.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.asset.equipment.service.ItemSaver;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemSaverImpl implements ItemSaver {

	private final ItemRepository itemRepository;
	private final EquipmentAdjuster equipmentAdjuster;

	@Override
	public void saveItems(final Long equipmentId, final List<AddItemRequest> itemRequests) {
		final List<Item> items = itemRequests.stream()
			.map(it -> mapToItem(equipmentId, it))
			.toList();
		itemRepository.saveAll(items);
	}

	private Item mapToItem(final Long equipmentId, final AddItemRequest addItemRequest) {
		return Item.builder()
			.assetId(equipmentId)
			.propertyNumber(addItemRequest.propertyNumber())
			.available(true)
			.build();
	}

	public void saveItemsWhenUpdate(final Long equipmentId, final List<UpdateItemRequest> saveItemRequests) {
		if (saveItemRequests == null)
			return;
		List<Item> itemsToSave = saveItemRequests.stream().map(it -> mapToItem(equipmentId, it)).toList();
		itemRepository.saveAll(itemsToSave);
		equipmentAdjuster.adjustWhenItemSaved(itemsToSave.size(), equipmentId);
	}

	private Item mapToItem(final Long equipmentId, final UpdateItemRequest updateItemRequest) {
		return Item.builder().propertyNumber(updateItemRequest.propertyNumber()).available(true)
			.assetId(equipmentId).build();
	}
}
