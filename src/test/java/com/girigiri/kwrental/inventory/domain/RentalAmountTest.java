package com.girigiri.kwrental.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.inventory.exception.RentalAmountException;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;

class RentalAmountTest {

    @Test
    @DisplayName("대여 갯수는 양수여야 한다..")
    void create_negative() {
        // given
        Integer zero = 0;

        // when, then
        assertThatThrownBy(() -> RentalAmount.ofPositive(zero))
                .isExactlyInstanceOf(RentalAmountException.class);
    }

    @Test
    @DisplayName("대여 갯수를 뺀다.")
    void subtract() {
        // given
        final RentalAmount rentalAmount = RentalAmount.ofPositive(10);

        // when
        final RentalAmount actual = rentalAmount.subtract(RentalAmount.ofPositive(5));

        // then
        assertThat(actual).isEqualTo(RentalAmount.ofPositive(5));
    }

    @Test
    @DisplayName("대여 갯수를 밸 때 결과가 음수면 예외 발생")
    void subtract_exceptionNegativeResult() {
        // given
        final RentalAmount rentalAmount = RentalAmount.ofPositive(10);

        // when, then
        assertThatThrownBy(() -> rentalAmount.subtract(RentalAmount.ofPositive(11)))
                .isExactlyInstanceOf(RentalAmountException.class);
    }
}