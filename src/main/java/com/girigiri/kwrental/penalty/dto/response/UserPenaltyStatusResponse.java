package com.girigiri.kwrental.penalty.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserPenaltyStatusResponse {
    private boolean canUse;
    private LocalDate endDate;

    private UserPenaltyStatusResponse() {
    }

    public UserPenaltyStatusResponse(final boolean canUse, final LocalDate endDate) {
        this.canUse = canUse;
        this.endDate = endDate;
    }
}
