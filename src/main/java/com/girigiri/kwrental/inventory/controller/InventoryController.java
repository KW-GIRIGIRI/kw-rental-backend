package com.girigiri.kwrental.inventory.controller;

import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.interceptor.UserMember;
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
    public ResponseEntity<?> save(@UserMember final SessionMember sessionMember, @RequestBody @Validated final AddInventoryRequest addInventoryRequest) {
        final Long id = inventoryService.save(sessionMember.getId(), addInventoryRequest);
        return ResponseEntity.created(URI.create("/api/inventories/" + id))
                .build();
    }

    @GetMapping
    public InventoriesResponse find(@UserMember final SessionMember sessionMember) {
        return inventoryService.getInventories(sessionMember.getId());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll(@UserMember final SessionMember sessionMember) {
        inventoryService.deleteAll(sessionMember.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@UserMember final SessionMember sessionMember, @PathVariable Long id) {
        inventoryService.deleteById(sessionMember.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public InventoryResponse update(@UserMember final SessionMember sessionMember, @PathVariable final Long id,
                                    @RequestBody @Validated final UpdateInventoryRequest updateInventoryRequest) {
        return inventoryService.update(sessionMember.getId(), id, updateInventoryRequest);
    }
}
