package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemsUpdateUseCase {

    public ItemsResponse saveOrUpdate(final Long equipmentId, final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
        Map<Boolean, List<UpdateItemRequest>> itemRequestsGroup = groupByIdNull(saveOrUpdateItemsRequest);
        List<UpdateItemRequest> saveItemRequests = itemRequestsGroup.get(true);
        itemSaver.saveItemsWhenUpdate(equipmentId, saveItemRequests);
        final EquipmentItems equipmentItems = getEquipmentItems(equipmentId);
        List<UpdateItemRequest> updateItemRequests = itemRequestsGroup.get(false);
        update(equipmentItems, updateItemRequests);
        deleteNotRequested(equipmentItems, saveOrUpdateItemsRequest.items());
        return ItemsResponse.of(equipmentItems.getItems());
    }
}
