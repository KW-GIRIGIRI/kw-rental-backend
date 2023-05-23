package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RentalSpecTest {

    @Test
    @DisplayName("대여 상세가 대여중인지 확인한다.")
    void isNowRental() {
        // given
        final RentalDateTime now = RentalDateTime.now();
        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().acceptDateTime(now).returnDateTime(null).build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().acceptDateTime(now).returnDateTime(now.calculateDay(1)).build();

        // when
        final boolean nowRental = rentalSpec1.isNowRental();
        final boolean notRental = rentalSpec2.isNowRental();

        // then
        assertAll(
                () -> assertThat(nowRental).isTrue(),
                () -> assertThat(notRental).isFalse()
        );
    }

    @ParameterizedTest
    @CsvSource({"RETURNED,true", "OVERDUE_RETURNED,true", "LOST,true", "BROKEN,true", "RENTED,false", "OVERDUE_RENTED,false"})
    @DisplayName("대여 상세가 반납이 된 상태인 경우 반납 시간을 기록한다")
    void setReturnDateTime(final RentalSpecStatus status, final boolean isNotNull) {
        // given
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().status(status).build();

        // when
        rentalSpec.setReturnDateTimeIfAnyReturned(LocalDateTime.now());

        // then
        assertThat(rentalSpec.getReturnDateTime() != null).isEqualTo(isNotNull);
    }

    @ParameterizedTest
    @CsvSource({"RETURNED,false", "OVERDUE_RETURNED,true", "LOST,false", "BROKEN,false", "RENTED,false", "OVERDUE_RENTED,false"})
    @DisplayName("대여 상세가 반납이 된 상태인 경우 반납 시간을 기록한다")
    void isOverdueReturned(final RentalSpecStatus status, final boolean expect) {
        // given
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().status(status).build();

        // when
        final boolean actual = rentalSpec.isOverdueReturned();

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource({"RETURNED,false", "OVERDUE_RETURNED,false", "LOST,true", "BROKEN,true", "RENTED,false", "OVERDUE_RENTED,true"})
    @DisplayName("대여 상세가 반납이 된 상태인 경우 반납 시간을 기록한다")
    void isUnavailableAfterReturn(final RentalSpecStatus status, final boolean expect) {
        // given
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().status(status).build();

        // when
        final boolean actual = rentalSpec.isUnavailableAfterReturn();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}