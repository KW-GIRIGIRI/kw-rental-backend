package com.girigiri.kwrental.rental.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;
import com.girigiri.kwrental.rental.exception.RentedStatusForReturnException;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

class RentalTest {

    @Test
    @DisplayName("정상 반납한다.")
    void returnByRentalSpecId() {
        // given
        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null).period(new RentalPeriod(now.minusDays(1), now)).id(1L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(2L).build();
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().id(3L).reservationSpecId(reservationSpec.getId()).build();
        final Rental rental = Rental.of(List.of(rentalSpec), reservation);

        // when
        rental.returnByRentalSpecId(rentalSpec.getId(), RentalSpecStatus.RETURNED);

        // then
        assertAll(
                () -> assertThat(rentalSpec.getStatus()).isEqualTo(RentalSpecStatus.RETURNED),
                () -> assertThat(rentalSpec.isNowRental()).isFalse()
        );
    }

    @Test
    @DisplayName("연체 처리한다.")
    void returnByRentalSpecId_overdueRented() {
        // given
        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null).period(new RentalPeriod(now.minusDays(1), now)).id(1L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(2L).build();
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().id(3L).reservationSpecId(reservationSpec.getId()).build();
        final Rental rental = Rental.of(List.of(rentalSpec), reservation);

        // when
        rental.returnByRentalSpecId(rentalSpec.getId(), RentalSpecStatus.OVERDUE_RENTED);

        // then
        assertAll(
                () -> assertThat(rentalSpec.getStatus()).isEqualTo(RentalSpecStatus.OVERDUE_RENTED),
                () -> assertThat(rentalSpec.isNowRental()).isTrue()
        );
    }

    @Test
    @DisplayName("연체 반납한다.")
    void returnByRentalSpecId_overdueReturned() {
        // given
        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null).period(new RentalPeriod(now.minusDays(2), now.minusDays(1))).id(1L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(2L).build();
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().id(3L).reservationSpecId(reservationSpec.getId()).build();
        final Rental rental = Rental.of(List.of(rentalSpec), reservation);

        // when
        rental.returnByRentalSpecId(rentalSpec.getId(), RentalSpecStatus.RETURNED);

        // then
        assertAll(
                () -> assertThat(rentalSpec.getStatus()).isEqualTo(RentalSpecStatus.OVERDUE_RETURNED),
                () -> assertThat(rentalSpec.isNowRental()).isFalse()
        );
    }

    @Test
    @DisplayName("존재하지 않은 대여 상세를 반납하려고 하면 예외 발생.")
    void returnByRentalSpecId_notExists() {
        // given
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(2L).build();
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().id(3L).build();
        final Rental rental = Rental.of(List.of(rentalSpec), reservation);

        // when, then
        assertThatThrownBy(() -> rental.returnByRentalSpecId(4L, RentalSpecStatus.RETURNED))
                .isExactlyInstanceOf(RentalSpecNotFoundException.class);
    }

    @Test
    @DisplayName("대여 상세를 반납할 때 대여중으로 반납하려면 예외 발생.")
    void returnByRentalSpecId_invalidStatus() {
        // given
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null).id(1L).build();
        final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).id(2L).build();
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().id(3L).build();
        final Rental rental = Rental.of(List.of(rentalSpec), reservation);

        // when, then
        assertThatThrownBy(() -> rental.returnByRentalSpecId(3L, RentalSpecStatus.RENTED))
                .isExactlyInstanceOf(RentedStatusForReturnException.class);
    }
}