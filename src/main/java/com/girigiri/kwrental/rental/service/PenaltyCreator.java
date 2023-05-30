package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public interface PenaltyCreator {

    void create(Long memberId, Long reservationId, Long reservationSpecId, Long rentalSpecId, RentalSpecStatus status);
}
