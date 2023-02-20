package com.girigiri.kwrental.equipment;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Entity
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String maker;

    @Column(nullable = false)
    private String modelName;


    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private String description;

    @Column
    private String components;

    @Column
    private String purpose;

    @Column(nullable = false)
    private String rentalPlace;

    @Embedded
    private RentalQuantity rentalQuantity;

    @Embedded
    private RentalDays rentalDays;

    @Embedded
    private RentalTimes rentalTimes;

    protected Equipment() {
    }

    @Builder
    public Equipment(final Long id, final Category category, final String maker, final String modelName,
                     final RentalQuantity rentalQuantity,
                     final RentalDays rentalDays, final RentalTimes rentalTimes, final String imgUrl,
                     final String description, final String components,
                     final String purpose, final String rentalPlace) {
        this.id = id;
        this.category = category;
        this.maker = maker;
        this.modelName = modelName;
        this.rentalQuantity = rentalQuantity;
        this.rentalDays = rentalDays;
        this.rentalTimes = rentalTimes;
        this.imgUrl = imgUrl;
        this.description = description;
        this.components = components;
        this.purpose = purpose;
        this.rentalPlace = rentalPlace;
    }
}
