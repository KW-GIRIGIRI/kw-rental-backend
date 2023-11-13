package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

import java.util.Collection;
import java.util.List;

public class PropertyNumberNotUniqueException extends BadRequestException {
    public PropertyNumberNotUniqueException(Collection<String> duplicatedPropertyNumbers) {
       super(String.format("자산 번호가 중복된 것 같습니다. 다음 자산번호를 확인해주세요. \n%s", String.join(", ", duplicatedPropertyNumbers)));
    }
}
