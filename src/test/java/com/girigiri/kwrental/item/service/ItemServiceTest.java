package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.*;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.exception.NotEnoughAvailableItemException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    EquipmentService equipmentService;

    @Mock
    RentedItemService rentedItemService;

    @InjectMocks
    ItemService itemService;

    @Test
    @DisplayName("품목 조회")
    void getItem() {
        // given
        given(itemRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.getItem(1L))
                .isExactlyInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 품목 조회 예외")
    void getItem_notFound() {
        // given
        given(itemRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.getItem(1L))
                .isExactlyInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 품목의 자산 번호 수정하려면 예외")
    void updatePropertyNumber_Notfound() {
        // given
        given(itemRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.updatePropertyNumber(1L, new ItemPropertyNumberRequest("12345678")))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 품목의 대여 가능 상태를 수정하려면 예외")
    void updateRentalAvailable_Notfound() {
        // given
        given(itemRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.updateRentalAvailable(1L, new ItemRentalAvailableRequest(false)))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("삭제할 품목이 존재하지 않으면 예외")
    void delete_notFound() {
        // given
        given(itemRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.delete(1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("기자재에 해당하는 품목들 없으면 추가, 있으면 수정")
    void saveOrUpdate() {
        // given
        Item itemForUpdate = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
        Item savedItem = ItemFixture.builder().id(2L).propertyNumber("1234567").build();
        given(itemRepository.findByEquipmentId(any()))
                .willReturn(List.of(itemForUpdate, savedItem));

        UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(null, "1234567");
        UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(1L, "7654321");
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(List.of(updateItemRequest1, updateItemRequest2));
        long equipmentId = 1L;

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
        given(itemRepository.saveAll(any())).willReturn(1);
        given(itemRepository.findByEquipmentId(any()))
                .willReturn(List.of(itemForUpdate, savedItem, itemForDelete));
        given(itemRepository.deleteByPropertyNumbers(List.of(itemForDelete.getPropertyNumber()))).willReturn(1L);

        UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(null, "1234567");
        UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(1L, "7654321");
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(List.of(updateItemRequest1, updateItemRequest2));
        long equipmentId = 1L;

        // when
        ItemsResponse itemsResponse = itemService.saveOrUpdate(equipmentId, updateItemsRequest);

        // then
        assertThat(itemsResponse.items()).hasSize(2);
    }

    @Test
    @DisplayName("운영 중인 품목 갯수를 검증한다.")
    void validateAvailableCount() {
        // given
        given(itemRepository.countAvailable(any())).willReturn(10);

        // when
        assertThatThrownBy(() -> itemService.validateAvailableCount(1L, 11))
                .isExactlyInstanceOf(NotEnoughAvailableItemException.class);
    }

    @Test
    @DisplayName("기자재 ID들과 그에 해당하는 자산번호를 검증한다.")
    void validatePropertyNumbers() {
        // given
        final Item item = ItemFixture.builder().equipmentId(1L).propertyNumber("11111111").build();
        given(itemRepository.findByEquipmentIds(any()))
                .willReturn(List.of(item));

        // when, then
        assertThatCode(() -> itemService.validatePropertyNumbers(Map.of(item.getEquipmentId(), Set.of(item.getPropertyNumber()))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("대여 가능한 품목을 조회한다.")
    void getRentalAvailableItems() {
        // given
        doNothing().when(equipmentService).validateExistsById(anyLong());
        given(rentedItemService.getRentedPropertyNumbers(anyLong(), any())).willReturn(Set.of("11111111"));
        final Item item1 = ItemFixture.builder().id(1L).propertyNumber("11111111").available(true).build();
        final Item item2 = ItemFixture.builder().id(2L).propertyNumber("22222222").available(true).build();
        final Item item3 = ItemFixture.builder().id(3L).propertyNumber("33333333").available(false).build();
        given(itemRepository.findByEquipmentId(any())).willReturn(List.of(item1, item2, item3));

        // when
        final ItemsResponse itemsResponse = itemService.getRentalAvailableItems(1L);

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
                .willReturn(new PageImpl<>(List.of(new EquipmentItemDto("model1", Category.CAMERA, propertyNumber1), new EquipmentItemDto("model2", Category.CAMERA, propertyNumber2))));
        given(rentedItemService.getRentalCountsByPropertyNumbersBetweenDate(eq(Set.of(propertyNumber1, propertyNumber2)), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Map.of(propertyNumber1, new RentalCountsDto(propertyNumber1, 1, 1)));

        // when
        final LocalDate now = LocalDate.now();
        final Page<ItemHistory> itemHistories = itemService.getItemHistories(PageRequest.of(0, 2), Category.CAMERA, now.minusDays(1), now);

        // then
        assertAll(
                () -> assertThat(itemHistories.getTotalElements()).isEqualTo(2L),
                () -> assertThat(itemHistories.getContent()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(new ItemHistory(Category.CAMERA, "model1", propertyNumber1, 1, 1),
                                new ItemHistory(Category.CAMERA, "model2", propertyNumber2, 0, 0))
        );
    }
}
