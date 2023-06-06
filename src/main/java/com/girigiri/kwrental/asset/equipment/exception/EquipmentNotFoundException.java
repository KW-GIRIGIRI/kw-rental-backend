package com.girigiri.kwrental.asset.equipment.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class EquipmentNotFoundException extends NotFoundException {

    public EquipmentNotFoundException() {
        super("기자재가 존재하지 않습니다.");
    }
}
