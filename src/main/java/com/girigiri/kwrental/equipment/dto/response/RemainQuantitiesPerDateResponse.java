package com.girigiri.kwrental.equipment.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RemainQuantitiesPerDateResponse {
    private List<RemainQuantityPerDateResponse> remainQuantities;

    private RemainQuantitiesPerDateResponse() {
    }

    public RemainQuantitiesPerDateResponse(final List<RemainQuantityPerDateResponse> remainQuantities) {
        this.remainQuantities = remainQuantities;
    }
}
