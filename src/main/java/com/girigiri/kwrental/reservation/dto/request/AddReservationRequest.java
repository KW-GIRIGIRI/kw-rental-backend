package com.girigiri.kwrental.reservation.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddReservationRequest {

    private String renterName;
    private String renterPhoneNumber;
    private String renterEmail;
    private String rentalPurpose;

    private AddReservationRequest() {
    }

    private AddReservationRequest(final String renterName, final String renterPhoneNumber,
                                  final String renterEmail, final String rentalPurpose) {
        this.renterName = renterName;
        this.renterPhoneNumber = renterPhoneNumber;
        this.renterEmail = renterEmail;
        this.rentalPurpose = rentalPurpose;
    }
}
