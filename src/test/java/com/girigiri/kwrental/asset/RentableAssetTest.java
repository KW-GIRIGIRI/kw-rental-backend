package com.girigiri.kwrental.asset;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.common.exception.EntityCastException;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

class RentableAssetTest {

    @Test
    @DisplayName("대여일만큼 대여할 수 잇는지 판단한다.")
    void canRentFor() {
	    // given
	    final RentableAsset asset = EquipmentFixture.builder().maxRentalDays(2).build();

	    // when
	    final boolean biggerInput = asset.canRentDaysFor(3);
	    final boolean sameInput = asset.canRentDaysFor(2);
	    final boolean smallerInput = asset.canRentDaysFor(1);

	    // then
	    assertThat(biggerInput).isFalse();
	    assertThat(sameInput).isTrue();
	    assertThat(smallerInput).isTrue();
    }

    @Test
    @DisplayName("구현체로 변환한다.")
    void as() {
        // given
	    final RentableAsset asset = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
	    assertThatCode(() -> asset.as(Equipment.class))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구현체가 맞지 않는 객체로 변환하려면 예외")
    void as_notEquipmentClass() {
        // given
	    final RentableAsset asset = EquipmentFixture.builder().maxRentalDays(2).build();

        // when, then
	    assertThatCode(() -> asset.as(RentableAsset.class))
                .isExactlyInstanceOf(EntityCastException.class);
    }
}