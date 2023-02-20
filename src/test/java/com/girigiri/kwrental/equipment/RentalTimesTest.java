package com.girigiri.kwrental.equipment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.girigiri.kwrental.equipment.exception.EquipmentException;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RentalTimesTest {

    @Test
    @DisplayName("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안된다.")
    void construct_except_toIsFasterThanFrom() {
        // given
        LocalTime from = LocalTime.of(1, 1, 2);
        LocalTime to = LocalTime.of(1, 1, 1);

        // when, then
        assertThatThrownBy(() -> new RentalTimes(from, to))
                .isInstanceOf(EquipmentException.class)
                .hasMessageContaining("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안됩니다.");
    }
}
