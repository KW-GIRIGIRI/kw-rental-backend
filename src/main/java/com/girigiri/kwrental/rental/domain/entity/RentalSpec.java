package com.girigiri.kwrental.rental.domain.entity;

import java.time.LocalDateTime;

import com.girigiri.kwrental.common.SuperEntity;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

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

    RentalDateTime getAcceptDateTime();

    RentalDateTime getReturnDateTime();
}