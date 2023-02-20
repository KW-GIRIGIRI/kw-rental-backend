package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.Category;
import com.girigiri.kwrental.equipment.Equipment;
import com.girigiri.kwrental.equipment.RentalDays;
import com.girigiri.kwrental.equipment.RentalQuantity;
import com.girigiri.kwrental.equipment.RentalTimes;
import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.RentalDaysResponse;
import com.girigiri.kwrental.equipment.dto.RentalQuantityResponse;
import com.girigiri.kwrental.equipment.dto.RentalTimesResponse;
import java.time.LocalTime;

public class TestFixtures {
    private static final LocalTime from = LocalTime.of(1, 1, 1);
    private static final LocalTime to = LocalTime.of(1, 1, 2);
    private static final RentalTimes rentalTimes = new RentalTimes(from, to);
    private static final String modelName = "modelName";
    private static final String maker = "sony";
    private static final int totalQuantity = 2;
    private static final int remainingQuantity = 1;
    private static final RentalQuantity rentalQuantity = new RentalQuantity(totalQuantity, remainingQuantity);
    private static final String components = "줌렌즈, 단렌즈";
    private static final String purpose = "동영상 촬영";
    private static final int maxRentalDays = 4;
    private static final int maxDaysBeforeRental = 10;
    private static final int minDaysBeforeRental = 4;
    private static final RentalDays rentalDays = new RentalDays(maxRentalDays, maxDaysBeforeRental,
            minDaysBeforeRental);
    private static final String rentalPlace = "한울관 랩실";
    private static final String imgUrl = "www.naver.com";
    private static final String description = "description";

    public static Equipment createEquipment() {
        return Equipment.builder()
                .category(Category.CAMERA)
                .maker(maker)
                .modelName(modelName)
                .rentalDays(rentalDays)
                .rentalTimes(rentalTimes)
                .rentalQuantity(rentalQuantity)
                .imgUrl(imgUrl)
                .description(description)
                .components(components)
                .purpose(purpose)
                .rentalPlace(rentalPlace)
                .build();
    }

    public static EquipmentDetailResponse createEquipmentResponse() {
        return EquipmentDetailResponse.builder()
                .modelName(modelName)
                .category(Category.CAMERA.name())
                .maker(maker)
                .components(components)
                .purpose(purpose)
                .rentalPlace(rentalPlace)
                .imgUrl(imgUrl)
                .description(description)
                .rentalQuantity(RentalQuantityResponse.from(rentalQuantity))
                .rentalTimes(RentalTimesResponse.from(rentalTimes))
                .rentalDays(RentalDaysResponse.from(rentalDays))
                .build();
    }
}
