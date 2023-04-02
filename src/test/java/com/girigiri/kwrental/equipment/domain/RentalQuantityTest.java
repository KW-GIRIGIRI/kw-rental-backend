package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.equipment.exception.RentalQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalQuantityTest {

    @Test
    @DisplayName("남은 갯수가 음수면 예외가 발생한다.")
    void construct_except_remainingQuantityIsNegative() {
        // given
        int invalidRemainingQuantity = -1;

        // when, then
        assertThatThrownBy(() -> new RentalQuantity(1, invalidRemainingQuantity))
                .isInstanceOf(RentalQuantityException.class)
                .hasMessageContaining("남은 갯수가 음수");
    }

    @Test
    @DisplayName("전체 갯수가 음수면 예외가 발생한다.")
    void construct_except_totalQuantityIsNegative() {
        // given
        int invalidTotalQuantity = -1;

        // when, then
        assertThatThrownBy(() -> new RentalQuantity(invalidTotalQuantity, 1))
                .isInstanceOf(RentalQuantityException.class)
                .hasMessageContaining("전체 갯수가 음수");
    }

    @Test
    @DisplayName("전체 갯수가 남은 갯수보다 작으면 예외가 발생한다.")
    void construct_except_totalQuantityIsSmallerThanRemainingQuantity() {
        // given
        int totalQuantity = 1;
        int remainingQuantity = 2;

        // when, then
        assertThatThrownBy(() -> new RentalQuantity(totalQuantity, remainingQuantity))
                .isInstanceOf(RentalQuantityException.class)
                .hasMessageContaining("전체 갯수가 남은 갯수보다 적으면 안됩니다.");
    }
}
