package com.girigiri.kwrental.asset.labroom.dto.response;

import java.time.LocalDate;

public record LabRoomAvailableResponse(Long id, boolean available, LocalDate date) {
}
