package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class ReservationSpecException extends DomainException {
    public ReservationSpecException(final String message) {
        super(message);
    }
}
