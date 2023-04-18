package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class RentalSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private RentalAmount amount;

    @Column(nullable = false)
    private RentalPeriod period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected RentalSpec() {
    }

    @Builder
    private RentalSpec(final Long id, final RentalAmount amount, final RentalPeriod period, final Equipment equipment) {
        this.id = id;
        this.amount = amount;
        this.period = period;
        this.equipment = equipment;
    }
}
