package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class NotSameRentableRentException extends BadRequestException {
    public NotSameRentableRentException() {
        super("대여하려는 대상이 통일되지 않았습니다.");
    }
}
