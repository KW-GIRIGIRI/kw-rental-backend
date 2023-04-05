package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}