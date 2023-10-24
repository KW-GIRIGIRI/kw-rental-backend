package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomRentalSpecNotOneException extends BadRequestException {
    public LabRoomRentalSpecNotOneException() {
        super("랩실 대여 상세가 한개가 아닙니다.");
    }
}
