package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationTest {
    @Test
    @DisplayName("대여 기간이 다른 대여 예약 상세를 가질 수 없다.")
    void construct_invalidPeriod() {
        // given
        final ReservationSpec spec1 = ReservationSpecFixture.builder(null).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec spec2 = ReservationSpecFixture.builder(null).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();

        // when
        assertThatThrownBy(() -> Reservation
                .builder()
                .reservationSpecs(List.of(spec1, spec2))
                .name("name")
                .email("email@email.com")
                .purpose("purpose")
                .phoneNumber("01073015510")
                .build()
        ).isExactlyInstanceOf(ReservationException.class);
    }

    @Test
    @DisplayName("대여 예약이 종결됐는지 업데이트 한다.")
    void updateIfTerminated() {
        // given
        final ReservationSpec spec1 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RENTED).build();
        final ReservationSpec spec2 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RESERVED).build();
        final ReservationSpec spec3 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.OVERDUE_RENTED).build();

        final ReservationSpec spec4 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.CANCELED).build();
        final ReservationSpec spec5 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.ABNORMAL_RETURNED).build();
        final ReservationSpec spec6 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RETURNED).build();

        final Reservation notTerminated = ReservationFixture.create(List.of(spec1, spec2, spec3));
        final Reservation terminated = ReservationFixture.create(List.of(spec4, spec5, spec6));

        // when
        notTerminated.updateIfTerminated();
        terminated.updateIfTerminated();

        // then
        assertAll(
                () -> assertThat(notTerminated.isTerminated()).isFalse(),
                () -> assertThat(terminated.isTerminated()).isTrue()
        );
    }
}