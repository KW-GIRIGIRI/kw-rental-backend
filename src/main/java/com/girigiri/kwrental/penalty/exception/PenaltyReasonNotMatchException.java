package com.girigiri.kwrental.penalty.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class PenaltyReasonNotMatchException extends DomainException {
    public PenaltyReasonNotMatchException() {
        super("적절한 페널티 사유를 할당할 수 없습니다.");
    }
}
