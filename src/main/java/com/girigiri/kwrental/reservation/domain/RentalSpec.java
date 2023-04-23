package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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
    private RentalSpec(final Long id, final RentalAmount amount, final RentalPeriod period, final Equipment equipment, final Reservation reservation) {
        this.id = id;
        this.amount = amount;
        this.period = period;
        this.equipment = equipment;
        this.reservation = reservation;
    }

    public void setReservation(final Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean containsDate(final LocalDate date) {
        return this.period.contains(date);
    }
}