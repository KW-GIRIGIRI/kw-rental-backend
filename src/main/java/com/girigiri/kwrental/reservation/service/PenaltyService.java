package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public interface PenaltyService {

    void createOrUpdate(Long memberId, Long reservationId, Long reservationSpecId, Long rentalSpecId,
        RentalSpecStatus status);

    boolean hasOngoingPenalty(Long memberId);

    void deleteByRentalSpecIdIfExists(Long rentalSpecId);
}
