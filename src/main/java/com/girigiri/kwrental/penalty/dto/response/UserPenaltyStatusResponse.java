package com.girigiri.kwrental.penalty.dto.response;

import com.girigiri.kwrental.penalty.domain.PenaltyStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserPenaltyStatusResponse {
    private boolean canUse;
    private String status;
    private LocalDate endDate;

    private UserPenaltyStatusResponse() {
    }

    public UserPenaltyStatusResponse(final boolean canUse, final PenaltyStatus status, final LocalDate endDate) {
        this.canUse = canUse;
        this.status = status.getMessage() == null ? null : status.getMessage();
        this.endDate = endDate;
    }
}
