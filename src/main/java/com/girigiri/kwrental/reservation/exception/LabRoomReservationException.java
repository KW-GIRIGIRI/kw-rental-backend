package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomReservationException extends BadRequestException {
	public LabRoomReservationException(final String message) {
		super(message);
	}
}
