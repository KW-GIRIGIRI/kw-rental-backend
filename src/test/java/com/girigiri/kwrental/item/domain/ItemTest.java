package com.girigiri.kwrental.item.domain;

import com.girigiri.kwrental.item.exception.ItemException;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    @Test
    @DisplayName("자산번호를 다르게 수정")
    void updatePropertyNumber() {
        // given
        Item item = ItemFixture.builder().propertyNumber("1234567").build();

        // when
        item.updatePropertyNumber("7654321");

        // then
        assertThat(item.getPropertyNumber()).isEqualTo("7654321");
    }

    @Test
    @DisplayName("자산번호를 null로 수정 시 예외")
    void updatePropertyNumber_Null() {
        // given
        Item item = ItemFixture.builder().propertyNumber("1234567").build();

        // when, then
        assertThatThrownBy(() -> item.updatePropertyNumber(null))
                .isInstanceOf(ItemException.class);
    }

    @Test
    @DisplayName("자산번호를 빈 값으로 수정 시 예외")
    void updatePropertyNumber_empty() {
        // given
        Item item = ItemFixture.builder().propertyNumber("1234567").build();

        // when, then
        assertThatThrownBy(() -> item.updatePropertyNumber(""))
                .isInstanceOf(ItemException.class);
    }

    @Test
    @DisplayName("자산번호를 공백으로 수정 시 예외")
    void updatePropertyNumber_blank() {
        // given
        Item item = ItemFixture.builder().propertyNumber("1234567").build();

        // when, then
        assertThatThrownBy(() -> item.updatePropertyNumber("     "))
                .isInstanceOf(ItemException.class);
    }
}