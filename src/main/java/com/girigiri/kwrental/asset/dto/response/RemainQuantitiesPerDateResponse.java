package com.girigiri.kwrental.asset.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class RemainQuantitiesPerDateResponse {
    private List<RemainQuantityPerDateResponse> remainQuantities;

    private RemainQuantitiesPerDateResponse() {
    }

    public RemainQuantitiesPerDateResponse(final List<RemainQuantityPerDateResponse> remainQuantities) {
        this.remainQuantities = remainQuantities;
    }
}
