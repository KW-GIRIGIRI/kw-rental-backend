package com.girigiri.kwrental.item.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.service.EquipmentAdjuster;
import com.girigiri.kwrental.asset.equipment.service.ToBeSavedItem;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.exception.ItemsNotSameEquipmentException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.item.service.save.ItemSaverPerEquipmentImpl;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemSaverPerEquipmentImplTest {

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private EquipmentAdjuster equipmentAdjuster;

	@InjectMocks
	private ItemSaverPerEquipmentImpl saveItemService;

	@Test
	@DisplayName("특정 기자재의 품목들을 저장한다.")
	void execute() {
		// given
		final ToBeSavedItem toBeSavedItem = new ToBeSavedItem("12345678", 1L);
		final List<Item> items = List.of(ItemFixture.builder().propertyNumber("12345678").assetId(1L).build());
		given(itemRepository.saveAll(deepRefEq(items))).willReturn(1);
		doNothing().when(equipmentAdjuster).adjustWhenItemSaved(1, 1L);

		// when
		saveItemService.execute(List.of(toBeSavedItem));

		// then
		verify(itemRepository).saveAll(deepRefEq(items));
	}

	@Test
	@DisplayName("저장하려는 품목의 기자재가 다른 경우 예외가 발생")
	void execute_notSameEquipment() {
		// given
		final ToBeSavedItem toBeSavedItem1 = new ToBeSavedItem("12345678", 1L);
		final ToBeSavedItem toBeSavedItem2 = new ToBeSavedItem("12345678", 2L);

		// when, then
		assertThatThrownBy(() -> saveItemService.execute(List.of(toBeSavedItem1, toBeSavedItem2)))
			.isExactlyInstanceOf(ItemsNotSameEquipmentException.class);
	}
}