package com.girigiri.kwrental.equipment.exception;

public class EquipmentNotFoundException extends RuntimeException {

    public EquipmentNotFoundException() {
        super("기자재가 존재하지 않습니다.");
    }
}
