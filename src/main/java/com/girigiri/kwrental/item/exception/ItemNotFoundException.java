package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException() {
        super("해당 품목이 존재하지 않습니다.");
    }
}
