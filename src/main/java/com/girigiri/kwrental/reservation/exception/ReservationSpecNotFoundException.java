package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class ReservationSpecNotFoundException extends NotFoundException {
    public ReservationSpecNotFoundException() {
        super("대여 예약 상세를 찾지 못했습니다.");
    }
}
