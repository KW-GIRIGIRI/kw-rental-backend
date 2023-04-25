package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationCalendarTest {

    @Test
    @DisplayName("대여 상세를 예약 달력에 추가한다.")
    void addAll() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        final YearMonth now = YearMonth.now();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
                .period(new RentalPeriod(now.atDay(1), now.atDay(2))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment)
                .period(new RentalPeriod(now.atEndOfMonth(), now.atEndOfMonth().plusDays(1))).build();
        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment)
                .period(new RentalPeriod(now.atEndOfMonth().plusDays(1), now.atEndOfMonth().plusDays(2))).build();
        final Reservation reservation = ReservationFixture.create(List.of(reservationSpec1, reservationSpec2, reservationSpec3));
        final ReservationCalendar calendar = ReservationCalendar.from(now.atDay(1), now.atEndOfMonth());

        // when
        calendar.addAll(List.of(reservationSpec1, reservationSpec2, reservationSpec3));

        // then
        assertAll(
                () -> assertThat(calendar.getCalendar().get(1)).containsOnly(reservation.getName()),
                () -> assertThat(calendar.getCalendar().get(now.atEndOfMonth().getDayOfMonth())).containsOnly(reservation.getName())
        );
    }
}