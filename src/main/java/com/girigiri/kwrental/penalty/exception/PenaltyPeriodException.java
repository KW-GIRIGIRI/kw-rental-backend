package com.girigiri.kwrental.penalty.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class PenaltyPeriodException extends DomainException {
    public PenaltyPeriodException(final String message) {
        super(message);
    }
}
