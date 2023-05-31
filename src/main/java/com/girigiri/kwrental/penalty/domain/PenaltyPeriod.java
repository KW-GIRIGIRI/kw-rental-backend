package com.girigiri.kwrental.penalty.domain;

import com.girigiri.kwrental.penalty.exception.NegativePenaltyCountException;
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
        final LocalDate now = LocalDate.now();
        if (penaltyCountBefore < 0) {
            throw new NegativePenaltyCountException();
        }
        if (penaltyCountBefore == 0) {
            return new PenaltyPeriod(now, now.plusWeeks(1));
        }
        if (penaltyCountBefore == 1) {
            return new PenaltyPeriod(now, now.plusMonths(1));
        }
        if (penaltyCountBefore == 2) {
            return new PenaltyPeriod(now, now.plusMonths(6));
        }
        return new PenaltyPeriod(now, now.plusYears(1));
    }

    public PenaltyStatus getStatus() {
        return PenaltyStatus.of(startDate, endDate);
    }
}
