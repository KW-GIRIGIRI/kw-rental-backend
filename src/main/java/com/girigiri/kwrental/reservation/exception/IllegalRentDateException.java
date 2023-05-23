package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class IllegalRentDateException extends BadRequestException {
    public IllegalRentDateException() {
        super("대여 수령 날짜가 대여 신청 기간에 없습니다.");
    }
}
