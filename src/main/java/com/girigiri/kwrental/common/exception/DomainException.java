package com.girigiri.kwrental.common.exception;

public class DomainException extends BadRequestException {
    public DomainException(String message) {
        super(message);
    }
}
