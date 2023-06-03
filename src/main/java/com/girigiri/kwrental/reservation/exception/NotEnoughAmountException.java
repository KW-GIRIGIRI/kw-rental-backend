package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class NotEnoughAmountException extends BadRequestException {
    public NotEnoughAmountException() {
        super("남은 갯수가 부족합니다.");
    }
}
