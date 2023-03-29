package com.girigiri.kwrental.testsupport;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.domain.Equipment.EquipmentBuilder;
import com.girigiri.kwrental.equipment.domain.RentalQuantity;

public class TestFixtures {
    private static final String modelName = "modelName";
    private static final String maker = "sony";
    private static final int totalQuantity = 2;
    private static final int remainingQuantity = 1;
    private static final RentalQuantity rentalQuantity = new RentalQuantity(totalQuantity, remainingQuantity);
    private static final String components = "줌렌즈, 단렌즈";
    private static final String purpose = "동영상 촬영";
    private static final String rentalPlace = "한울관 랩실";
    private static final String imgUrl = "www.naver.com";
    private static final String description = "description";

    public static EquipmentBuilder equipmentBuilder() {
        return Equipment.builder()
                .category(Category.CAMERA)
                .maker(maker)
                .modelName(modelName)
                .rentalQuantity(rentalQuantity)
                .imgUrl(imgUrl)
                .description(description)
                .components(components)
                .purpose(purpose)
                .rentalPlace(rentalPlace);
    }

    public static Equipment createEquipment() {
        return equipmentBuilder()
                .build();
    }
}
