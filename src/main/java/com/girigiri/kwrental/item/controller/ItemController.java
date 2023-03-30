package com.girigiri.kwrental.item.controller;

import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemServiceImpl itemService;

    public ItemController(final ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ItemsResponse getItems(final Long equipmentId) {
        return itemService.getItems(equipmentId);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable final Long id) {
        return itemService.getItem(id);
    }

    @PatchMapping("/{id}/rentalAvailable")
    public ResponseEntity<?> updateRentalAvailable(@PathVariable final Long id,
                                                   @RequestBody ItemRentalAvailableRequest rentalAvailableRequest) {
        itemService.updateRentalAvailable(id, rentalAvailableRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/propertyNumber")
    public ResponseEntity<?> updatePropertyNumber(@PathVariable final Long id,
                                                  @RequestBody ItemPropertyNumberRequest propertyNumberRequest) {
        itemService.updatePropertyNumber(id, propertyNumberRequest);
        return ResponseEntity.noContent().build();
    }
}
