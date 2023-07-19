package com.girigiri.kwrental.reservation.dto.response;

import java.util.List;
import java.util.Map;

import com.girigiri.kwrental.reservation.domain.ReservationCalendar;

public record ReservationsByEquipmentPerYearMonthResponse(
    Map<Integer, List<String>> reservations
) {
    public static ReservationsByEquipmentPerYearMonthResponse from(final ReservationCalendar calendar) {
        return new ReservationsByEquipmentPerYearMonthResponse(calendar.getCalendar());
    }
}
