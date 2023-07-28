package com.girigiri.kwrental.rental.service.rent.creator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.LabRoomRentalSpec;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationService;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class LabRoomRentalSpecCreatorTest {

	@Mock
	private ReservationService reservationService;
	@InjectMocks
	private LabRoomRentalSpecCreator labRoomRentalSpecCreator;

	@Test
	@DisplayName("랩실 대여 상세를 생성한다.")
	void create() {
		// given
		final CreateLabRoomRentalRequest createLabRoomRentalRequest = CreateLabRoomRentalRequest.builder()
			.name("labRoomName")
			.reservationSpecIds(List.of(1L, 2L)).build();

		given(reservationService.getLabRoomReservationIdsByReservationSpecIds(
			createLabRoomRentalRequest.reservationSpecIds()))
			.willReturn(Map.of(1L, 3L, 2L, 4L));

		final LabRoomRentalSpec spec1 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(1L)
			.reservationId(3L)
			.build();
		final LabRoomRentalSpec spec2 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(2L)
			.reservationId(4L)
			.build();

		// when
		final List<AbstractRentalSpec> actual = labRoomRentalSpecCreator.create(createLabRoomRentalRequest);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptDateTime")
			.containsExactlyInAnyOrder(spec1, spec2);
	}
}