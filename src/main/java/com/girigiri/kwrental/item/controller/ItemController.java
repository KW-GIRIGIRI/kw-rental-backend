package com.girigiri.kwrental.item.controller;

import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
