package com.girigiri.kwrental.item.domain;

import com.girigiri.kwrental.item.exception.EquipmentItemsException;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipmentItemsTest {

    @Test
    @DisplayName("중복된 ID를 가진 품목으로 생성할 경우 예외")
    void from_duplicateId() {
        // given
        Item item1 = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
        Item item2 = ItemFixture.builder().id(1L).propertyNumber("22222222").build();

        // when, then
        assertThatThrownBy(() -> EquipmentItems.from(List.of(item1, item2)))
                .isExactlyInstanceOf(EquipmentItemsException.class);
    }

    @Test
    @DisplayName("ID가 null인 품목으로 생성할 경우 예외")
    void from_nullId() {
        // given
        Item item = ItemFixture.builder().id(null).propertyNumber("22222222").build();

        // when, then
        assertThatThrownBy(() -> EquipmentItems.from(List.of(item)))
                .isExactlyInstanceOf(EquipmentItemsException.class);
    }

    @Test
    @DisplayName("중복된 자산번호를 가진 품목으로 생성할 경우 예외")
    void from_duplicatePropertyNumber() {
        // given
        Item item1 = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
        Item item2 = ItemFixture.builder().id(2L).propertyNumber("11111111").build();

        // when, then
        assertThatThrownBy(() -> EquipmentItems.from(List.of(item1, item2)))
                .isExactlyInstanceOf(EquipmentItemsException.class);
    }

    @Test
    @DisplayName("자산 번호가 null인 품목으로 생성할 경우 예외")
    void from_nullPropertyNumber() {
        // given
        Item item = ItemFixture.builder().id(null).propertyNumber("22222222").build();

        // when, then
        assertThatThrownBy(() -> EquipmentItems.from(List.of(item)))
                .isExactlyInstanceOf(EquipmentItemsException.class);
    }

    @Test
    @DisplayName("특정 기자재 ID를 가진 품목의 자산 번호를 수정한다.")
    void updatePropertyNumberById() {
        // given
        Item item1 = ItemFixture.builder().id(1L).propertyNumber("11111111").build();
        Item item2 = ItemFixture.builder().id(2L).propertyNumber("22222222").build();

        EquipmentItems equipmentItems = EquipmentItems.from(List.of(item1, item2));

        // when
        equipmentItems.updatePropertyNumberById(1L, "33333333");

        // then
        assertThat(item1.getPropertyNumber()).isEqualTo("33333333");
    }
}