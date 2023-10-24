package com.girigiri.kwrental.item.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.service.EquipmentValidator;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.girigiri.kwrental.item.dto.response.ItemHistory;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemViewService {

	private final ItemRetriever itemRetriever;
	private final EquipmentValidator equipmentValidator;
	private final ItemRepository itemRepository;
	private final RentedItemService rentedItemService;

	public ItemsResponse getItems(final Long equipmentId) {
		equipmentValidator.validateExistsById(equipmentId);
		final List<Item> items = itemRepository.findByAssetId(equipmentId);
		return ItemsResponse.of(items);
	}

	public ItemResponse getItem(final Long id) {
		final Item item = itemRetriever.getById(id);
		return ItemResponse.from(item);
	}

	@Transactional(readOnly = true)
	public ItemsResponse getRentalAvailableItems(final Long equipmentId) {
		equipmentValidator.validateExistsById(equipmentId);
		final Set<String> rentedPropertyNumbers = rentedItemService.getRentedPropertyNumbers(equipmentId,
			LocalDateTime.now());
		final List<Item> rentalAvailableItems = getRentalAvailableItems(equipmentId, rentedPropertyNumbers);
		return ItemsResponse.of(rentalAvailableItems);
	}

	private List<Item> getRentalAvailableItems(final Long equipmentId, final Set<String> rentedPropertyNumbers) {
		return itemRepository.findByAssetId(equipmentId)
			.stream()
			.filter(it -> canRentalAvailable(rentedPropertyNumbers, it))
			.toList();
	}

	private boolean canRentalAvailable(final Set<String> rentedPropertyNumbers, final Item it) {
		return !rentedPropertyNumbers.contains(it.getPropertyNumber()) && it.isAvailable();
	}

	public Page<ItemHistory> getItemHistories(final Pageable pageable, final Category category, final LocalDate from,
		final LocalDate to) {
		final Page<EquipmentItemDto> itemDtosPage = itemRepository.findEquipmentItem(pageable, category);
		final Set<String> propertyNumbers = itemDtosPage.getContent()
			.stream()
			.map(EquipmentItemDto::propertyNumber)
			.collect(Collectors.toSet());
		final Map<String, RentalCountsDto> rentalCountsByPropertyNumbers = rentedItemService.getRentalCountsByPropertyNumbersBetweenDate(
			propertyNumbers, from, to);
		return itemDtosPage.map(it -> mapToHistory(it, rentalCountsByPropertyNumbers.get(it.propertyNumber())));
	}

	private ItemHistory mapToHistory(final EquipmentItemDto equipmentItemDto, final RentalCountsDto rentalCountsDto) {
		final ItemHistory.ItemHistoryBuilder builder = ItemHistory.builder()
			.modelName(equipmentItemDto.modelName())
			.category(equipmentItemDto.category())
			.propertyNumber(equipmentItemDto.propertyNumber());

		if (rentalCountsDto == null) {
			return builder.normalRentalCount(0).abnormalRentalCount(0).build();
		}
		return builder.normalRentalCount(rentalCountsDto.normalRentalCount())
			.abnormalRentalCount(rentalCountsDto.abnormalRentalCount())
			.build();
	}
}
