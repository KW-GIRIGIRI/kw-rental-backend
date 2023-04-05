package com.girigiri.kwrental.inventory.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class RentalDateException extends DomainException {
    public RentalDateException(String message) {
        super(message);
    }
}
