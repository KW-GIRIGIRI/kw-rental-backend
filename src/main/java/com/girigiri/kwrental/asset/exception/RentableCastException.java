package com.girigiri.kwrental.asset.exception;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.common.exception.DomainException;

public class RentableCastException extends DomainException {
    public <T extends Rentable> RentableCastException(final Class<? extends RentableAsset> entityClass, final Class<T> inputClass) {
        super(String.format("%s를 %s로 변환할 수 없습니다.", entityClass.getName(), inputClass.getName()));
    }
}
