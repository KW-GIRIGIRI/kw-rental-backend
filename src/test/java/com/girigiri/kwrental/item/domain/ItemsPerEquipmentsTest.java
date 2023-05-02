package com.girigiri.kwrental.item.domain;

import com.girigiri.kwrental.item.exception.ItemNotAvailableException;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ItemsPerEquipmentsTest {

    @Test
    @DisplayName("존재하지 않는 기자재 아이디의 품목 자산번호를 검증하려 하면 예외 발생")
    void validatePropertyNumbersAvailable_notExistsEquipmentItem() {
        // given
        final Item item = ItemFixture.builder().equipmentId(1L).build();
        final ItemsPerEquipments items = ItemsPerEquipments.from(List.of(item));

        // when, then
        assertThatThrownBy(() -> items.validatePropertyNumbersAvailable(2L, Set.of("12345678")))
                .isExactlyInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 품목 자산번호를 검증하려 하면 예외 발생")
    void validatePropertyNumbersAvailable_notExistsPropertyNumber() {
        // given
        final Item item = ItemFixture.builder().equipmentId(1L).propertyNumber("11111111").build();
        final ItemsPerEquipments items = ItemsPerEquipments.from(List.of(item));

        // when, then
        assertThatThrownBy(() -> items.validatePropertyNumbersAvailable(1L, Set.of("12345678")))
                .isExactlyInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("품목이 대여 불가능한 상황일 경우 예외 발생")
    void validatePropertyNumbersAvailable_notAvailableItem() {
        // given
        final Item item = ItemFixture.builder().equipmentId(1L).available(false).build();
        final ItemsPerEquipments items = ItemsPerEquipments.from(List.of(item));

        // when, then
        assertThatThrownBy(() -> items.validatePropertyNumbersAvailable(item.getEquipmentId(), Set.of(item.getPropertyNumber())))
                .isExactlyInstanceOf(ItemNotAvailableException.class);
    }
}