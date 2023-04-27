package com.girigiri.kwrental.item.exception;

public class ItemNotAvailableException extends ItemException {
    public ItemNotAvailableException() {
        super("품목을 대여할 수 없습니다.");
    }
}
