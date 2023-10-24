package com.girigiri.kwrental.penalty.dto.response;

import java.time.LocalDate;

import com.girigiri.kwrental.penalty.domain.PenaltyStatus;

public record UserPenaltyStatusResponse(
    boolean canUse,
    String status,
    LocalDate endDate) {

    public UserPenaltyStatusResponse(final boolean canUse, final PenaltyStatus status, final LocalDate endDate) {
        this(canUse, status == null ? null : status.getMessage(), endDate);
    }
}
