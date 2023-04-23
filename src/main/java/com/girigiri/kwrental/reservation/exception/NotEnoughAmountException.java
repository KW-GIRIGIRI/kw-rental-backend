package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class NotEnoughAmountException extends BadRequestException {
    public NotEnoughAmountException() {
        super("예약 가능한 수량이 부족해서 대여 예약을 할 수 없습니다.");
    }
}
