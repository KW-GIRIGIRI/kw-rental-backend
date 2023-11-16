package com.girigiri.kwrental.item.dto.request;

import static com.girigiri.kwrental.asset.equipment.dto.request.UpdateEquipmentRequest.*;

import java.util.List;

import jakarta.validation.Valid;

public record SaveOrUpdateItemsRequest(@Valid List<UpdateItemRequest> items) {
}
