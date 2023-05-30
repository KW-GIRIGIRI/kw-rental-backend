package com.girigiri.kwrental.penalty.domain;

import com.girigiri.kwrental.penalty.exception.PenaltyReasonNotMatchException;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import java.util.Arrays;
import java.util.Set;

public enum PenaltyReason {
    BROKEN(Set.of(RentalSpecStatus.BROKEN)),
    LOST(Set.of(RentalSpecStatus.LOST)),
    OVERDUE_RENTED(Set.of(RentalSpecStatus.OVERDUE_RENTED, RentalSpecStatus.OVERDUE_RETURNED)),
    ;

    private final Set<RentalSpecStatus> rentalSpecStatuses;

    PenaltyReason(final Set<RentalSpecStatus> rentalSpecStatuses) {
        this.rentalSpecStatuses = rentalSpecStatuses;
    }

    public static PenaltyReason from(final RentalSpecStatus status) {
        return Arrays.stream(PenaltyReason.values())
                .filter(it -> it.rentalSpecStatuses.contains(status))
                .findFirst()
                .orElseThrow(PenaltyReasonNotMatchException::new);
    }
}
