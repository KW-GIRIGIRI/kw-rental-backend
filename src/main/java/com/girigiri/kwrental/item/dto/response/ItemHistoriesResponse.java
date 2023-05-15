package com.girigiri.kwrental.item.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ItemHistoriesResponse {

    private final List<ItemHistory> histories;
    private final Integer page;
    private final List<String> endpoints;

    private ItemHistoriesResponse(final List<ItemHistory> histories, final Integer page, final List<String> endpoints) {
        this.histories = histories;
        this.page = page;
        this.endpoints = endpoints;
    }

    public static ItemHistoriesResponse of(final Page<ItemHistory> page, final List<String> allPageEndPoints) {
        return new ItemHistoriesResponse(page.getContent(), page.getNumber(), allPageEndPoints);
    }
}
