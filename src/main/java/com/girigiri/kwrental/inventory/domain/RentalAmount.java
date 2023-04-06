package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.inventory.exception.RentalAmountException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class RentalAmount {

    @Column(nullable = false)
    private Integer amount;

    protected RentalAmount() {
    }

    public RentalAmount(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RentalAmountException("대여 갯수는 양수어야 합니다.");
        }
        this.amount = amount;
    }
}
