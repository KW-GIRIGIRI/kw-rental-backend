package com.girigiri.kwrental.equipment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
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

    @Column(nullable = false, unique = true)
    private String modelName;

    @Column(nullable = false)
    private String imgUrl;


    private String description;

    private String components;

    private String purpose;

    @Column(nullable = false)
    private String rentalPlace;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer maxRentalDays;

    protected Equipment() {
    }

    @Builder
    public Equipment(final Long id, final Category category, final String maker, final String modelName,
                     final String imgUrl, final String description,
                     final String components, final String purpose, final String rentalPlace,
                     final Integer totalQuantity, final Integer maxRentalDays) {
        this.id = id;
        this.category = category;
        this.maker = maker;
        this.modelName = modelName;
        this.imgUrl = imgUrl;
        this.description = description;
        this.components = components;
        this.purpose = purpose;
        this.rentalPlace = rentalPlace;
        this.totalQuantity = totalQuantity;
        this.maxRentalDays = maxRentalDays;
    }

    public boolean canRentFor(final Integer rentalDays) {
        return this.maxRentalDays.compareTo(rentalDays) >= 0;
    }
}
