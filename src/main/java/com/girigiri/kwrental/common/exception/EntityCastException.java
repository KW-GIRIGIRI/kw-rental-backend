package com.girigiri.kwrental.common.exception;

import com.girigiri.kwrental.common.SuperEntity;

public class EntityCastException extends DomainException {
    public <T extends SuperEntity> EntityCastException(final Class<? extends SuperEntity> entityClass, final Class<T> inputClass) {
        super(String.format("%s를 %s로 변환할 수 없습니다.", entityClass.getName(), inputClass.getName()));
    }
}
