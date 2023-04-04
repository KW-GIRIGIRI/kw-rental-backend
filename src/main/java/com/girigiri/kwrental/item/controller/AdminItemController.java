package com.girigiri.kwrental.item.controller;

import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemsRequest;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/items")
public class AdminItemController {

    private final ItemServiceImpl itemService;

    public AdminItemController(final ItemServiceImpl itemService) {
        this.itemService = itemService;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable final Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> saveOrUpdate(final Long equipmentId, @RequestBody @Validated UpdateItemsRequest updateItemsRequest) {
        itemService.saveOrUpdate(equipmentId, updateItemsRequest);
        return ResponseEntity.noContent().location(URI.create("/api/items?equipmentId=" + equipmentId)).build();
    }
}
