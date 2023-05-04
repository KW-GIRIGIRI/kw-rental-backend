package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
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
        final LocalDateTime now = LocalDateTime.now();
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().acceptDateTime(now).returnDateTime(null).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().acceptDateTime(now).returnDateTime(now.plusSeconds(1)).build();

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
        final RentalSpec rentalSpec = RentalSpecFixture.builder().status(status).build();

        // when
        rentalSpec.setReturnDateTimeIfAnyReturned(LocalDateTime.now());

        // then
        assertThat(rentalSpec.getReturnDateTime() != null).isEqualTo(isNotNull);
    }
}