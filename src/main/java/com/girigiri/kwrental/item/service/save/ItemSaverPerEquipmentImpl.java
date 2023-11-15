package com.girigiri.kwrental.item.service.save;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.asset.equipment.service.ItemSaverPerEquipment;
import com.girigiri.kwrental.asset.equipment.service.ToBeSavedItem;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.exception.ItemsNotSameEquipmentException;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemSaverPerEquipmentImpl implements ItemSaverPerEquipment {

	private final ItemRepository itemRepository;
	private final EquipmentAdjuster equipmentAdjuster;

	@Override
	public int execute(final List<ToBeSavedItem> toBeSavedItems) {
		if (toBeSavedItems == null || toBeSavedItems.isEmpty()) return 0;
		validateSameEquipment(toBeSavedItems);
		final List<Item> itemEntities = toBeSavedItems.stream()
			.map(ToBeSavedItem::mapToEntity).toList();
		final int savedCount = itemRepository.saveAll(itemEntities);
		equipmentAdjuster.adjustWhenItemSaved(savedCount, getAssetId(itemEntities));
		return savedCount;
	}

	private void validateSameEquipment(final List<ToBeSavedItem> toBeSavedItems) {
		final Set<Long> equipmentIds = toBeSavedItems.stream()
			.map(ToBeSavedItem::assetId)
			.collect(Collectors.toSet());
		if (equipmentIds.size() != 1) throw new ItemsNotSameEquipmentException();
	}

	private Long getAssetId(final List<Item> itemEntities) {
		return itemEntities.iterator().next().getAssetId();
	}
}
