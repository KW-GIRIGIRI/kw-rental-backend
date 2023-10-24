package com.girigiri.kwrental.inventory.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class InventoryNotFoundException extends NotFoundException {
    public InventoryNotFoundException() {
        super("담은 기자재를 찾을 수 없습니다.");
    }
}
