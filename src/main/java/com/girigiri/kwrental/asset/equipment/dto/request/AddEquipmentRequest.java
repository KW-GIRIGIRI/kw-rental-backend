package com.girigiri.kwrental.asset.equipment.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddEquipmentRequest {

        @NotEmpty
        private final String rentalPlace;
        @NotEmpty
        private final String modelName;
        @NotEmpty
        private final String category;
        @NotEmpty
        private final String maker;
        @NotEmpty
        private final String imgUrl;
        @Length(max = 200)
        private final String components;
        @Length(max = 100)
        private final String purpose;
        @Length(max = 500)
        private final String description;
        @Min(1)
        private final Integer maxRentalDays;
        @Positive
        private final Integer totalQuantity;

        public AddEquipmentRequest(final String rentalPlace, final String modelName, final String category,
                                   final String maker, final String imgUrl,
                                   final String components, final String purpose, final String description,
                                   final Integer maxRentalDays, final Integer totalQuantity) {
                this.rentalPlace = rentalPlace;
                this.modelName = modelName;
                this.category = category;
                this.maker = maker;
                this.imgUrl = imgUrl;
                this.components = components;
                this.purpose = purpose;
                this.description = description;
                this.maxRentalDays = maxRentalDays;
                this.totalQuantity = totalQuantity;
        }
}
