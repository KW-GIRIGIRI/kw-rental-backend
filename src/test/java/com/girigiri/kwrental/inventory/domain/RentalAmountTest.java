package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.inventory.exception.RentalAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RentalAmountTest {

    @Test
    @DisplayName("대여 갯수는 양수여야 한다..")
    void create_negative() {
        // given
        Integer zero = 0;

        // when, then
        assertThatThrownBy(() -> new RentalAmount(zero))
                .isExactlyInstanceOf(RentalAmountException.class);
    }
}