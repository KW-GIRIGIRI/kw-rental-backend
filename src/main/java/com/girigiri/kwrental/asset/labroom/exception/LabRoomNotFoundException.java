package com.girigiri.kwrental.asset.labroom.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class LabRoomNotFoundException extends NotFoundException {
    public LabRoomNotFoundException() {
        super("해당 랩실을 찾을 수 없습니다.");
    }
}
