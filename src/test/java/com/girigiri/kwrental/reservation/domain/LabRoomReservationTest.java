package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationException;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

class LabRoomReservationTest {

	@Test
	@DisplayName("랩실 대여 예약 객체를 생성한다.")
	void construct() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.create(labRoom);
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when, then
		assertThatCode(() -> new LabRoomReservation(reservation))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("랩실 대여 예약 객체의 대여 예약 상세가 1개가 아니면 예외")
	void construct_notOneSpec() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec1 = ReservationSpecFixture.create(labRoom);
		final ReservationSpec spec2 = ReservationSpecFixture.create(labRoom);
		final Reservation reservation = ReservationFixture.create(List.of(spec1, spec2));

		// when, then
		assertThatThrownBy(() -> new LabRoomReservation(reservation))
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약이 대여 가능한지 검증한다.")
	void validateWhenRent() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RESERVED)
			.period(new RentalPeriod(RentalDateTime.now(), RentalDateTime.now().calculateDay(1)))
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatCode(labRoomReservation::validateWhenRent)
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("랩실 대여 예약은 예약 중이 아니면 대여가 불가능하다.")
	void validateWhenRent_notReserved() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RETURNED)
			.period(new RentalPeriod(RentalDateTime.now(), RentalDateTime.now().calculateDay(1)))
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatThrownBy(labRoomReservation::validateWhenRent)
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약은 현재 대여 기간에 속하지 않으면 대여가 불가능하다.")
	void validateWhenReturn_invalidPeriod() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RESERVED)
			.period(new RentalPeriod(RentalDateTime.now().calculateDay(1), RentalDateTime.now().calculateDay(2)))
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatThrownBy(labRoomReservation::validateWhenRent)
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약이 반납이 가능한지 검증한다.")
	void validateWhenReturn() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatCode(labRoomReservation::validateWhenReturn)
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("랩실 대여 예약이 대여 중이 아니면 반납이 불가능하다.")
	void validateWhenReturn_notRented() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatThrownBy(labRoomReservation::validateWhenReturn)
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 예약을 모두 정상 반납 처리한다.")
	void normalReturnAll() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when
		labRoomReservation.normalReturnAll();

		// then
		assertThat(spec.getStatus()).isEqualTo(ReservationSpecStatus.RETURNED);
		assertThat(reservation.isTerminated()).isTrue();
	}

	@Test
	@DisplayName("랩실 대여 예약 상세가 대여 중이 아니면 정상 반납할 수 없다.")
	void normalReturnAll_notRented() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when, then
		assertThatThrownBy(labRoomReservation::normalReturnAll)
			.isExactlyInstanceOf(LabRoomReservationException.class);
	}

	@Test
	@DisplayName("랩실 대여 기간이 일치하는지 확인한다.")
	void has() {
		// given
		final LabRoom labRoom = LabRoomFixture.create();
		final RentalPeriod period = new RentalPeriod(RentalDateTime.now(), RentalDateTime.now().calculateDay(1));
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.period(period)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));
		final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);

		// when
		final boolean actual = labRoomReservation.has(period);

		// then
		assertThat(actual).isTrue();
	}
}