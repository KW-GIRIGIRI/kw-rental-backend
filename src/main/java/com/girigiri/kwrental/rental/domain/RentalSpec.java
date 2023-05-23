package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.common.SuperEntity;

import java.time.LocalDateTime;

public interface RentalSpec extends SuperEntity {
    boolean isNowRental();

    void setReturnDateTimeIfAnyReturned(LocalDateTime returnDateTime);

    boolean isUnavailableAfterReturn();

    boolean isOverdueReturned();

    Long getId();

    Long getReservationSpecId();

    Long getReservationId();

    RentalSpecStatus getStatus();

    void setStatus(RentalSpecStatus status);

    com.girigiri.kwrental.inventory.domain.RentalDateTime getAcceptDateTime();

    com.girigiri.kwrental.inventory.domain.RentalDateTime getReturnDateTime();
}
