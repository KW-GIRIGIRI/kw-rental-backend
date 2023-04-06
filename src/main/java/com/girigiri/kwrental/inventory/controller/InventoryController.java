package com.girigiri.kwrental.inventory.controller;

import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.dto.response.InventoryResponse;
import com.girigiri.kwrental.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

// TODO: 2023/04/06 회원관련 기능이 포함되어야 한다.
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

    @GetMapping
    public InventoriesResponse find() {
        return inventoryService.getInventories();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        inventoryService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        inventoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public InventoryResponse update(@PathVariable final Long id,
                                    @RequestBody @Validated final UpdateInventoryRequest updateInventoryRequest) {
        return inventoryService.update(id, updateInventoryRequest);
    }
}
