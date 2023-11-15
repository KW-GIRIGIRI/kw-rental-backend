package com.girigiri.kwrental.item.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.item.service.propertynumberupdate.ItemPropertyNumberUpdaterPerEquipment;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;
import com.girigiri.kwrental.item.service.save.ItemSaverPerEquipmentImpl;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

	private final ItemRetriever itemRetriever;
	private final ItemAvailableSetter itemAvailableSetter;
	private final ItemRepository itemRepository;
	private final ItemDeleter itemDeleter;
	private final ItemPropertyNumberUpdaterPerEquipment itemPropertyNumberUpdaterPerEquipment;
	private final ItemsUpdateUseCase itemsUpdateUseCase;

	public void updateAvailable(final Long id, final boolean rentalAvailable) {
		final Item item = itemRetriever.getById(id);
		itemAvailableSetter.updateAvailable(rentalAvailable, item);
	}

	public void updatePropertyNumber(final Long id, final String propertyNumber) {
		Item item = itemRetriever.getById(id);
		final ToBeUpdatedItem toBeUpdatedItem = new ToBeUpdatedItem(item.getId(), item.getAssetId(),
			item.getPropertyNumber(), propertyNumber);
		itemPropertyNumberUpdaterPerEquipment.execute(List.of(toBeUpdatedItem));
	}

	public void delete(final Long id) {
		Item item = itemRetriever.getById(id);
		itemDeleter.delete(item);
	}

	public void saveOrUpdate(final Long equipment, final SaveOrUpdateItemsRequest request) {
		itemsUpdateUseCase.saveOrUpdate(equipment, request);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void setAvailable(final String propertyNumber, final boolean available) {
		Item item = itemRetriever.getByPropertyNumber(propertyNumber);
		itemAvailableSetter.updateAvailable(available, item);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void deleteByAssetId(Long assetId) {
		final List<Item> items = itemRepository.findByAssetId(assetId);
		itemDeleter.batchDelete(items);
	}
}
