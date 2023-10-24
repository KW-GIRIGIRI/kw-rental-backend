package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemAvailableSetterTest {

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private EquipmentAdjuster equipmentAdjuster;
	@InjectMocks
	private ItemAvailableSetter itemAvailableSetter;

	@ParameterizedTest
	@DisplayName("품목의 운영 가능 여부를 수정한다.")
	@CsvSource({"true,false,-1,false", "false,true,1,true", "true,true,0,true", "false,false,0,false"})
	void updateAvailable_trueToFalse(final boolean itemAvailable, final boolean requestAvailable,
		final int expectedOperand, final boolean expectedItemAvailable) {
		// given
		final Item item = ItemFixture.builder().assetId(1L).available(itemAvailable).build();
		doNothing().when(equipmentAdjuster).adjustRentableQuantity(1L, expectedOperand);

		// when
		itemAvailableSetter.updateAvailable(requestAvailable, item);

		// then
		assertThat(item.isAvailable()).isEqualTo(expectedItemAvailable);
	}

	@Test
	@DisplayName("품목이 삭제됐을 때 운영 불가능 처리한다.")
	void updateAvailableWhenItemDeleted() {
		// given
		final Item item = ItemFixture.builder().assetId(1L).available(true).build();
		doNothing().when(equipmentAdjuster).adjustWhenItemDeleted(1, -1, 1L);

		// when
		itemAvailableSetter.updateAvailableWhenItemDeleted(item);

		// then
		assertThat(item.isAvailable()).isFalse();
	}

	@Test
	void batchUpdateAvailableWhenItemsDeleted() {
		// given
		final Item item = ItemFixture.builder().id(1L).assetId(1L).available(true).build();
		doNothing().when(equipmentAdjuster).adjustWhenItemDeleted(1, -1, 1L);
		BDDMockito.given(itemRepository.updateAvailable(List.of(item.getId()), false)).willReturn(1);

		// when
		itemAvailableSetter.batchUpdateAvailableWhenItemsDeleted(List.of(item));

		// then
		verify(equipmentAdjuster).adjustWhenItemDeleted(1, -1, 1L);
	}
}