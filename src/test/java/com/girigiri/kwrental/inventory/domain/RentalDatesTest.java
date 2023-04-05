package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.inventory.exception.RentalDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RentalDatesTest {


    @Test
    @DisplayName("반납 일자가 수령 일자보다 이전이면 예외가 발생")
    void create_endBeforeStart() {
        // given
        final LocalDate start = LocalDate.now().plusDays(2);
        final LocalDate end = LocalDate.now().plusDays(1);

        // when, then
        assertThatThrownBy(() -> new RentalDates(start, end))
                .isExactlyInstanceOf(RentalDateException.class);
    }

    @Test
    @DisplayName("대여 가능한 일 수를 계산한다")
    void getRentalDays() {
        // given
        final LocalDate start = LocalDate.now().plusDays(1);
        final LocalDate end = LocalDate.now().plusDays(1);
        final RentalDates rentalDates = new RentalDates(start, end);

        // when
        final Integer expect = rentalDates.getRentalDays();

        // then
        assertThat(expect).isZero();
    }
}