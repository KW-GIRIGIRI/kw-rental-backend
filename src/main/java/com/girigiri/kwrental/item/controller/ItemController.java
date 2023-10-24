package com.girigiri.kwrental.item.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.service.ItemViewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemViewService itemViewService;

    @GetMapping
    public ItemsResponse getItems(final Long equipmentId) {
        return itemViewService.getItems(equipmentId);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable final Long id) {
        return itemViewService.getItem(id);
    }
}
