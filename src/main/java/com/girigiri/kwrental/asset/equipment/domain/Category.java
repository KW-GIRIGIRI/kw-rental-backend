package com.girigiri.kwrental.asset.equipment.domain;

import java.util.Arrays;

import com.girigiri.kwrental.asset.equipment.exception.InvalidCategoryException;

public enum Category {
    CAMERA, RECORDING, FILMING_ASSIST, VR, ETC;

    public static Category from(final String name) {
        return Arrays.stream(Category.values())
            .filter(category -> category.name().equals(name))
            .findFirst()
            .orElseThrow(InvalidCategoryException::new);
    }
}
