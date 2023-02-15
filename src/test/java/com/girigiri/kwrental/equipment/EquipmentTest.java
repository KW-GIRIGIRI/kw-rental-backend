package com.girigiri.kwrental.equipment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.girigiri.kwrental.equipment.exception.EquipmentException;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EquipmentTest {

    @Test
    @DisplayName("남은 갯수가 음수면 예외가 발생한다.")
    void construct_except_remainingQuantityIsNegative() {
        // given
        int invalidRemainingQuantity = -1;

        // when, then
        assertThatThrownBy(() -> Equipment.builder()
                .category(Category.CAMERA)
                .maker("sony")
                .modelName("modelName")
                .totalQuantity(10)
                .remainingQuantity(invalidRemainingQuantity)
                .availableTimeFrom(LocalTime.MAX)
                .availableTimeTo(LocalTime.MIN)
                .description("description").build())
                .isInstanceOf(EquipmentException.class)
                .hasMessageContaining("남은 갯수가 음수");
    }

    @Test
    @DisplayName("전체 갯수가 음수면 예외가 발생한다.")
    void construct_except_totalQuantityIsNegative() {
        // given
        int invalidTotalQuantity = -1;

        // when, then
        assertThatThrownBy(() -> Equipment.builder()
                .category(Category.CAMERA)
                .maker("sony")
                .modelName("modelName")
                .totalQuantity(invalidTotalQuantity)
                .remainingQuantity(0)
                .availableTimeFrom(LocalTime.MAX)
                .availableTimeTo(LocalTime.MIN)
                .description("description").build())
                .isInstanceOf(EquipmentException.class)
                .hasMessageContaining("전체 갯수가 음수");
    }

    @Test
    @DisplayName("전체 갯수가 남은 갯수보다 작으면 예외가 발생한다.")
    void construct_except_totalQuantityIsSmallerThanRemainingQuantity() {
        // given
       int totalQuantity = 1;
       int remainingQuantity = 2;

        // when, then
        assertThatThrownBy(() -> Equipment.builder()
                .category(Category.CAMERA)
                .maker("sony")
                .modelName("modelName")
                .totalQuantity(totalQuantity)
                .remainingQuantity(remainingQuantity)
                .availableTimeFrom(LocalTime.MAX)
                .availableTimeTo(LocalTime.MIN)
                .description("description").build())
                .isInstanceOf(EquipmentException.class)
                .hasMessageContaining("전체 갯수가 남은 갯수보다 적으면 안됩니다.");
    }

    @Test
    @DisplayName("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안된다.")
    void construct_except_toIsFasterThanFrom() {
        // given
        LocalTime from = LocalTime.of(1, 1, 2);
        LocalTime to = LocalTime.of(1, 1, 1);

        // when, then
        assertThatThrownBy(() -> Equipment.builder()
                .category(Category.CAMERA)
                .maker("sony")
                .modelName("modelName")
                .totalQuantity(1)
                .remainingQuantity(0)
                .availableTimeFrom(from)
                .availableTimeTo(to)
                .description("description").build())
                .isInstanceOf(EquipmentException.class)
                .hasMessageContaining("대여 가능 시작 시간보다 대여 가능 종료 시간이 더 빠르면 안됩니다.");
    }
}
