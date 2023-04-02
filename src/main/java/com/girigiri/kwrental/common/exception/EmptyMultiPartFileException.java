package com.girigiri.kwrental.common.exception;

public class EmptyMultiPartFileException extends BadRequestException {

    public EmptyMultiPartFileException() {
        super("MultiPartFile이 null이거나 빈 값일 수 없습니다.");
    }
}
