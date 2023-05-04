package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class RentedStatusWhenReturnException extends BadRequestException {
    public RentedStatusWhenReturnException() {
        super("반납 처리하려는 대여 상세의 상태가 대여 중이면 안됩니다.");
    }
}
