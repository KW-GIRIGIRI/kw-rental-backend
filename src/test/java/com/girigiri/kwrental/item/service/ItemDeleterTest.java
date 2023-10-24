package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemDeleterTest {
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ItemAvailableSetter itemAvailableSetter;
	@Mock
	private RentedItemService rentedItemService;
	@InjectMocks
	private ItemDeleter itemDeleter;

	@Test
	@DisplayName("단일 품목을 삭제한다.")
	void delete() {
		// given
		final Item item = ItemFixture.builder().id(1L).assetId(2L).propertyNumber("12345678").build();
		doNothing().when(rentedItemService).validateNotRentedByPropertyNumbers(List.of(item.getPropertyNumber()));
		given(itemRepository.deleteById(1L)).willReturn(1);
		doNothing().when(itemAvailableSetter).updateAvailableWhenItemDeleted(item);

		// when
		final int actual = itemDeleter.delete(item);

		// then
		assertThat(actual).isOne();
	}

	@Test
	@DisplayName("여러 품목을 삭제한다.")
	void batchDelete() {
		// given
		final Item item = ItemFixture.builder().id(1L).assetId(2L).propertyNumber("12345678").build();
		doNothing().when(rentedItemService).validateNotRentedByPropertyNumbers(List.of(item.getPropertyNumber()));
		given(itemRepository.deleteByIdIn(List.of(1L))).willReturn(1);
		doNothing().when(itemAvailableSetter).batchUpdateAvailableWhenItemsDeleted(List.of(item));

		// when
		final int actual = itemDeleter.batchDelete(List.of(item));

		// then
		assertThat(actual).isOne();
	}
}