package com.girigiri.kwrental.rental.service.restore;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public interface PenaltyService {

    void createOrUpdate(Long memberId, Long reservationId, Long reservationSpecId, Long rentalSpecId,
        RentalSpecStatus status);

    void deleteByRentalSpecIdIfExists(Long rentalSpecId);
}
