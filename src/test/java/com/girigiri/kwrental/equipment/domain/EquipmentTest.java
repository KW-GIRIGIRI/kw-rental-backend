package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.asset.Rentable;
import com.girigiri.kwrental.asset.RentableAsset;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EquipmentTest {

    @Test
    @DisplayName("대여일만큼 기자재를 대여할 수 잇는지 판단한다.")
    void canRentFor() {
        // given
        final Equipment equipment = EquipmentFixture.builder().maxRentalDays(2).build();

        // when
        final boolean biggerInput = equipment.canRentFor(3);
        final boolean sameInput = equipment.canRentFor(2);
        final boolean smallerInput = equipment.canRentFor(1);

        // then
        assertThat(biggerInput).isFalse();
        assertThat(sameInput).isTrue();
        assertThat(smallerInput).isTrue();
    }

    @Test
    @DisplayName("기자재 구현체로 변환한다.")
    void as() {
        // given
        final Rentable rentable = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
        assertThatCode(() -> rentable.as(Equipment.class))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("기자재가 아닌 객체로 변환하려면 예외")
    void as_notEquipmentClass() {
        // given
        final Rentable rentable = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
        assertThatCode(() -> rentable.as(RentableAsset.class))
                .isExactlyInstanceOf(EquipmentException.class);
    }
}