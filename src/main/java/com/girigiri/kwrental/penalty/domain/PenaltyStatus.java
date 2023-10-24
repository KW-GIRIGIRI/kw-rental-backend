package com.girigiri.kwrental.penalty.domain;

import com.girigiri.kwrental.penalty.exception.NegativePenaltyCountException;
import com.girigiri.kwrental.penalty.exception.PenaltyStatusNotMatchException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.function.Function;

@Getter
public enum PenaltyStatus {
    ONE_WEEK("1주일 이용 금지", 0, now -> now.plusWeeks(1)),
    ONE_MONTH("1개월 이용 금지", 1, now -> now.plusMonths(1)),
    THREE_MONTH("3개월 이용 금지", 2, now -> now.plusMonths(3)),
    SIX_MONTH("6개월 이용 금지", 3, now -> now.plusMonths(6)),
    ONE_YEAR("1년 이용 금지", 4, now -> now.plusYears(1)),
    ;

    private final String message;
    private final int penaltyCount;
    private final Function<LocalDate, LocalDate> endDateCalculate;

    PenaltyStatus(final String message, final int penaltyCount, final Function<LocalDate, LocalDate> endDateCalculate) {
        this.message = message;
        this.penaltyCount = penaltyCount;
        this.endDateCalculate = endDateCalculate;
    }

    public static PenaltyStatus of(final LocalDate startDate, final LocalDate endDate) {
        final long days = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        if (days <= 8) return ONE_WEEK;
        if (days <= 32) return ONE_MONTH;
        if (days <= 96) return THREE_MONTH;
        if (days <= 192) return SIX_MONTH;
        if (days <= 367) return ONE_YEAR;
        throw new PenaltyStatusNotMatchException();
    }

    public static PenaltyStatus from(final int penaltyCount) {
        if (penaltyCount < 0) {
            throw new NegativePenaltyCountException();
        }
        return Arrays.stream(values())
                .filter(penaltyStatus -> penaltyStatus.penaltyCount == penaltyCount)
                .findFirst()
                .orElseThrow(PenaltyStatusNotMatchException::new);
    }

    public LocalDate getEndDate(final LocalDate startDate) {
        return this.endDateCalculate.apply(startDate);
    }
}
