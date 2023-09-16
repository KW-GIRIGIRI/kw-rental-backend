package com.girigiri.kwrental.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.girigiri.kwrental.inventory.exception.RentalDateException;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;

class RentalPeriodTest {


    @Test
    @DisplayName("반납 일자가 수령 일자보다 이전이면 예외가 발생")
    void create_endBeforeStart() {
        // given
        final LocalDate start = LocalDate.now().plusDays(2);
        final LocalDate end = LocalDate.now().plusDays(1);

        // when, then
        assertThatThrownBy(() -> new RentalPeriod(start, end))
                .isExactlyInstanceOf(RentalDateException.class);
    }

    @Test
    @DisplayName("대여 기간 중 특정 일자가 포함되는 지 판단한다.")
    void contains() {
        // given
        final LocalDate start = LocalDate.now();
        final LocalDate end = LocalDate.now().plusDays(2);
        final RentalPeriod rentalPeriod = new RentalPeriod(start, end);

        final LocalDate beforeStart = LocalDate.now().minusDays(1);
        final LocalDate equalsStart = LocalDate.now();
        final LocalDate midOfPeriod = LocalDate.now().plusDays(1);
        final LocalDate equalsEnd = LocalDate.now().plusDays(2);

        // when
        final boolean beforeStartExpect = rentalPeriod.contains(beforeStart);
        final boolean equalsStartExpect = rentalPeriod.contains(equalsStart);
        final boolean midOfPeriodExpect = rentalPeriod.contains(midOfPeriod);
        final boolean equalsEndExpect = rentalPeriod.contains(equalsEnd);
        final boolean afterEndExpect = rentalPeriod.contains(end.plusDays(1));

        // then
        assertAll(
                () -> assertThat(beforeStartExpect).isFalse(),
                () -> assertThat(equalsStartExpect).isTrue(),
                () -> assertThat(midOfPeriodExpect).isTrue(),
                () -> assertThat(equalsEndExpect).isFalse(),
                () -> assertThat(afterEndExpect).isFalse()
        );
    }

    @Test
    @DisplayName("특정 날짜가 정상 반납할 수 있는 지 판단한다.")
    void isLegalReturnIn() {
        // given
        final LocalDate start = LocalDate.now();
        final LocalDate mid = start.plusDays(1);
        final LocalDate end = LocalDate.now().plusDays(2);
        final RentalPeriod rentalPeriod = new RentalPeriod(start, end);

        final LocalDate illegal = end.plusDays(1);

        // when
        final boolean illegalReturn = rentalPeriod.isLegalReturnIn(illegal);
        final boolean legalReturn1 = rentalPeriod.isLegalReturnIn(end);
        final boolean legalReturn2 = rentalPeriod.isLegalReturnIn(start);
        final boolean legalReturn3 = rentalPeriod.isLegalReturnIn(mid);

        // then
        assertAll(
                () -> assertThat(illegalReturn).isFalse(),
                () -> assertThat(legalReturn1).isTrue(),
                () -> assertThat(legalReturn2).isTrue(),
                () -> assertThat(legalReturn3).isTrue()
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"0,2,1", "1,2,1", "1,3,0", "1,4,-1", "2,3,-1", "2,4,-1"})
    void compareTo(int start, int end, int expect) {
        // given
        final LocalDate now = LocalDate.now();
        final RentalPeriod period = new RentalPeriod(now.plusDays(1), now.plusDays(3));
        final RentalPeriod other = new RentalPeriod(now.plusDays(start), now.plusDays(end));

        // when
        final int actual = period.compareTo(other);

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("대여 기간에 있는 일자들을 모두 조회한다.")
    void getDates() {
        // given
        final LocalDate now = LocalDate.now();
        final RentalPeriod rentalPeriod = new RentalPeriod(now, now.plusDays(3));

        // when
        final Set<LocalDate> actual = rentalPeriod.getDates();

        // then
        assertThat(actual).containsExactlyInAnyOrder(now, now.plusDays(1), now.plusDays(2));
    }
}