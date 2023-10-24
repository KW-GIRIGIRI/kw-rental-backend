package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class NotExpectedRentalStatusException extends BadRequestException {
    public NotExpectedRentalStatusException() {
        super("대여 상세의 상태가 예상하지 못한 값입니다.");
    }
}
