package com.girigiri.kwrental.reservation.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationCancelServiceTest {

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private ReservationSpecRepository reservationSpecRepository;

	@InjectMocks
	private ReservationCancelService reservationCancelService;

	@Test
	@DisplayName("특정 회원의 모든 대여 예약을 취소한다.")
	void cancelAll() {
		// given
		final ReservationSpec spec1 = ReservationSpecFixture.builder(null).id(1L).build();
		final ReservationSpec spec2 = ReservationSpecFixture.builder(null).id(2L).build();
		final Long memberId = 1L;
		final Reservation reservation1 = ReservationFixture.builder(List.of(spec1)).id(1L).memberId(memberId).build();
		final Reservation reservation2 = ReservationFixture.builder(List.of(spec2)).id(2L).memberId(memberId).build();
		given(reservationRepository.findNotTerminatedReservationsByMemberId(memberId))
			.willReturn(Set.of(reservation1, reservation2));
		given(reservationRepository.findByIdWithSpecs(reservation1.getId()))
			.willReturn(Optional.of(reservation1));
		given(reservationRepository.findByIdWithSpecs(reservation2.getId()))
			.willReturn(Optional.of(reservation2));

		// when
		assertThatCode(() -> reservationCancelService.cancelReserved(memberId))
			.doesNotThrowAnyException();
		assertThat(reservation1.isTerminated()).isTrue();
		assertThat(spec1.getStatus()).isEqualTo(ReservationSpecStatus.CANCELED);
		assertThat(reservation2.isTerminated()).isTrue();
		assertThat(spec2.getStatus()).isEqualTo(ReservationSpecStatus.CANCELED);
	}

	@Test
	@DisplayName("대여 예약 상세를 전량 취소하지만 대여 예약이 종결되지 않아 취소되지 않는다.")
	void cancelReservationSpec() {
		// given
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(null)
			.id(1L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final ReservationSpec afterCanceledSpec = ReservationSpecFixture.builder(null)
			.id(1L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		afterCanceledSpec.cancelAmount(2);
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(null)
			.id(2L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
			.terminated(false)
			.build();
		final Reservation afterCanceledReservation = ReservationFixture.builder(
			List.of(afterCanceledSpec, reservationSpec2)).terminated(false).build();
		reservation.updateIfTerminated();

		given(reservationSpecRepository.findById(any())).willReturn(Optional.of(reservationSpec1));
		doNothing().when(reservationSpecRepository).adjustAmountAndStatus(deepRefEq(afterCanceledSpec, "reservation"));
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));

		// when
		final Long actual = reservationCancelService.cancelReservationSpec(any(), 2);

		// then
		assertAll(
			() -> assertThat(reservationSpec1).usingRecursiveComparison().isEqualTo(afterCanceledSpec),
			() -> assertThat(reservation).usingRecursiveComparison().isEqualTo(afterCanceledReservation),
			() -> assertThat(actual).isEqualTo(reservationSpec1.getId())
		);
	}

	@Test
	@DisplayName("대여 예약 상세를 전량 취소하고 대여 예약이 취소된다.")
	void cancelReservationSpec_cancelReservation() {
		// given
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(null)
			.id(1L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		final ReservationSpec afterCanceledSpec = ReservationSpecFixture.builder(null)
			.id(1L)
			.amount(RentalAmount.ofPositive(2))
			.build();
		afterCanceledSpec.cancelAmount(2);
		final Reservation reservation = ReservationFixture.builder(List.of(reservationSpec)).terminated(false).build();
		final Reservation afterCanceledReservation = ReservationFixture.builder(List.of(afterCanceledSpec))
			.terminated(true)
			.build();
		reservation.updateIfTerminated();

		given(reservationSpecRepository.findById(any())).willReturn(Optional.of(reservationSpec));
		doNothing().when(reservationSpecRepository).adjustAmountAndStatus(deepRefEq(afterCanceledSpec, "reservation"));
		given(reservationRepository.findByIdWithSpecs(any())).willReturn(Optional.of(reservation));
		doNothing().when(reservationRepository).adjustTerminated(deepRefEq(afterCanceledReservation));

		// when
		final Long actual = reservationCancelService.cancelReservationSpec(any(), 2);

		// then
		assertAll(
			() -> assertThat(reservationSpec).usingRecursiveComparison().isEqualTo(afterCanceledSpec),
			() -> assertThat(reservation).usingRecursiveComparison().isEqualTo(afterCanceledReservation),
			() -> assertThat(actual).isEqualTo(reservationSpec.getId())
		);
	}
}