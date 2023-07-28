package com.girigiri.kwrental.rental.service.restore;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PenaltySetter {
	private final PenaltyService penaltyService;
	private final ReservationService reservationService;

	public void setPenalty(final RentalSpec rentalSpec, final Reservation reservation) {
		final Long memberId = reservation.getMemberId();
		if (rentalSpec.isOverdueReturned() || rentalSpec.isUnavailableAfterReturn()) {
			penaltyService.createOrUpdate(memberId, rentalSpec.getReservationId(), rentalSpec.getReservationSpecId(),
				rentalSpec.getId(), rentalSpec.getStatus());
			reservationService.cancelReserved(memberId);
		} else {
			penaltyService.deleteByRentalSpecIdIfExists(rentalSpec.getId());
		}
	}
}
