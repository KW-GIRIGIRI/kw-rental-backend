package com.girigiri.kwrental.rental.service.rent.validator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationException;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomRentValidatorTest {

	@Mock
	private ReservationRetrieveService reservationRetrieveService;
	@InjectMocks
	private LabRoomRentValidator labRoomRentValidator;

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 모두 같은 랩실이어야 한다.")
	void validateLabRoomReservationForRent_notSameLabRoomName() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).name("labRoom1").build();
		final LabRoom labRoom2 = LabRoomFixture.builder().id(2L).name("labRoom2").build();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1).id(1L).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom2).id(2L).build();
		final LabRoomReservation reservation1 = new LabRoomReservation(ReservationFixture.create(List.of(spec1)));
		final LabRoomReservation reservation2 = new LabRoomReservation(ReservationFixture.create(List.of(spec2)));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRetrieveService.getLabRoomReservationBySpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		final CreateLabRoomRentalRequest request = CreateLabRoomRentalRequest.builder()
			.name(labRoom1.getName())
			.reservationSpecIds(ids).build();

		// when, then
		assertThatCode(() -> labRoomRentValidator.validate(request))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 모두 현재 대여 가능한 상태여야 한다.")
	void validateLabRoomReservationForRent_statusNotReserved() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1)
			.id(1L).status(ReservationSpecStatus.RESERVED).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom1)
			.id(2L).status(ReservationSpecStatus.CANCELED).build();
		final LabRoomReservation reservation1 = new LabRoomReservation(ReservationFixture.create(List.of(spec1)));
		final LabRoomReservation reservation2 = new LabRoomReservation(ReservationFixture.create(List.of(spec2)));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRetrieveService.getLabRoomReservationBySpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		final CreateLabRoomRentalRequest request = CreateLabRoomRentalRequest.builder()
			.name(labRoom1.getName())
			.reservationSpecIds(ids).build();

		// when, then
		assertThatCode(() -> labRoomRentValidator.validate(request))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("수령하려는 랩실 대여 예약들은 현재 날짜를 포함해야 한다.")
	void validateLabRoomReservationForRent_periodNotContainsNow() {
		// given
		final LabRoom labRoom1 = LabRoomFixture.builder().id(1L).build();
		final LocalDate now = LocalDate.now();
		final ReservationSpec spec1 = ReservationSpecFixture.builder(labRoom1)
			.id(1L).period(new RentalPeriod(now, now.plusDays(1))).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(labRoom1)
			.id(2L).period(new RentalPeriod(now.minusDays(2), now.minusDays(1))).build();
		final LabRoomReservation reservation1 = new LabRoomReservation(ReservationFixture.create(List.of(spec1)));
		final LabRoomReservation reservation2 = new LabRoomReservation(ReservationFixture.create(List.of(spec2)));

		final List<Long> ids = List.of(spec1.getId(), spec2.getId());
		given(reservationRetrieveService.getLabRoomReservationBySpecIds(ids))
			.willReturn(List.of(reservation1, reservation2));

		final CreateLabRoomRentalRequest request = CreateLabRoomRentalRequest.builder()
			.name(labRoom1.getName())
			.reservationSpecIds(ids).build();

		// when, then
		assertThatCode(() -> labRoomRentValidator.validate(request))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}
}