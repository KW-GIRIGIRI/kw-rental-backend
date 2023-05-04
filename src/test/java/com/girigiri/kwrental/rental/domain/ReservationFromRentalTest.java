package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.rental.exception.RentedStatusWhenReturnException;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationFromRentalTest {

    @Test
    @DisplayName("특정 대여 상세가 현재 날짜가 정상 반납 가능한 지 확인한다.")
    void nowIsLegalForReturn() {
        // given
        final LocalDate now = LocalDate.now();

        final ReservationSpec reservationsSpec1 = ReservationSpecFixture.builder(null).id(1L).period(new RentalPeriod(now.minusDays(1), now)).build();
        final Reservation reservation1 = ReservationFixture.create(List.of(reservationsSpec1));
        final ReservationFromRental reservationFromRental1 = ReservationFromRental.from(reservation1);

        final ReservationSpec reservationsSpec2 = ReservationSpecFixture.builder(null).id(2L).period(new RentalPeriod(now.minusDays(2), now.minusDays(2))).build();
        final Reservation reservation2 = ReservationFixture.create(List.of(reservationsSpec2));
        final ReservationFromRental reservationFromRental2 = ReservationFromRental.from(reservation2);

        // when
        final boolean legal = reservationFromRental1.nowIsLegalForReturn(reservationsSpec1.getId());
        final boolean illegal = reservationFromRental2.nowIsLegalForReturn(reservationsSpec2.getId());

        // then
        assertAll(
                () -> assertThat(legal).isTrue(),
                () -> assertThat(illegal).isFalse()
        );
    }

    @Test
    @DisplayName("정상 반납처리 되도록 상태를 변경한다.")
    void setStatusAfterReturn_returned() {
        // given
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation1 = ReservationFixture.create(List.of(reservationSpec1));
        final ReservationFromRental reservationFromRental1 = ReservationFromRental.from(reservation1);

        // then
        reservationFromRental1.setStatusAfterReturn(Map.of(reservationSpec1.getId(), List.of(RentalSpecStatus.RETURNED, RentalSpecStatus.RETURNED)));

        // when
        assertAll(
                () -> assertThat(reservation1.isTerminated()).isTrue(),
                () -> assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.RETURNED)
        );
    }

    @ParameterizedTest
    @EnumSource(value = RentalSpecStatus.class, names = {"OVERDUE_RETURNED", "LOST", "BROKEN"})
    @DisplayName("비정상 반납처리 되도록 상태를 변경한다.")
    void setStatusAfterReturn_abnormalReturned(final RentalSpecStatus abnormalReturnedStatus) {
        // given
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation1 = ReservationFixture.create(List.of(reservationSpec1));
        final ReservationFromRental reservationFromRental1 = ReservationFromRental.from(reservation1);

        // then
        reservationFromRental1.setStatusAfterReturn(Map.of(reservationSpec1.getId(), List.of(RentalSpecStatus.RETURNED, abnormalReturnedStatus)));

        // when
        assertAll(
                () -> assertThat(reservation1.isTerminated()).isTrue(),
                () -> assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.ABNORMAL_RETURNED)
        );
    }

    @ParameterizedTest
    @EnumSource(value = RentalSpecStatus.class, names = {"OVERDUE_RETURNED", "LOST", "BROKEN"})
    @DisplayName("만약 연체된 대여 상세가 존재하는 경우, 정상 반납과 비정상 반납이 존재해도 대여 예약 상세의 상태가 연체 처리 되도록 상태를 변경한다.")
    void setStatusAfterReturn_overdueRented(final RentalSpecStatus abnormalReturnedStatus) {
        // given
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation1 = ReservationFixture.create(List.of(reservationSpec1));
        final ReservationFromRental reservationFromRental1 = ReservationFromRental.from(reservation1);

        // then
        reservationFromRental1.setStatusAfterReturn(Map.of(reservationSpec1.getId(), List.of(RentalSpecStatus.RETURNED, abnormalReturnedStatus, RentalSpecStatus.OVERDUE_RENTED)));

        // when
        assertAll(
                () -> assertThat(reservation1.isTerminated()).isFalse(),
                () -> assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.OVERDUE_RENTED)
        );
    }

    @Test
    @DisplayName("반납된 대여 상세의 상태가 대여중이면 예외가 발생한다.")
    void setStatusAfterReturn_rentedException() {
        // given
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation1 = ReservationFixture.create(List.of(reservationSpec1));
        final ReservationFromRental reservationFromRental1 = ReservationFromRental.from(reservation1);

        // then, when
        assertThatThrownBy(() ->
                reservationFromRental1.setStatusAfterReturn(Map.of(reservationSpec1.getId(), List.of(RentalSpecStatus.RETURNED, RentalSpecStatus.RENTED)))
        ).isExactlyInstanceOf(RentedStatusWhenReturnException.class);
    }
}