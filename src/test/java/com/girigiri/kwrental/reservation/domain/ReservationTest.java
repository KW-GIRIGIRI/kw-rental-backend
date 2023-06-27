package com.girigiri.kwrental.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

class ReservationTest {
	@Test
	@DisplayName("대여 기간이 다른 대여 예약 상세를 가질 수 없다.")
	void construct_invalidPeriod() {
		// given
		final ReservationSpec spec1 = ReservationSpecFixture.builder(null)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1)))
			.build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(null)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2)))
			.build();

		// when
		assertThatThrownBy(() -> Reservation
			.builder()
			.reservationSpecs(List.of(spec1, spec2))
			.name("name")
			.email("email@email.com")
			.purpose("purpose")
			.phoneNumber("01073015510")
			.build()
		).isExactlyInstanceOf(ReservationException.class);
	}

	@Test
	@DisplayName("대여 예약이 종결됐는지 업데이트 한다.")
	void updateIfTerminated() {
		// given
		final ReservationSpec spec1 = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RENTED).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.RESERVED)
			.build();
		final ReservationSpec spec3 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.build();

		final ReservationSpec spec4 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final ReservationSpec spec5 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.ABNORMAL_RETURNED)
			.build();
		final ReservationSpec spec6 = ReservationSpecFixture.builder(null)
			.status(ReservationSpecStatus.RETURNED)
			.build();

		final Reservation notTerminated = ReservationFixture.create(List.of(spec1, spec2, spec3));
		final Reservation terminated = ReservationFixture.create(List.of(spec4, spec5, spec6));

		// when
		notTerminated.updateIfTerminated();
		terminated.updateIfTerminated();

		// then
		assertAll(
			() -> assertThat(notTerminated.isTerminated()).isFalse(),
			() -> assertThat(terminated.isTerminated()).isTrue()
		);
	}

	@Test
	@DisplayName("대여 예약을 수령처리한다.")
	void acceptAt() {
		// given
		final ReservationSpec spec = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RENTED).build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when
		reservation.acceptAt(LocalDateTime.now());

		// then
		assertThat(reservation.getAcceptDateTime()).isNotNull();
	}

	@Test
	@DisplayName("대여 예약 수령 여부를 조회한다.")
	void isAccepted() {
		// given
		final ReservationSpec spec = ReservationSpecFixture.builder(null).status(ReservationSpecStatus.RENTED).build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when
		final boolean actual = reservation.isAccepted();

		// then
		assertThat(actual).isFalse();
	}

	@Test
	@DisplayName("대여 예약의 대여 대상을 대여하는 지 확인한다.")
	void isOnlyRentFor() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("labRoom").build();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when
		final boolean actual = reservation.isOnlyRentFor(labRoom.getName());

		// then
		assertThat(actual).isTrue();
	}

	@Test
	@DisplayName("대여 예약 시작일을 알 수 있다.")
	void getStartDate() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("labRoom").build();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when
		final LocalDate actual = reservation.getStartDate();

		// then
		assertThat(actual).isEqualTo(spec.getStartDate());
	}

	@Test
	@DisplayName("대여 예약 종료일을 알 수 있다.")
	void getEndDate() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("labRoom").build();
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation = ReservationFixture.create(List.of(spec));

		// when
		final LocalDate actual = reservation.getEndDate();

		// then
		assertThat(actual).isEqualTo(spec.getEndDate());
	}
}