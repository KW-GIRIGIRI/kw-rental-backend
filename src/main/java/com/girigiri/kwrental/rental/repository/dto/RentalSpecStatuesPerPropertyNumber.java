package com.girigiri.kwrental.rental.repository.dto;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class RentalSpecStatuesPerPropertyNumber {

    private final String propertyNumber;
    private final List<RentalSpecStatus> statuses;

    public RentalSpecStatuesPerPropertyNumber(final String propertyNumber, final List<RentalSpecStatus> statuses) {
        this.propertyNumber = propertyNumber;
        this.statuses = statuses;
    }

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
