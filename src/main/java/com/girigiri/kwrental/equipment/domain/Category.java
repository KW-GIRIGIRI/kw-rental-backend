package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.equipment.exception.InvalidCategoryException;
import java.util.Arrays;

public enum Category {
    CAMERA, RECORDING, FILMING_ASSIST, VR, ETC;

    public static Category from(final String name) {
        return Arrays.stream(Category.values())
                .filter(category -> category.name().equals(name))
                .findFirst()
                .orElseThrow(InvalidCategoryException::new);
    }
}
