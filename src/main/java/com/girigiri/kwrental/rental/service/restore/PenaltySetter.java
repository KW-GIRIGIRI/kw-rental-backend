package com.girigiri.kwrental.rental.service.restore;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.service.ReservationCancelService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PenaltySetter {
	private final PenaltyService penaltyService;
	private final ReservationCancelService reservationCancelService;

	public void setPenalty(final RentalSpec rentalSpec, final Reservation reservation) {
		final Long memberId = reservation.getMemberId();
		if (rentalSpec.isOverdueReturned() || rentalSpec.isUnavailableAfterReturn()) {
			penaltyService.createOrUpdate(memberId, rentalSpec.getReservationId(), rentalSpec.getReservationSpecId(),
				rentalSpec.getId(), rentalSpec.getStatus());
			reservationCancelService.cancelReserved(memberId);
		} else {
			penaltyService.deleteByRentalSpecIdIfExists(rentalSpec.getId());
		}
	}
}
