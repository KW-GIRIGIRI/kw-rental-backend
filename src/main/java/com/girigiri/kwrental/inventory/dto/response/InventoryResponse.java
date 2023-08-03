package com.girigiri.kwrental.inventory.dto.response;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {

    private Long id;
    private String rentalPlace;
    private String modelName;
    private String category;
    private String maker;
    private String imgUrl;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private Integer amount;

    private Long equipmentId;

    private InventoryResponse(final Long id, final String rentalPlace, final String modelName, final String category, final String maker, final String imgUrl, final LocalDate rentalStartDate, final LocalDate rentalEndDate, final Integer amount, final Long equipmentId) {
        this.id = id;
        this.rentalPlace = rentalPlace;
        this.modelName = modelName;
        this.category = category;
        this.maker = maker;
        this.imgUrl = imgUrl;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.amount = amount;
        this.equipmentId = equipmentId;
    }

    public static InventoryResponse from(final Inventory inventory) {
        final Equipment equipment = inventory.getAsset().as(Equipment.class);
        return InventoryResponse.builder()
                .id(inventory.getId())
                .rentalPlace(equipment.getRentalPlace())
                .modelName(equipment.getName())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .equipmentId(equipment.getId())
                .imgUrl(equipment.getImgUrl())
                .rentalStartDate(inventory.getRentalPeriod().getRentalStartDate())
                .rentalEndDate(inventory.getRentalPeriod().getRentalEndDate())
                .amount(inventory.getRentalAmount().getAmount())
                .build();
    }
}