package com.girigiri.kwrental.reservation.service.reserve.creator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.asset.labroom.service.LabRoomRetriever;
import com.girigiri.kwrental.asset.labroom.service.LabRoomValidator;
import com.girigiri.kwrental.operation.service.OperationChecker;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomReservationCreatorTest {

	@Mock
	private LabRoomValidator labRoomValidator;
	@Mock
	private LabRoomRetriever labRoomRetriever;
	@Mock
	private OperationChecker operationChecker;
	@InjectMocks
	private LabRoomReservationCreator labRoomReservationCreator;

	@Test
	@DisplayName("랩실 대여 예약 객체를 생성한다.")
	void create() {
		// given
		final Long memberId = 1L;
		final AddLabRoomReservationRequest addLabRoomReservationRequest = createRequest();
		final LabRoom labRoom = LabRoomFixture.builder().name(addLabRoomReservationRequest.labRoomName()).build();

		given(labRoomRetriever.getLabRoomByName(addLabRoomReservationRequest.labRoomName()))
			.willReturn(labRoom);
		given(operationChecker.getOperateDates(addLabRoomReservationRequest.startDate(),
			addLabRoomReservationRequest.endDate()))
			.willReturn(Set.of(addLabRoomReservationRequest.startDate()));
		doNothing().when(labRoomValidator).validateDays(labRoom, Set.of(addLabRoomReservationRequest.startDate()));

		final Reservation expect = mapToReservation(memberId, addLabRoomReservationRequest, labRoom);

		// when
		final Reservation actual = labRoomReservationCreator.create(memberId, addLabRoomReservationRequest);

		// then
		assertThat(actual).usingRecursiveComparison().isEqualTo(expect);
	}

	@Test
	@DisplayName("운영되지 않고 있는 랩실로는 대여 예약 객체를 만들 수 없다.")
	void create_notAvailable() {
		// given
		final Long memberId = 1L;
		final AddLabRoomReservationRequest addLabRoomReservationRequest = createRequest();
		final LabRoom labRoom = LabRoomFixture.builder()
			.name(addLabRoomReservationRequest.labRoomName())
			.isAvailable(false)
			.build();

		given(labRoomRetriever.getLabRoomByName(addLabRoomReservationRequest.labRoomName()))
			.willReturn(labRoom);
		doThrow(LabRoomNotAvailableException.class).when(labRoomValidator).validateAvailable(labRoom);

		// when, then
		assertThatThrownBy(() -> labRoomReservationCreator.create(memberId, addLabRoomReservationRequest))
			.isExactlyInstanceOf(LabRoomNotAvailableException.class);
	}

	@Test
	@DisplayName("최대 대여 일수를 넘는 대여 예약 객체는 만들 수 없다.")
	void create_invalidRentalDays() {
		// given
		final Long memberId = 1L;
		final AddLabRoomReservationRequest addLabRoomReservationRequest = createRequest();
		final LabRoom labRoom = LabRoomFixture.builder()
			.name(addLabRoomReservationRequest.labRoomName())
			.build();

		given(labRoomRetriever.getLabRoomByName(addLabRoomReservationRequest.labRoomName()))
			.willReturn(labRoom);
		given(operationChecker.getOperateDates(any(), any())).willReturn(
			Set.of(addLabRoomReservationRequest.startDate()));
		doThrow(LabRoomNotAvailableException.class).when(labRoomValidator)
			.validateDays(eq(labRoom), eq(Set.of(addLabRoomReservationRequest.startDate())));

		// when, then
		assertThatThrownBy(() -> labRoomReservationCreator.create(memberId, addLabRoomReservationRequest))
			.isExactlyInstanceOf(LabRoomNotAvailableException.class);
	}

	private AddLabRoomReservationRequest createRequest() {
		return AddLabRoomReservationRequest.builder()
			.renterCount(1)
			.labRoomName("name")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.renterEmail("email@email.com")
			.rentalPurpose("purpose")
			.renterPhoneNumber("000-0000-0000")
			.renterName("renter").build();
	}

	private Reservation mapToReservation(final Long memberId,
		final AddLabRoomReservationRequest addLabRoomReservationRequest, final LabRoom labRoom) {
		final ReservationSpec spec = ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(addLabRoomReservationRequest.startDate(), addLabRoomReservationRequest.endDate()))
			.amount(RentalAmount.ofPositive(addLabRoomReservationRequest.renterCount()))
			.build();
		return ReservationFixture.builder(List.of(spec))
			.name(addLabRoomReservationRequest.renterName())
			.memberId(memberId)
			.email(addLabRoomReservationRequest.renterEmail())
			.purpose(addLabRoomReservationRequest.rentalPurpose())
			.phoneNumber(addLabRoomReservationRequest.renterPhoneNumber())
			.build();
	}
}