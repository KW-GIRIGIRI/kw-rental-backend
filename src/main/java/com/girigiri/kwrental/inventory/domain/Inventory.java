package com.girigiri.kwrental.inventory.domain;

import com.girigiri.kwrental.equipment.domain.Equipment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

// TODO: 2023/04/05 회원 관련 코드가 필요하다
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Equipment equipment;

    protected Inventory() {
    }

    public Inventory(final Long id, final RentalPeriod rentalPeriod, final RentalAmount rentalAmount, final Equipment equipment) {
        this.id = id;
        this.rentalPeriod = rentalPeriod;
        this.rentalAmount = rentalAmount;
        this.equipment = equipment;
    }

    public void setRentalPeriod(final RentalPeriod rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public void setRentalAmount(final RentalAmount rentalAmount) {
        this.rentalAmount = rentalAmount;
    }
}
