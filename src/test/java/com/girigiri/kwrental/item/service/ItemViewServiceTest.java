package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.service.EquipmentValidator;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.girigiri.kwrental.item.dto.response.ItemHistory;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemViewServiceTest {

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ItemRetriever itemRetriever;
	@Mock
	private EquipmentValidator equipmentValidator;
	@Mock
	private RentedItemService rentedItemService;
	@InjectMocks
	private ItemViewService itemViewService;

	@Test
	@DisplayName("대여 가능한 품목을 조회한다.")
	void getRentalAvailableItems() {
		// given
		doNothing().when(equipmentValidator).validateExistsById(anyLong());
		given(rentedItemService.getRentedPropertyNumbers(anyLong(), any())).willReturn(Set.of("11111111"));
		final Item item1 = ItemFixture.builder().id(1L).propertyNumber("11111111").available(true).build();
		final Item item2 = ItemFixture.builder().id(2L).propertyNumber("22222222").available(true).build();
		final Item item3 = ItemFixture.builder().id(3L).propertyNumber("33333333").available(false).build();
		given(itemRepository.findByAssetId(any())).willReturn(List.of(item1, item2, item3));

		// when
		final ItemsResponse itemsResponse = itemViewService.getRentalAvailableItems(1L);

		// then
		assertThat(itemsResponse.items()).usingRecursiveFieldByFieldElementComparator()
			.containsOnly(ItemResponse.from(item2));
	}

	@Test
	@DisplayName("품목 히스토리를 페이징해서 조회")
	void getItemHistories() {
		// given
		final String propertyNumber1 = "11111111";
		final String propertyNumber2 = "22222222";
		given(itemRepository.findEquipmentItem(any(Pageable.class), any(Category.class)))
			.willReturn(new PageImpl<>(List.of(new EquipmentItemDto("model1", Category.CAMERA, propertyNumber1),
				new EquipmentItemDto("model2", Category.CAMERA, propertyNumber2))));
		given(
			rentedItemService.getRentalCountsByPropertyNumbersBetweenDate(eq(Set.of(propertyNumber1, propertyNumber2)),
				any(LocalDate.class), any(LocalDate.class)))
			.willReturn(Map.of(propertyNumber1, new RentalCountsDto(propertyNumber1, 1, 1)));

		// when
		final LocalDate now = LocalDate.now();
		final Page<ItemHistory> itemHistories = itemViewService.getItemHistories(PageRequest.of(0, 2), Category.CAMERA,
			now.minusDays(1), now);

		// then
		assertAll(
			() -> assertThat(itemHistories.getTotalElements()).isEqualTo(2L),
			() -> assertThat(itemHistories.getContent()).usingRecursiveFieldByFieldElementComparator()
				.containsExactly(new ItemHistory(Category.CAMERA, "model1", propertyNumber1, 1, 1),
					new ItemHistory(Category.CAMERA, "model2", propertyNumber2, 0, 0))
		);
	}
}