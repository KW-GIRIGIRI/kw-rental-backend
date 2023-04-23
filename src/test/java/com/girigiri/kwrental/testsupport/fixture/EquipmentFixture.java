package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.domain.Equipment.EquipmentBuilder;

public class EquipmentFixture {
    private static final String modelName = "modelName";
    private static final String maker = "sony";
    private static final int totalQuantity = 2;
    private static final String components = "줌렌즈, 단렌즈";
    private static final String purpose = "동영상 촬영";
    private static final String rentalPlace = "한울관 랩실";
    private static final String imgUrl = "www.naver.com";
    private static final String description = "description";

    public static EquipmentBuilder builder() {
        return Equipment.builder()
                .category(Category.CAMERA)
                .maker(maker)
                .modelName(modelName)
                .totalQuantity(totalQuantity)
                .imgUrl(imgUrl)
                .description(description)
                .components(components)
                .purpose(purpose)
                .rentalPlace(rentalPlace)
                .maxRentalDays(1);
    }

    public static Equipment create() {
        return builder()
                .build();
    }
}
