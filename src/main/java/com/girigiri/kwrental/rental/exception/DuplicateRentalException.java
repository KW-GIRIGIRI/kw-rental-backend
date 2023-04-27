package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class DuplicateRentalException extends DomainException {
    public DuplicateRentalException() {
        super("이미 대여 중인 것을 대여할 수 없습니다.");
    }
}
