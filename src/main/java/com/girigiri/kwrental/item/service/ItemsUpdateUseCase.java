package com.girigiri.kwrental.item.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.ItemSaverPerEquipment;
import com.girigiri.kwrental.asset.equipment.service.ToBeSavedItem;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.service.propertynumberupdate.ItemPropertyNumberUpdaterPerEquipment;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemsUpdateUseCase {
	private final ItemSaverPerEquipment itemSaverPerEquipment;
	private final ItemPropertyNumberUpdaterPerEquipment itemPropertyNumberUpdaterPerEquipment;
	private final ItemRetriever itemRetriever;
	private final ItemDeleter itemDeleter;

	public void saveOrUpdate(final Long equipmentId, final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		if (saveOrUpdateItemsRequest == null || saveOrUpdateItemsRequest.items() == null)
			return;
		final List<Item> toBeDeletedItems = mapToToBeDeletedItems(equipmentId, saveOrUpdateItemsRequest.items());
		itemDeleter.batchDelete(toBeDeletedItems);

		final List<ToBeSavedItem> toBeSavedItems = mapToToBeSavedItems(equipmentId, saveOrUpdateItemsRequest);
		itemSaverPerEquipment.execute(toBeSavedItems);

		final List<ToBeUpdatedItem> toBeUpdatedItems = mapToToBeUpdatedItems(saveOrUpdateItemsRequest);
		itemPropertyNumberUpdaterPerEquipment.execute(toBeUpdatedItems);
	}

	private List<Item> mapToToBeDeletedItems(final Long equipmentId, final List<UpdateItemRequest> updateItemRequests) {
		final Set<Long> requestedIds = updateItemRequests.stream()
			.map(UpdateItemRequest::id)
			.collect(Collectors.toSet());
		final List<Item> itemsPerEquipment = itemRetriever.getByAssetId(equipmentId);
		return itemsPerEquipment.stream()
			.filter(it -> !requestedIds.contains(it.getId()))
			.toList();
	}

	private List<ToBeSavedItem> mapToToBeSavedItems(final Long equipmentId,
		final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		final List<UpdateItemRequest> saveItemRequests = saveOrUpdateItemsRequest.items().stream()
			.filter(it -> it.id() == null).toList();
		return saveItemRequests.stream()
			.map(it -> new ToBeSavedItem(it.propertyNumber(), equipmentId)).toList();
	}

	private List<ToBeUpdatedItem> mapToToBeUpdatedItems(final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		final List<UpdateItemRequest> updateItemRequests = saveOrUpdateItemsRequest.items().stream()
			.filter(it -> it.id() != null).toList();
		final Map<Long, UpdateItemRequest> updateItemRequestGroupById = updateItemRequests.stream()
			.collect(Collectors.toMap(UpdateItemRequest::id, Function.identity()));
		final List<Item> items = itemRetriever.getByIds(updateItemRequestGroupById.keySet());
		return items.stream()
			.filter(it -> it.canUpdatePropertyNumberTo(updateItemRequestGroupById.get(it.getId()).propertyNumber()))
			.map(it -> new ToBeUpdatedItem(it.getId(), it.getAssetId(), it.getPropertyNumber(),
				updateItemRequestGroupById.get(it.getId()).propertyNumber()))
			.toList();
	}
}
