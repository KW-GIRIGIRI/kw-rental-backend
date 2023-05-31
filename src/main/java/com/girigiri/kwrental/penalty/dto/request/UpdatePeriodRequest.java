package com.girigiri.kwrental.penalty.dto.request;

import com.girigiri.kwrental.penalty.domain.PenaltyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdatePeriodRequest {
    @NotNull
    private PenaltyStatus status;

    private UpdatePeriodRequest() {
    }

    public UpdatePeriodRequest(final PenaltyStatus status) {
        this.status = status;
    }
}
