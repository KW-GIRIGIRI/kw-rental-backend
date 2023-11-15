package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.asset.equipment.service.ItemSaverPerEquipment;
import com.girigiri.kwrental.asset.equipment.service.ToBeSavedItem;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.service.propertynumberupdate.ItemPropertyNumberUpdaterPerEquipment;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ItemsUpdateUseCaseTest {

    @Mock
    private ItemSaverPerEquipment itemSaverPerEquipment;
    @Mock
    private ItemPropertyNumberUpdaterPerEquipment itemPropertyNumberUpdaterPerEquipment;
    @Mock
    private ItemRetriever itemRetriever;
    @Mock
    private ItemDeleter itemDeleter;

    @InjectMocks
    private ItemsUpdateUseCase itemsUpdateUseCase;
    @Test
    @DisplayName("기자재에 해당하는 품목들 없으면 추가, 있으면 수정")
    void saveOrUpdate() {
        // given
        Item itemForUpdate = ItemFixture.builder().id(1L).assetId(2L).propertyNumber("11111111").build();
        Item updatedItem = ItemFixture.builder().id(1L).assetId(2L).propertyNumber("7654321").build();
        Item savedItem = ItemFixture.builder().id(2L).propertyNumber("1234567").build();
        Item itemForDelete = ItemFixture.builder().id(3L).propertyNumber("17263544").build();

        UpdateItemRequest updateItemRequest1 = new UpdateItemRequest(null, "1234567");
        UpdateItemRequest updateItemRequest2 = new UpdateItemRequest(1L, "7654321");
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(
                List.of(updateItemRequest1, updateItemRequest2));

        given(itemRetriever.getByAssetId(2L)).willReturn(List.of(itemForUpdate,  itemForDelete));
        given(itemDeleter.batchDelete(List.of(itemForDelete))).willReturn(1);

        given(itemSaverPerEquipment.execute(List.of(new ToBeSavedItem("1234567", 2L))))
                .willReturn(1);

        given(itemRetriever.getByIds(Set.of(1L))).willReturn(List.of(itemForUpdate));
        given(itemPropertyNumberUpdaterPerEquipment.execute(List.of(new ToBeUpdatedItem(1L, 2L, "11111111", "7654321"))))
                .willReturn(1);

        // when, then
        assertThatCode(() -> itemsUpdateUseCase.saveOrUpdate(2L, updateItemsRequest))
                .doesNotThrowAnyException();
    }
}