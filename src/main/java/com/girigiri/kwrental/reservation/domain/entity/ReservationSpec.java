package com.girigiri.kwrental.reservation.domain.entity;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @JoinColumn(name = "asset_id")
    private RentableAsset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    void setReservation(final Reservation reservation) {
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

    public void validateAmountIsSame(final int amount) {
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

    public boolean isRentFor(final String rentableName) {
        return this.asset.getName().equals(rentableName);
    }

    public boolean isReserved() {
        return status == ReservationSpecStatus.RESERVED;
    }

    public boolean isRented() {
        return status == ReservationSpecStatus.RENTED;
    }
}
