package com.girigiri.kwrental.penalty.dto.response;

import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserPenaltyResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String assetName;
    private PenaltyReason reason;

    private UserPenaltyResponse() {
    }

    public UserPenaltyResponse(final Long id, final PenaltyPeriod period,
                               final String assetName, final PenaltyReason reason) {
        this(id, period.getStartDate(), period.getEndDate(), period.getStatus().getMessage(), assetName, reason);
    }

    private UserPenaltyResponse(final Long id, final LocalDate startDate, final LocalDate endDate, final String status,
                                final String assetName, final PenaltyReason reason) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.assetName = assetName;
        this.reason = reason;
    }
}
