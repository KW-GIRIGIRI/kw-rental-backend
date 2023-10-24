package com.girigiri.kwrental.common.exception;

import java.util.Arrays;

public class NotNullException extends BadRequestException {
    public NotNullException(final Object[] params) {
        super(String.format("null이면 안되는 값이 null로 입력됐습니다. %s", Arrays.toString(params)));
    }
}
