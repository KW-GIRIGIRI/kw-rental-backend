package com.girigiri.kwrental.common;

import com.girigiri.kwrental.common.exception.EntityCastException;
import com.girigiri.kwrental.common.exception.NotNullException;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractSuperEntity implements SuperEntity {
    protected void validateNotNull(final Object... params) {
        final boolean anyIsNull = Arrays.stream(params)
                .anyMatch(Objects::isNull);
        if (anyIsNull) {
            throw new NotNullException(params);
        }
    }

    @Override
    public <T extends SuperEntity> T as(final Class<T> clazz) {
        if (this.getClass() != clazz) {
            throw new EntityCastException(this.getClass(), clazz);
        }
        return (T) this;
    }
}
