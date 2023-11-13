package com.girigiri.kwrental.item.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.ItemSaverPerEquipment;
import com.girigiri.kwrental.asset.equipment.service.ToBeSavedItem;
import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.service.propertynumberupdate.ItemPropertyNumberUpdaterPerEquipment;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class ItemsUpdateUseCase {
	private final ItemSaverPerEquipment itemSaverPerEquipment;
	private final ItemPropertyNumberUpdaterPerEquipment itemPropertyNumberUpdaterPerEquipment;

	public ItemsResponse saveOrUpdate(final Long equipmentId, final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		Map<Boolean, List<UpdateItemRequest>> itemRequestsGroup = groupByIdNull(saveOrUpdateItemsRequest);
		final List<ToBeSavedItem> toBeSavedItems = mapToToBeSavedItems(equipmentId, saveOrUpdateItemsRequest);
		itemSaverPerEquipment.execute(toBeSavedItems);
		final EquipmentItems equipmentItems = getEquipmentItems(equipmentId);
		List<UpdateItemRequest> updateItemRequests = itemRequestsGroup.get(false);
		update(equipmentItems, updateItemRequests);
		deleteNotRequested(equipmentItems, saveOrUpdateItemsRequest.items());
		return ItemsResponse.of(equipmentItems.getItems());
	}

	private List<ToBeSavedItem> mapToToBeSavedItems(final Long equipmentId,
		final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		final List<UpdateItemRequest> itemRequestsForSave = saveOrUpdateItemsRequest.items().stream()
			.filter(it -> it.id() == null).toList();
		return itemRequestsForSave.stream()
			.map(it -> new ToBeSavedItem(it.propertyNumber(), equipmentId)).toList();
	}

	private Map<Boolean, List<UpdateItemRequest>> groupByIdNull(SaveOrUpdateItemsRequest updateItemsRequest) {
		return updateItemsRequest.items().stream().collect(Collectors.groupingBy(it -> it.id() == null));
	}

	private EquipmentItems getEquipmentItems(final Long equipmentId) {
		List<Item> items = itemRepository.findByAssetId(equipmentId);
		return EquipmentItems.from(items);
	}

	private void update(EquipmentItems equipmentItems, List<UpdateItemRequest> updateItemRequests) {
		if (updateItemRequests == null)
			return;
		for (UpdateItemRequest request : updateItemRequests) {
			final Item item = equipmentItems.getItem(request.id());
			updatePropertyNumber(item.getId(), request.propertyNumber());
		}
	}

	private void deleteNotRequested(final EquipmentItems equipmentItems,
		final List<UpdateItemRequest> updateItemRequests) {
		if (updateItemRequests == null)
			return;
		List<Long> notRequestedIds = getNotRequestedIds(equipmentItems, updateItemRequests);
		final List<Item> itemsToDelete = notRequestedIds
			.stream().map(equipmentItems::getItem).toList();
		itemDeleter.batchDelete(itemsToDelete);
		equipmentItems.deleteByIds(notRequestedIds);
	}

	private List<Long> getNotRequestedIds(EquipmentItems equipmentItems,
		List<UpdateItemRequest> updateItemRequests) {
		final List<Long> ids = equipmentItems.getIds();
		final Set<Long> requestedIds = updateItemRequests.stream()
			.map(UpdateItemRequest::id)
			.collect(Collectors.toSet());
		return ids.stream().filter(id -> !requestedIds.contains(id)).toList();
	}
}
