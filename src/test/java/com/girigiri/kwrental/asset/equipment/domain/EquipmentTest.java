package com.girigiri.kwrental.asset.equipment.domain;

import com.girigiri.kwrental.asset.exception.RentableAssetException;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EquipmentTest {

    @Test
    @DisplayName("대여 가능 갯수를 조절한다.")
    void adjustToRentalQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(5).build();

        // when
        equipment.adjustToRentalQuantity(1);

        // then
        assertThat(equipment.getRentableQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("대여 가능 갯수를 전체 갯수보다 크게 조정할 수 없다.")
    void adjustToRentalQuantity_biggerThanTotalQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(5).build();

        // when, then
        assertThatThrownBy(() -> equipment.adjustToRentalQuantity(6))
                .isExactlyInstanceOf(RentableAssetException.class);
    }

    @Test
    @DisplayName("대여 가능 갯수를 전체 갯수보다 크게 조정할 수 없다.")
    void adjustToRentalQuantity_negativeRentableQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(5).build();

        // when, then
        assertThatThrownBy(() -> equipment.adjustToRentalQuantity(-6))
                .isExactlyInstanceOf(RentableAssetException.class);
    }

    @Test
    @DisplayName("전체 갯수를 줄인다.")
    void reduceTotalCount() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(5).build();

        // when
        equipment.reduceTotalCount(4);

        // then
        assertThat(equipment.getTotalQuantity()).isEqualTo(6);
        assertThat(equipment.getRentableQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("전체 갯수를 대여 가능 갯수보다 작게 줄일 수 없다.")
    void reduceTotalCount_smallerThanRentableQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(5).build();

        // when, then
        assertThatThrownBy(() -> equipment.reduceTotalCount(6))
                .isExactlyInstanceOf(RentableAssetException.class);
    }

    @Test
    @DisplayName("전체 갯수를 0보다 작게 줄일 수 없다.")
    void reduceTotalCount_negativeTotalQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();

        // when, then
        assertThatThrownBy(() -> equipment.reduceTotalCount(11))
                .isExactlyInstanceOf(RentableAssetException.class);
    }

    @Test
    @DisplayName("전체 갯수를 늘린다.")
    void addTotalCount() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();

        // when
        equipment.addTotalCount(1);

        // then
        assertThat(equipment.getTotalQuantity()).isEqualTo(11);
        assertThat(equipment.getRentableQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("남은 개수를 계산한다.")
    void getRemainQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();

        // when
        int actual = equipment.getRemainQuantity(5);

        // then
        assertThat(actual).isEqualTo(5);
    }

    @Test
    @DisplayName("대여 예약된 건이 대여 가능 갯수보다 많으면 0을 반환")
    void getRemainQuantity_biggerThanRemainQuantity() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();

        // when
        int actual = equipment.getRemainQuantity(11);

        // then
        assertThat(actual).isEqualTo(0);
    }
}