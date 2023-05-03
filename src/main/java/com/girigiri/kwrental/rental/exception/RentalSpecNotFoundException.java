package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class RentalSpecNotFoundException extends NotFoundException {
    public RentalSpecNotFoundException() {
        super("찾으려는 대여 상세를 찾을 수 없습니다.");
    }
}
