package com.girigiri.kwrental.equipment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record UpdateEquipmentRequest(
        @NotEmpty String rentalPlace, @NotEmpty String modelName, @NotEmpty String category,
        @NotEmpty String maker, @NotEmpty String imgUrl, @Length(max = 200) String components,
        @Length(max = 100) String purpose, @Length(max = 500) String description, @Positive Integer totalQuantity,
        @Positive Integer maxRentalDays) {

}
