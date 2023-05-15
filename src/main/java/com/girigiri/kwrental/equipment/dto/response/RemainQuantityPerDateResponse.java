package com.girigiri.kwrental.equipment.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RemainQuantityPerDateResponse {
    private LocalDate date;
    private Integer remainQuantity;

    private RemainQuantityPerDateResponse() {
    }

    public RemainQuantityPerDateResponse(final LocalDate date, final Integer remainQuantity) {
        this.date = date;
        this.remainQuantity = remainQuantity;
    }
}
