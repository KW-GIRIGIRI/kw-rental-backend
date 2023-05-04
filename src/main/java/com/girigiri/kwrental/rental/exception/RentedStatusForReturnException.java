package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class RentedStatusForReturnException extends BadRequestException {
    public RentedStatusForReturnException() {
        super("반납을 할 때 대여중으로 상태를 처리할 수 없습니다.");
    }
}
