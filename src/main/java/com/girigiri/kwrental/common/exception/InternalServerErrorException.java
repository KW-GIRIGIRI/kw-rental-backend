package com.girigiri.kwrental.common.exception;

public class InternalServerErrorException extends RuntimeException {

    protected InternalServerErrorException(final String message) {
        super(message);
    }
}
