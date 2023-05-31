package com.girigiri.kwrental.penalty.domain;

import com.girigiri.kwrental.penalty.exception.PenaltyStatusNotMatchException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public enum PenaltyStatus {
    ONE_WEEK("1주일 이용 금지"),
    ONE_MONTH("1개월 이용 금지"),
    SIX_MONTH("6개월 이용 금지"),
    ONE_YEAR("1년 이용 금지"),
    ;

    private final String message;

    PenaltyStatus(final String message) {
        this.message = message;
    }

    public static PenaltyStatus of(final LocalDate startDate, final LocalDate endDate) {
        final long days = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        if (days == 7) return ONE_WEEK;
        if (days <= 31) return ONE_MONTH;
        if (days <= 186) return SIX_MONTH;
        if (days <= 357) return ONE_YEAR;
        throw new PenaltyStatusNotMatchException(days);
    }
}
