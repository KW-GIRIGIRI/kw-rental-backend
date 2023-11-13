package com.girigiri.kwrental.item.repository;

import java.util.List;

public interface ItemConstraintPolicy {

    void validateNotDeletedPropertyNumberIsUnique(List<String> propertyNumbers);
}
