package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.Category;
import com.girigiri.kwrental.equipment.Equipment;
import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import java.time.LocalTime;

public class TestFixtures {
    private static final String maker = "sony";
    private static final String modelName = "modelName";
    private static final int totalQuantity = 2;
    private static final int remainingQuantity = 1;
    private static final LocalTime from = LocalTime.of(1, 1, 1);
    private static final LocalTime to = LocalTime.of(1,1, 2);
    private static final String imgUrl = "www.naver.com";
    private static final String description = "description";

    public static Equipment createEquipment() {
        return Equipment.builder()
                .category(Category.CAMERA)
                .maker(maker)
                .modelName(modelName)
                .totalQuantity(totalQuantity)
                .remainingQuantity(remainingQuantity)
                .availableTimeFrom(from)
                .availableTimeTo(to)
                .imgUrl(imgUrl)
                .description(description).build();
    }

    public static EquipmentResponse createEquipmentResponse() {
        return EquipmentResponse.builder()
                .category(Category.CAMERA.name())
                .maker(maker)
                .modelName(modelName)
                .totalQuantity(totalQuantity)
                .remainingQuantity(remainingQuantity)
                .availableTimeFrom(from)
                .availableTimeTo(to)
                .imgUrl(imgUrl)
                .description(description).build();
    }
}
