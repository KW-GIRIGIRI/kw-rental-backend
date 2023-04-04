package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemsRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    EquipmentRepository equipmentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("품목 Bulk Update")
    void saveItems() {
        // given
        given(itemRepository.saveAll(any()))
                .willReturn(1);
        final AddItemRequest addItemRequest = new AddItemRequest("12345678");

        // when
        itemService.saveItems(1L, List.of(addItemRequest));

        // then
        verify(itemRepository).saveAll(any());
    }

    @Test
    @DisplayName("존재하지 않은 기자재의 품목 목록 조회 예외")
    void getItems_notFound() {
        // given
        given(equipmentRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> itemService.getItems(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
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
        UpdateItemsRequest updateItemsRequest = new UpdateItemsRequest(List.of(updateItemRequest1, updateItemRequest2));
        long equipmentId = 1L;

        // when
        ItemsResponse itemsResponse = itemService.saveOrUpdate(equipmentId, updateItemsRequest);

        // then
        assertThat(itemsResponse.items()).hasSize(2);
    }
}
