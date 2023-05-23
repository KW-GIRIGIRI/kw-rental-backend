package com.girigiri.kwrental.common;

public interface SuperEntity {
    <T extends SuperEntity> T as(Class<T> clazz);
}
