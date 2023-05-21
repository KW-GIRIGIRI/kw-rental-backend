package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.asset.Rentable;
import com.girigiri.kwrental.asset.RentableAsset;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RentalPeriod rentalPeriod;

    @Embedded
    private RentalAmount rentalAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = RentableAsset.class)
    @JoinColumn(name = "asset_id")
    private Rentable rentable;

    @Column(nullable = false)
    private Long memberId;

    protected Inventory() {
    }

    public Inventory(final Long id, final RentalPeriod rentalPeriod, final RentalAmount rentalAmount, final Rentable rentable, final Long memberId) {
        this.id = id;
        this.rentalPeriod = rentalPeriod;
        this.rentalAmount = rentalAmount;
        this.rentable = rentable;
        this.memberId = memberId;
    }

    public void setRentalPeriod(final RentalPeriod rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public void setRentalAmount(final RentalAmount rentalAmount) {
        this.rentalAmount = rentalAmount;
    }

    public boolean hasMemberId(final Long memberId) {
        return Objects.equals(this.memberId, memberId);
    }
}
