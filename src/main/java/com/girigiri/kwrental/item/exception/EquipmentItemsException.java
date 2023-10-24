package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class EquipmentItemsException extends BadRequestException {
    public EquipmentItemsException(String message) {
        super(message);
    }
}
