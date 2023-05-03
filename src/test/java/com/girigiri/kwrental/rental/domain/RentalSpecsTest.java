package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalSpecsTest {

    @Test
    @DisplayName("대여 상세의 상태를 변경한다.")
    void setStatus() {
        // given
        final RentalSpec rentalSpec = RentalSpecFixture.builder().id(1L).build();
        final RentalSpecs rentalSpecs = RentalSpecs.from(List.of(rentalSpec));

        // when
        rentalSpecs.setStatus(1L, RentalStatus.RETURNED);

        // then
        assertThat(rentalSpec.getStatus()).isEqualTo(RentalStatus.RETURNED);
    }

    @Test
    @DisplayName("존재하지 않은 대여 상세의 상태를 변경하려고 하면 예외 발생.")
    void setStatus_notExists() {
        // given
        final RentalSpec rentalSpec = RentalSpecFixture.builder().id(1L).build();
        final RentalSpecs rentalSpecs = RentalSpecs.from(List.of(rentalSpec));

        // when, then
        assertThatThrownBy(() -> rentalSpecs.setStatus(2L, RentalStatus.RETURNED))
                .isExactlyInstanceOf(RentalSpecNotFoundException.class);
    }
}