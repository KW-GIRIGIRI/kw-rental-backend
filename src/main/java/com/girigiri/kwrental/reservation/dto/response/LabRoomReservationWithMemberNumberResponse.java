package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class LabRoomReservationWithMemberNumberResponse {

    private String labRoomName;
    private LocalDateTime acceptTime;
    private List<LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber;

    private LabRoomReservationWithMemberNumberResponse() {
    }

    public LabRoomReservationWithMemberNumberResponse(final String labRoomName, final RentalDateTime acceptTime, final List<LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber) {
        this.labRoomName = labRoomName;
        this.acceptTime = acceptTime == null ? null : acceptTime.toLocalDateTime();
        this.specsWithMemberNumber = specsWithMemberNumber;
    }
}
