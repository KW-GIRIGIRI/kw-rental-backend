package com.girigiri.kwrental.inventory.controller;

import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(final InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Validated AddInventoryRequest addInventoryRequest) {
        final Long id = inventoryService.save(addInventoryRequest);
        return ResponseEntity.created(URI.create("/api/inventories/" + id))
                .build();
    }
}
