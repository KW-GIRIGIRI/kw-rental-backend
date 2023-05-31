package com.girigiri.kwrental.penalty.domain;

import com.girigiri.kwrental.penalty.exception.PenaltyPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Embeddable
@EqualsAndHashCode
public class PenaltyPeriod {

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    protected PenaltyPeriod() {
    }

    public PenaltyPeriod(final LocalDate startDate, final LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new PenaltyPeriodException("일자에 빈 값이 올 수 없습니다.");
        }
        if (endDate.isBefore(startDate)) {
            throw new PenaltyPeriodException("반납일자는 대여일자보다 이전일 수 없습니다.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static PenaltyPeriod fromPenaltyCount(final int penaltyCountBefore) {
        final LocalDate startDate = LocalDate.now();
        final PenaltyStatus status = PenaltyStatus.from(penaltyCountBefore);
        final LocalDate endDate = status.getEndDate(startDate);
        return new PenaltyPeriod(startDate, endDate);
    }

    public PenaltyPeriod resize(final PenaltyStatus penaltyStatus) {
        final LocalDate resizedEndDate = penaltyStatus.getEndDate(this.startDate);
        return new PenaltyPeriod(this.startDate, resizedEndDate);
    }

    public PenaltyStatus getStatus() {
        return PenaltyStatus.of(startDate, endDate);
    }
}
