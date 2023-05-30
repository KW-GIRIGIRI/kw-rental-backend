package com.girigiri.kwrental.penalty.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class NegativePenaltyCountException extends DomainException {
    public NegativePenaltyCountException() {
        super("패널티 횟수가 음수가 될 수 없습니다.");
    }
}
