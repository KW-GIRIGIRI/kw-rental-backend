package com.girigiri.kwrental.asset.domain;

import com.girigiri.kwrental.common.AbstractSuperEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "asset")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class RentableAsset extends AbstractSuperEntity implements Rentable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer maxRentalDays;


    protected RentableAsset() {
    }

    protected RentableAsset(final Long id, final String name, final Integer totalQuantity, final Integer maxRentalDays) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.maxRentalDays = maxRentalDays;
    }

    @Override
    public boolean canRentFor(final Integer rentalDays) {
        return this.maxRentalDays.compareTo(rentalDays) >= 0;
    }
}
