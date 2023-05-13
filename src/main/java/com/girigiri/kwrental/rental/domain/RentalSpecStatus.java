package com.girigiri.kwrental.rental.domain;

import java.util.Arrays;
import java.util.List;

public enum RentalSpecStatus {

    RENTED, RETURNED, LOST, BROKEN, OVERDUE_RENTED, OVERDUE_RETURNED;

    public static List<RentalSpecStatus> getAbnormalReturnedStatus() {
        return Arrays.stream(RentalSpecStatus.values())
                .filter(RentalSpecStatus::isAbnormalReturned)
                .toList();
    }

    public boolean isReturnedOrAbnormalReturned() {
        return this == RETURNED || isAbnormalReturned();
    }

    private boolean isAbnormalReturned() {
        return this == LOST || this == BROKEN || this == OVERDUE_RETURNED;
    }
}
