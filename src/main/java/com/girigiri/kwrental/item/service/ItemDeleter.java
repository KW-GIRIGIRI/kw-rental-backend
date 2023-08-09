package com.girigiri.kwrental.item.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemDeleter {

	private final ItemRepository itemRepository;
	private final ItemAvailableSetter itemAvailableSetter;
	private final RentedItemService rentedItemService;

	public int delete(final Item item) {
		rentedItemService.validateNotRentedByPropertyNumbers(List.of(item.getPropertyNumber()));
		final int deletedCount = itemRepository.deleteById(item.getId());
		itemAvailableSetter.updateAvailableWhenItemDeleted(item);
		return deletedCount;
	}

	public int batchDelete(final Collection<Item> items) {
		rentedItemService.validateNotRentedByPropertyNumbers(items.stream().map(Item::getPropertyNumber).toList());
		final int deletedCount = itemRepository.deleteByIdIn(items.stream().map(Item::getId).toList());
		itemAvailableSetter.batchUpdateAvailableWhenItemsDeleted(items);
		return deletedCount;
	}
}
