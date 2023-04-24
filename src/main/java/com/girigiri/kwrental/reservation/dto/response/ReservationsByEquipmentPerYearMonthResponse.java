package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReservationsByEquipmentPerYearMonthResponse {

    private Map<Integer, List<String>> reservations;

    private ReservationsByEquipmentPerYearMonthResponse() {
    }

    public ReservationsByEquipmentPerYearMonthResponse(final Map<Integer, List<String>> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsByEquipmentPerYearMonthResponse from(final ReservationCalendar calendar) {
        return new ReservationsByEquipmentPerYearMonthResponse(calendar.getCalendar());
    }
}
