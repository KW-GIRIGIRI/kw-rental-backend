package com.girigiri.kwrental.equipment.exception;

public class InvalidCategoryException extends EquipmentException {

    public InvalidCategoryException() {
        super("카테고리가 잘못됐습니다.");
    }
}
