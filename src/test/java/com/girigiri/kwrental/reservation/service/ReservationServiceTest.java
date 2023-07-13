package com.girigiri.kwrental.reservation.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private ReservationSpecRepository reservationSpecRepository;
	@InjectMocks
	private ReservationService reservationService;

}