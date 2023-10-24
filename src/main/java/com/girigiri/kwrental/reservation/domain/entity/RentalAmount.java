package com.girigiri.kwrental.reservation.domain.entity;

import com.girigiri.kwrental.inventory.exception.RentalAmountException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalAmount {

    @Column(nullable = false)
    private Integer amount;

    private RentalAmount(final Integer amount) {
        if (amount == null || amount < 0) {
            throw new RentalAmountException("대여 갯수는 음수 일 수 없습니다.");
        }
        this.amount = amount;
    }

    public static RentalAmount ofPositive(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RentalAmountException("대여 갯수는 양수여야 합니다.");
        }
        return new RentalAmount(amount);
    }

    public RentalAmount subtract(final RentalAmount right) {
        if (this.amount < right.amount) {
            throw new RentalAmountException("대여 갯수가 음수가 될 수 없습니다.");
        }
        return new RentalAmount(this.amount - right.amount);
    }

    public boolean isZero() {
        return this.amount == 0;
    }
}

