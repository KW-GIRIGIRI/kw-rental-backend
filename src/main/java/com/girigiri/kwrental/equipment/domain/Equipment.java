package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@DiscriminatorValue("equipment")
public class Equipment extends RentableAsset {

    @Enumerated(EnumType.STRING)
    private Category category;

    private String maker;

    private String imgUrl;

    private String description;

    private String components;

    private String purpose;

    private String rentalPlace;

    protected Equipment() {
    }

    @Builder
    public Equipment(final Long id, final Category category, final String maker, final String name,
                     final String imgUrl, final String description,
                     final String components, final String purpose, final String rentalPlace,
                     final Integer totalQuantity, final Integer maxRentalDays) {
        super(id, name, totalQuantity, maxRentalDays);
        validateNotNull(category, maker, imgUrl, rentalPlace);
        this.category = category;
        this.maker = maker;
        this.imgUrl = imgUrl;
        this.description = description;
        this.components = components;
        this.purpose = purpose;
        this.rentalPlace = rentalPlace;
    }
}
