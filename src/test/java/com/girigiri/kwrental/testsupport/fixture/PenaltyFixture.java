package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;

import java.time.LocalDate;

import static com.girigiri.kwrental.penalty.domain.Penalty.PenaltyBuilder;

public class PenaltyFixture {

    public static Penalty create(final PenaltyReason reason) {
        return builder(reason).build();
    }

    public static PenaltyBuilder builder(final PenaltyReason reason) {
        final LocalDate now = LocalDate.now();
        return Penalty.builder()
                .period(new PenaltyPeriod(now, now.plusDays(1)))
                .reservationId(0L)
                .reservationSpecId(0L)
                .memberId(0L)
                .rentalSpecId(0L)
                .reason(reason);
    }
}
