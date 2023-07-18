package com.girigiri.kwrental.inventory.domain;

import java.util.Objects;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.reservation.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
