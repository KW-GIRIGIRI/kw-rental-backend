package com.girigiri.kwrental.asset.equipment.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class RentalQuantityException extends DomainException {
    public RentalQuantityException(final String message) {
        super(message);
    }
}
