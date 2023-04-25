package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
}