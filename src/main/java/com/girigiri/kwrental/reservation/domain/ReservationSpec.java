package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.domain.RentableAsset;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = RentableAsset.class)
    @JoinColumn(name = "asset_id")
    private Rentable rentable;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected ReservationSpec() {
    }

    private ReservationSpec(final Long id, final RentalAmount amount, final RentalPeriod period,
                            final ReservationSpecStatus status, final Rentable rentable, final Reservation reservation) {
        this.id = id;
        this.amount = amount;
        this.period = period;
        this.status = status;
        this.rentable = rentable;
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
        if (!this.amount.equals(RentalAmount.ofPositive(amount)))
            throw new ReservationSpecException("대여 신청 갯수가 맞지 않습니다.");
    }

    public boolean isLegalReturnIn(final LocalDate date) {
        return period.isLegalReturnIn(date);
    }

    public void setStatus(final ReservationSpecStatus status) {
        this.status = status;
    }

    public LocalDate getEndDate() {
        return this.period.getRentalEndDate();
    }

    public void cancelAmount(final Integer amount) {
        if (this.status != ReservationSpecStatus.RESERVED) {
            throw new ReservationSpecException("대여 예약 상세 취소는 예약 상태에서만 가능합니다.");
        }
        this.amount = this.amount.subtract(RentalAmount.ofPositive(amount));
        if (this.amount.isZero()) {
            this.status = ReservationSpecStatus.CANCELED;
        }
    }

    public boolean isTerminated() {
        return status != ReservationSpecStatus.RESERVED
                && status != ReservationSpecStatus.RENTED
                && status != ReservationSpecStatus.OVERDUE_RENTED;
    }

    public boolean isReservedOrRented() {
        return this.status == ReservationSpecStatus.RESERVED || this.status == ReservationSpecStatus.RENTED;
    }
}
