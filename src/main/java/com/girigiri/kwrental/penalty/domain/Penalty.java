package com.girigiri.kwrental.penalty.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private Long reservationSpecId;

    @Column(nullable = false)
    private Long rentalSpecId;

    @Column(nullable = false)
    private Long memberId;

    @Embedded
    private PenaltyPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyReason reason;

    protected Penalty() {
    }

    @Builder
    private Penalty(final Long id, final Long reservationId, final Long reservationSpecId,
                    final Long rentalSpecId, final Long memberId, final PenaltyPeriod period, final PenaltyReason reason) {
        this.id = id;
        this.reservationId = reservationId;
        this.reservationSpecId = reservationSpecId;
        this.memberId = memberId;
        this.rentalSpecId = rentalSpecId;
        this.period = period;
        this.reason = reason;
    }

    public void updatePeriodByStatus(final PenaltyStatus penaltyStatus) {
        this.period = period.resize(penaltyStatus);
    }

    public void setReason(final PenaltyReason reason) {
        this.reason = reason;
    }
}
