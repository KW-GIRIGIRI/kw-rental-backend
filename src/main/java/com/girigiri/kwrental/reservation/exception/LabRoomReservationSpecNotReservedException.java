package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomReservationSpecNotReservedException extends BadRequestException {
    public LabRoomReservationSpecNotReservedException() {
        super("랩실 대여를 하려는 예약 상세는 예약 상태여야 합니다.");
    }
}
