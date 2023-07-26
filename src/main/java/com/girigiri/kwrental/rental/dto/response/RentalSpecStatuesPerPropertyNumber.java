package com.girigiri.kwrental.rental.dto.response;

import java.util.List;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public record RentalSpecStatuesPerPropertyNumber(String propertyNumber, List<RentalSpecStatus> statuses) {

    public int getNormalReturnedCount() {
        return Math.toIntExact(statuses.stream()
            .filter(RentalSpecStatus::isNormalReturned)
            .count());
    }

    public int getAbnormalReturnedCount() {
        return Math.toIntExact(statuses.stream()
            .filter(RentalSpecStatus::isAbnormalReturned)
            .count());
    }
}
