package com.girigiri.kwrental.inventory.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse.InventoryResponse;
import com.girigiri.kwrental.inventory.service.InventoryService;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(final InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<?> save(@Login final SessionMember sessionMember,
        @RequestBody @Validated final AddInventoryRequest addInventoryRequest) {
        final Long id = inventoryService.save(sessionMember.getId(), addInventoryRequest);
        return ResponseEntity.created(URI.create("/api/inventories/" + id))
            .build();
    }

    @GetMapping
    public InventoriesResponse find(@Login final SessionMember sessionMember) {
        return inventoryService.getInventories(sessionMember.getId());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll(@Login final SessionMember sessionMember) {
        inventoryService.deleteAll(sessionMember.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@Login final SessionMember sessionMember, @PathVariable Long id) {
        inventoryService.deleteById(sessionMember.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public InventoryResponse update(@Login final SessionMember sessionMember, @PathVariable final Long id,
        @RequestBody @Validated final UpdateInventoryRequest updateInventoryRequest) {
        return inventoryService.update(sessionMember.getId(), id, updateInventoryRequest);
    }
}
