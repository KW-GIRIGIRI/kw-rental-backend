package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomReservationSpecNotOneException extends BadRequestException {
    public LabRoomReservationSpecNotOneException() {
        super("랩실 대여 예약 상세는 하나여야 합니다.");
    }
}
