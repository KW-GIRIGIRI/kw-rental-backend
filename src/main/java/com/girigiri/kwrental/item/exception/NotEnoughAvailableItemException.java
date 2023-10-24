package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class NotEnoughAvailableItemException extends DomainException {
    public NotEnoughAvailableItemException() {
        super("대여 가능한 품목이 부족합니다.");
    }
}
