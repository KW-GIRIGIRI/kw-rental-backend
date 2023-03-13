package com.girigiri.kwrental.equipment;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.girigiri.kwrental.equipment.domain.RentalDays;
import com.girigiri.kwrental.equipment.exception.RentalDaysException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RentalDaysTest {

    @ParameterizedTest
    @CsvSource({"1, 0, 0", "1, 1, 1", "1, 2, 1"})
    @DisplayName("대여 관련 제약 조건을 생성할 수 있다.")
    void construct(int rentalDays, int maxDaysBeforeRental, int minDaysBeforeRental) {
        assertThatCode(() -> new RentalDays(rentalDays, maxDaysBeforeRental, minDaysBeforeRental))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("대여 가능 일 수가 양수가 아니면 예외가 발생한다.")
    void construct_exception_rentalDaysNotPositive(int invalidRentalDays) {
        // given, when, then
        assertThatThrownBy(() -> new RentalDays(invalidRentalDays, 10, 1))
                .isExactlyInstanceOf(RentalDaysException.class)
                .hasMessageContaining("대여 가능 일 수는 항상 양수");
    }

    @Test
    @DisplayName("최대 대여 예약 가능 일 수가 음수면 예외가 발생한다.")
    void construct_exception_maxDaysBeforeRentalNotPositive() {
        // given
        int invalidMaxDaysBeforeRental = -1;

        // when, then
        assertThatThrownBy(() -> new RentalDays(1, invalidMaxDaysBeforeRental, 1))
                .isExactlyInstanceOf(RentalDaysException.class)
                .hasMessageContaining("최대 대여 예약 가능 일 수가 음수");
    }

    @Test
    @DisplayName("최소 대여 예약 가능 일 수가 음수면 예외가 발생한다.")
    void construct_exception_minDaysBeforeRentalNotPositive() {
        // given
        int invalidMinDaysBeforeRental = -1;

        // when, then
        assertThatThrownBy(() -> new RentalDays(1, 10, invalidMinDaysBeforeRental))
                .isExactlyInstanceOf(RentalDaysException.class)
                .hasMessageContaining("최소 대여 예약 가능 일 수가 음수");
    }

    @Test
    @DisplayName("최대 대여 예약 가능 일 수가 최소 대여 예약 가능 일 수보다 작으면 예외가 발생한다.")
    void construct_exception_maxDaysBeforeRentalSmallerThanMinDaysBeforeRental() {
        // given
        int maxDaysBeforeRental = 0;
        int minDaysBeforeRental = 1;

        // when, then
        assertThatThrownBy(() -> new RentalDays(1, maxDaysBeforeRental, minDaysBeforeRental))
                .isExactlyInstanceOf(RentalDaysException.class)
                .hasMessageContaining("최대 대여 예약 가능 일 수가 최소 대여 예약 가능 일 수보다 작으면 안됩니다.");
    }
}
