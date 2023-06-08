package com.girigiri.kwrental.asset.equipment.domain;

import com.girigiri.kwrental.asset.equipment.exception.RentalQuantityException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class RentalQuantity {

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer remainQuantity;

    protected RentalQuantity() {
    }

    public RentalQuantity(final Integer totalQuantity, final Integer remainQuantity) {
        validateQuantity(totalQuantity, remainQuantity);
        this.totalQuantity = totalQuantity;
        this.remainQuantity = remainQuantity;
    }

    private static void validateQuantity(final int totalQuantity, final int remainingQuantity) {
        if (remainingQuantity < 0) {
            throw new RentalQuantityException("남은 갯수가 음수일 수 없습니다.");
        }
        if (totalQuantity < 0) {
            throw new RentalQuantityException("전체 갯수가 음수일 수 없습니다.");
        }
        if (totalQuantity < remainingQuantity) {
            throw new RentalQuantityException("전체 갯수가 남은 갯수보다 적으면 안됩니다.");
        }
    }
}
