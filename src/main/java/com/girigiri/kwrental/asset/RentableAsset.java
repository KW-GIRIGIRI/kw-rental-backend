package com.girigiri.kwrental.asset;

import com.girigiri.kwrental.asset.exception.RentableCastException;
import com.girigiri.kwrental.common.exception.NotNullException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "asset")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class RentableAsset implements Rentable {

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

    @Override
    public <T extends Rentable> T as(final Class<T> clazz) {
        if (this.getClass() != clazz) {
            throw new RentableCastException(this.getClass(), clazz);
        }
        return (T) this;
    }

    protected void validateNotNull(final Object... params) {
        final boolean anyIsNull = Arrays.stream(params)
                .anyMatch(Objects::isNull);
        if (anyIsNull) {
            throw new NotNullException(params);
        }
    }
}
