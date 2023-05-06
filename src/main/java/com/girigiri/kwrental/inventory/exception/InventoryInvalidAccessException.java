package com.girigiri.kwrental.inventory.exception;

import com.girigiri.kwrental.auth.exception.ForbiddenException;

public class InventoryInvalidAccessException extends ForbiddenException {

    public InventoryInvalidAccessException() {
        super("담은 기자재에 접근할 권리가 없습니다.");
    }
}
