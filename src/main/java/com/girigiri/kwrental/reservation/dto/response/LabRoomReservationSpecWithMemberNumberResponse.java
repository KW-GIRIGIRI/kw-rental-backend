package com.girigiri.kwrental.reservation.dto.response;

import lombok.Getter;

@Getter
public class LabRoomReservationSpecWithMemberNumberResponse {

    private String renterName;
    private String memberNumber;
    private Integer rentalAmount;
    private String phoneNumber;

    private LabRoomReservationSpecWithMemberNumberResponse() {
    }

    public LabRoomReservationSpecWithMemberNumberResponse(final String renterName, final String memberNumber, final Integer rentalAmount, final String phoneNumber) {
        this.renterName = renterName;
        this.memberNumber = memberNumber;
        this.rentalAmount = rentalAmount;
        this.phoneNumber = phoneNumber;
    }
}
