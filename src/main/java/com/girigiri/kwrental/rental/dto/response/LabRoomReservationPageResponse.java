package com.girigiri.kwrental.rental.dto.response;

import java.util.List;

public record LabRoomReservationPageResponse(List<LabRoomReservationResponse> labRoomReservations, Integer page,
                                             List<String> endPoints) {
}
