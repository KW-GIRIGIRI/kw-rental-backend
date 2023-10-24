package com.girigiri.kwrental.inventory.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class RentalAmountException extends DomainException {
    public RentalAmountException(final String message) {
        super(message);
    }
}
