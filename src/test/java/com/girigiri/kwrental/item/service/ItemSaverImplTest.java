package com.girigiri.kwrental.item.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemSaverImplTest {

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private EquipmentAdjuster equipmentAdjuster;

	@InjectMocks
	private ItemSaverImpl saveItemService;

	@Test
	@DisplayName("품목을 여러개 저장한다.")
	void saveItems() {
		// given
		given(itemRepository.saveAll(any()))
			.willReturn(1);
		final AddItemRequest addItemRequest = new AddItemRequest("12345678");

		// when
		saveItemService.saveItems(1L, List.of(addItemRequest));

		// then
		verify(itemRepository).saveAll(any());
	}

	@Test
	@DisplayName("품목 목록을 수정할 때 품목을 저장한다.")
	void saveItemsWhenUpdate() {
		// given
		final UpdateItemRequest updateItemRequest = new UpdateItemRequest(1L, "12345678");
		final List<Item> items = List.of(ItemFixture.builder().propertyNumber("12345678").assetId(2L).build());
		given(itemRepository.saveAll(deepRefEq(items))).willReturn(1);
		doNothing().when(equipmentAdjuster).adjustWhenItemSaved(1, 2L);

		// when
		saveItemService.saveItemsWhenUpdate(2L, List.of(updateItemRequest));

		// then
		verify(itemRepository).saveAll(deepRefEq(items));
	}
}