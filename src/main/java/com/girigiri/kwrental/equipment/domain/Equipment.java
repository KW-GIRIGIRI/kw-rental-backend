package com.girigiri.kwrental.equipment.domain;

import com.girigiri.kwrental.asset.Rentable;
import com.girigiri.kwrental.asset.RentableAsset;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
public class Equipment extends RentableAsset {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String maker;

    @Column(nullable = false)
    private String imgUrl;

    private String description;

    private String components;

    private String purpose;

    @Column(nullable = false)
    private String rentalPlace;

    protected Equipment() {
    }

    @Builder
    public Equipment(final Long id, final Category category, final String maker, final String name,
                     final String imgUrl, final String description,
                     final String components, final String purpose, final String rentalPlace,
                     final Integer totalQuantity, final Integer maxRentalDays) {
        super(id, name, totalQuantity, maxRentalDays);
        this.category = category;
        this.maker = maker;
        this.imgUrl = imgUrl;
        this.description = description;
        this.components = components;
        this.purpose = purpose;
        this.rentalPlace = rentalPlace;
    }

    @Override
    public <T extends Rentable> T as(final Class<T> clazz) {
        if (Equipment.class != clazz) {
            throw new EquipmentException("해당 엔티티가 기자재가 아닙니다.");
        }
        return (T) this;
    }
}
