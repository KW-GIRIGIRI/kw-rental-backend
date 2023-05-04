package com.girigiri.kwrental.rental.domain;

public enum RentalSpecStatus {

    RENTED, RETURNED, LOST, BROKEN, OVERDUE_RENTED, OVERDUE_RETURNED;

    public boolean isReturnedOrAbnormalReturned() {
        return this == RETURNED || isAbnormalReturned();
    }

    private boolean isAbnormalReturned() {
        return this == LOST || this == BROKEN || this == OVERDUE_RETURNED;
    }
}
