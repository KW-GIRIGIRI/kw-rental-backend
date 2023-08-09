package com.girigiri.kwrental.asset.equipment.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record AddEquipmentRequest(@NotEmpty String rentalPlace, @NotEmpty String modelName, @NotEmpty String category,
                                  @NotEmpty String maker, @NotEmpty String imgUrl, @Length(max = 200) String components,
                                  @Length(max = 100) String purpose, @Length(max = 500) String description,
                                  @Min(1) Integer maxRentalDays, @Positive Integer totalQuantity) {
}
