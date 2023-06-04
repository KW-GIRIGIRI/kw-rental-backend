package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;

import lombok.Getter;

@Getter
public class ReservationInfoResponse {
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;

	private ReservationInfoResponse() {
	}

	private ReservationInfoResponse(Long id, String name, String phoneNumber, String email) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public static ReservationInfoResponse from(final Reservation reservation) {
		return new ReservationInfoResponse(reservation.getId(), reservation.getName(), reservation.getPhoneNumber(),
			reservation.getEmail());
	}
}
