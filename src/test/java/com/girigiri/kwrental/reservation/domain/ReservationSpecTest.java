package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationSpecTest {

    @Test
    @DisplayName("대여 예약 상세 일부를 취소한다.")
    void cancelAmount() {
        // given
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null)
                .status(ReservationSpecStatus.RESERVED)
                .amount(RentalAmount.ofPositive(2)).build();

        // when
        reservationSpec.cancelAmount(1);

        // then
        assertAll(
                () -> assertThat(reservationSpec.getAmount()).isEqualTo(RentalAmount.ofPositive(1)),
                () -> assertThat(reservationSpec.getStatus()).isEqualTo(ReservationSpecStatus.RESERVED)
        );
    }

    @Test
    @DisplayName("대여 예약 상세 전부를 취소할 경우 취소 상태로 변한다.")
    void cancelAmount_allCanceled() {
        // given
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null)
                .status(ReservationSpecStatus.RESERVED)
                .amount(RentalAmount.ofPositive(2)).build();

        // when
        reservationSpec.cancelAmount(2);

        // then
        assertAll(
                () -> assertThat(reservationSpec.getAmount().getAmount()).isEqualTo(0),
                () -> assertThat(reservationSpec.getStatus()).isEqualTo(ReservationSpecStatus.CANCELED)
        );
    }

    @Test
    @DisplayName("대여 예약 상세가 예약 상태가 아닐 때 예약을 취소하려면 예외")
    void cancelAmount_notReserved() {
        // given
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null)
                .status(ReservationSpecStatus.RENTED)
                .amount(RentalAmount.ofPositive(2)).build();

        // when, then
        assertThatThrownBy(() -> reservationSpec.cancelAmount(1))
                .isExactlyInstanceOf(ReservationSpecException.class);
    }
}
