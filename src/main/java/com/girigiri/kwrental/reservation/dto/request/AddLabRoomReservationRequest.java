package com.girigiri.kwrental.reservation.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AddLabRoomReservationRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String labRoomName;
    private String renterName;
    private String renterPhoneNumber;
    private String renterEmail;
    private String rentalPurpose;
    private Integer renterCount;

    private AddLabRoomReservationRequest() {
    }

    private AddLabRoomReservationRequest(final LocalDate startDate, final LocalDate endDate, final String labRoomName,
                                         final String renterName, final String renterPhoneNumber, final String renterEmail,
                                         final String rentalPurpose, final Integer renterCount) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.labRoomName = labRoomName;
        this.renterName = renterName;
        this.renterPhoneNumber = renterPhoneNumber;
        this.renterEmail = renterEmail;
        this.rentalPurpose = rentalPurpose;
        this.renterCount = renterCount;
    }
}
