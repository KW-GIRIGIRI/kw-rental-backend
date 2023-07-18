package com.girigiri.kwrental.penalty.dto.response;

import java.time.LocalDate;

import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.reservation.domain.RentalDateTime;

import lombok.Getter;

@Getter
public class UserPenaltyResponse {

    private Long id;
    private LocalDate acceptDate;
    private LocalDate returnDate;
    private String status;
    private String assetName;
    private PenaltyReason reason;

    private UserPenaltyResponse() {
    }

    public UserPenaltyResponse(final Long id, final RentalDateTime acceptDate, final RentalDateTime returnDate,
        final PenaltyPeriod period,
        final String assetName, final PenaltyReason reason) {
        this(id, acceptDate.toLocalDate(), returnDate.toLocalDate(), period.getStatus().getMessage(), assetName,
            reason);
    }

    public UserPenaltyResponse(final Long id, final LocalDate acceptDate, final LocalDate returnDate,
        final String status,
        final String assetName, final PenaltyReason reason) {
        this.id = id;
        this.acceptDate = acceptDate;
        this.returnDate = returnDate;
        this.status = status;
        this.assetName = assetName;
        this.reason = reason;
    }
}
