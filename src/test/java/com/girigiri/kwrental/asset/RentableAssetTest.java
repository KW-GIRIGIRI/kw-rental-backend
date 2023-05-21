package com.girigiri.kwrental.asset;

import com.girigiri.kwrental.asset.exception.RentableCastException;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class RentableAssetTest {

    @Test
    @DisplayName("대여일만큼 대여할 수 잇는지 판단한다.")
    void canRentFor() {
        // given
        final RentableAsset rentableAsset = EquipmentFixture.builder().maxRentalDays(2).build();

        // when
        final boolean biggerInput = rentableAsset.canRentFor(3);
        final boolean sameInput = rentableAsset.canRentFor(2);
        final boolean smallerInput = rentableAsset.canRentFor(1);

        // then
        assertThat(biggerInput).isFalse();
        assertThat(sameInput).isTrue();
        assertThat(smallerInput).isTrue();
    }

    @Test
    @DisplayName("구현체로 변환한다.")
    void as() {
        // given
        final Rentable rentable = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
        assertThatCode(() -> rentable.as(Equipment.class))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구현체가 맞지 않는 객체로 변환하려면 예외")
    void as_notEquipmentClass() {
        // given
        final Rentable rentable = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
        assertThatCode(() -> rentable.as(RentableAsset.class))
                .isExactlyInstanceOf(RentableCastException.class);
    }
}