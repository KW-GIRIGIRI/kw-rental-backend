package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRetriever itemRetriever;
	@Mock
	private ItemAvailableSetter itemAvailableSetter;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ItemSaverImpl itemSaver;
	@Mock
	private RentedItemService rentedItemService;
	@Mock
	private ItemDeleter itemDeleter;
	@InjectMocks
	private ItemService itemService;

	@Test
	@DisplayName("기자재에 해당하는 품목들 없으면 추가, 있으면 수정")
	void saveOrUpdate() {
		// given
		Item itemForUpdate = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
		Item savedItem = ItemFixture.builder().id(2L).propertyNumber("1234567").build();

		UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(null, "1234567");
		UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(1L, "7654321");
		SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(
			List.of(updateItemRequest1, updateItemRequest2));
		long equipmentId = 1L;

		doNothing().when(itemSaver).saveItemsWhenUpdate(equipmentId, List.of(updateItemRequest1));
		given(itemRepository.findByAssetId(any()))
			.willReturn(List.of(itemForUpdate, savedItem));

		given(itemRetriever.getById(1L)).willReturn(itemForUpdate);
		doNothing().when(rentedItemService).updatePropertyNumber(itemForUpdate.getPropertyNumber(),
			updateItemRequest2.propertyNumber());

		// when
		ItemsResponse itemsResponse = itemService.saveOrUpdate(equipmentId, updateItemsRequest);

		// then
		assertThat(itemsResponse.items()).hasSize(2);
	}

	@Test
	@DisplayName("기자재에 해당하는 품목들 없으면 추가, 있으면 수정, 만약 요청하지 않은 값은 삭제")
	void saveOrUpdate_deleteNotRequested() {
		// given
		Item itemForUpdate = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
		Item savedItem = ItemFixture.builder().id(2L).propertyNumber("1234567").build();
		Item itemForDelete = ItemFixture.builder().id(3L).propertyNumber("33333333").build();
		UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(null, "1234567");
		UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(1L, "7654321");
		SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(
			List.of(updateItemRequest1, updateItemRequest2));
		long equipmentId = 1L;

		doNothing().when(itemSaver).saveItemsWhenUpdate(equipmentId, List.of(updateItemRequest1));
		given(itemRepository.findByAssetId(any()))
			.willReturn(List.of(itemForUpdate, savedItem, itemForDelete));

		given(itemRetriever.getById(1L)).willReturn(itemForUpdate);
		doNothing().when(rentedItemService).updatePropertyNumber(itemForUpdate.getPropertyNumber(),
			updateItemRequest2.propertyNumber());

		given(itemDeleter.batchDelete(List.of(itemForDelete))).willReturn(1);

		// when
		ItemsResponse itemsResponse = itemService.saveOrUpdate(equipmentId, updateItemsRequest);

		// then
		assertThat(itemsResponse.items()).hasSize(2);
	}
}
