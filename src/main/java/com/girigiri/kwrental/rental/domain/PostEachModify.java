package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;

public interface PostEachModify {
	void execute(RentalSpec rentalSpec, Reservation reservation);
}
