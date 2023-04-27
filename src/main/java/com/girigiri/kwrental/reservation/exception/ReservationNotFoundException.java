package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class ReservationNotFoundException extends NotFoundException {
    public ReservationNotFoundException() {
        super("대여 예약을 찾지 못했습니다.");
    }
}
