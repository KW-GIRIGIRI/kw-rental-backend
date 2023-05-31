package com.girigiri.kwrental.penalty.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class PenaltyStatusNotMatchException extends DomainException {
    public PenaltyStatusNotMatchException() {
        super("적절한 패널티 상태를 찾을 수 없습니다.");
    }
}
