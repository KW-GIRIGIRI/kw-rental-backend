package com.girigiri.kwrental.penalty.dto.response;

import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PenaltyHistoryResponse {

    private Long id;
    private String renterName;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String assetName;
    private PenaltyReason reason;

    private PenaltyHistoryResponse() {
    }

    public PenaltyHistoryResponse(final Long id, final String renterName, final PenaltyPeriod period,
                                  final String assetName, final PenaltyReason reason) {
        this(id, renterName, period.getStatus().getMessage(), period.getStartDate(), period.getEndDate(), assetName, reason);
    }

    private PenaltyHistoryResponse(final Long id, final String renterName, final String status,
                                   final LocalDate startDate, final LocalDate endDate, final String assetName, final PenaltyReason reason) {
        this.id = id;
        this.renterName = renterName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assetName = assetName;
        this.reason = reason;
    }
}
