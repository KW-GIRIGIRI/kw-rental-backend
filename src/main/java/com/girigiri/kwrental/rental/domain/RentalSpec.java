package com.girigiri.kwrental.rental.domain;

import java.time.LocalDateTime;

import com.girigiri.kwrental.common.SuperEntity;
import com.girigiri.kwrental.reservation.domain.RentalDateTime;

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
