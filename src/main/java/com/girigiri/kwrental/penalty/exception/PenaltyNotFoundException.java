package com.girigiri.kwrental.penalty.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class PenaltyNotFoundException extends NotFoundException {
    public PenaltyNotFoundException() {
        super("해당하는 패널티를 찾지 못했습니다.");
    }
}
