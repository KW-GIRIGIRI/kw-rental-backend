package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class ReservationException extends DomainException {
    public ReservationException(final String message) {
        super(message);
    }
}
