package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Builder
public class ReservationSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private RentalAmount amount;

    @Column(nullable = false)
    private RentalPeriod period;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationSpecStatus status = ReservationSpecStatus.RESERVED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected ReservationSpec() {
    }

    private ReservationSpec(final Long id, final RentalAmount amount, final RentalPeriod period,
                            final ReservationSpecStatus status, final Equipment equipment, final Reservation reservation) {
        this.id = id;
        this.amount = amount;
        this.period = period;
        this.status = status;
        this.equipment = equipment;
        this.reservation = reservation;
    }

    public void setReservation(final Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean containsDate(final LocalDate date) {
        return this.period.contains(date);
    }

    public LocalDate getStartDate() {
        return period.getRentalStartDate();
    }

    public boolean hasPeriod(final RentalPeriod period) {
        return this.period.equals(period);
    }

    public void validateAmount(final int amount) {
        if (this.amount.getAmount() != amount) throw new ReservationSpecException("대여 신청 갯수가 맞지 않습니다.");
    }

    public boolean isLegalReturnIn(final LocalDate date) {
        return period.isLegalReturnIn(date);
    }

    public void setStatus(final ReservationSpecStatus status) {
        this.status = status;
    }
}
