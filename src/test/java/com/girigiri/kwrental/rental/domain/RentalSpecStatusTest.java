package com.girigiri.kwrental.rental.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RentalSpecStatusTest {

    @ParameterizedTest
    @CsvSource({"RENTED,false", "RETURNED,true", "LOST,true", "BROKEN,true", "OVERDUE_RENTED,false", "OVERDUE_RETURNED,true"})
    void getAbnormalReturnedStatus(final RentalSpecStatus status, final boolean expect) {
        // given, when
        final boolean actual = status.isReturnedOrAbnormalReturned();

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void isReturnedOrAbnormalReturned() {
        // given, when
        final List<RentalSpecStatus> actual = RentalSpecStatus.getAbnormalReturnedStatus();

        // then
        assertThat(actual).containsExactlyInAnyOrder(RentalSpecStatus.LOST, RentalSpecStatus.BROKEN, RentalSpecStatus.OVERDUE_RETURNED);
    }
}