package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class ItemException extends DomainException {
    public ItemException(String message) {
        super(message);
    }
}
