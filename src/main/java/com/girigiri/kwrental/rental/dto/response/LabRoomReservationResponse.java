package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LabRoomReservationResponse {

    private Long reservationId;
    private Long reservationSpecId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String renterName;
    private RentalSpecStatus reason;

    private LabRoomReservationResponse() {
    }

    public LabRoomReservationResponse(final Long reservationId, final Long reservationSpecId,
                                      final LocalDate startDate, final LocalDate endDate, final String renterName, final RentalSpecStatus reason) {
        this(reservationId, reservationSpecId, reason.isNormalReturned() ? "정상 반납" : "불량 반납", startDate, endDate, renterName, reason);
    }

    @Builder
    private LabRoomReservationResponse(final Long reservationId, final Long reservationSpecId, final String status,
                                       final LocalDate startDate, final LocalDate endDate, final String renterName, final RentalSpecStatus reason) {
        this.reservationId = reservationId;
        this.reservationSpecId = reservationSpecId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.renterName = renterName;
        this.reason = reason;
    }
}
