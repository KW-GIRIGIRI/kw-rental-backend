package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public interface PenaltyService {

    void create(Long memberId, Long reservationId, Long reservationSpecId, Long rentalSpecId, RentalSpecStatus status);

    boolean hasOngoingPenalty(Long memberId);
}
