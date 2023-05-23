package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomReservationNotRentedWhenReturnException extends BadRequestException {
    public LabRoomReservationNotRentedWhenReturnException() {
        super("반납하려는 대여 예약 상세가 대여 중 상태가 아닙니다.");
    }
}
