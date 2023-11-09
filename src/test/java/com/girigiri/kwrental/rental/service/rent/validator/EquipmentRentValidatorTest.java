package com.girigiri.kwrental.rental.service.rent.validator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.exception.ItemNotAvailableException;
import com.girigiri.kwrental.item.service.ItemValidator;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest.EquipmentRentalSpecsRequest;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;
import com.girigiri.kwrental.reservation.service.ReservationValidator;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentRentValidatorTest {

	@Mock
	private ReservationValidator reservationValidator;
	@Mock
	private ReservationRetrieveService reservationRetrieveService;
	@Mock
	private ItemValidator itemValidator;
	@Mock
	private RentalSpecRepository rentalSpecRepository;
	@InjectMocks
	private EquipmentRentValidator equipmentRentValidator;

	@Test
	@DisplayName("입력한 품목 번호의 갯수와 대여 예약의 대여 갯수와 다르면 예외 발생")
	void validate_propertyNumberCountNotSameWithReservationSpecAmount() {
		// given
		final CreateEquipmentRentalRequest createEquipmentRentalRequest = createEquipmentRentalRequest();

		doThrow(ReservationSpecException.class).when(reservationValidator)
			.validateAmountIsSame(Map.of(2L, 1));

		// when, then
		assertThatThrownBy(() -> equipmentRentValidator.validate(createEquipmentRentalRequest))
			.isExactlyInstanceOf(ReservationSpecException.class);
	}

	@Test
	@DisplayName("입력된 품목 번호가 대여가 불가능하면 예외가 발생한다.")
	void validate_invalidPropertyNumbers() {
		// given
		final CreateEquipmentRentalRequest createEquipmentRentalRequest = createEquipmentRentalRequest();

		given(reservationRetrieveService.groupPropertyNumbersByEquipmentId(1L, Map.of(2L, Set.of("12345678"))))
			.willReturn(Map.of(3L, Set.of("12345678")));
		doThrow(ItemNotAvailableException.class).when(itemValidator)
			.validatePropertyNumbers(Map.of(3L, Set.of("12345678")));

		// when
		assertThatThrownBy(() -> equipmentRentValidator.validate(createEquipmentRentalRequest))
			.isExactlyInstanceOf(ItemNotAvailableException.class);
	}

	@Test
	@DisplayName("이미 대여된 기록이 있는 품목은 예외가 발생한다.")
	void validate_nowRented() {
		// given
		final CreateEquipmentRentalRequest createEquipmentRentalRequest = createEquipmentRentalRequest();

		given(reservationRetrieveService.groupPropertyNumbersByEquipmentId(1L, Map.of(2L, Set.of("12345678"))))
			.willReturn(Map.of(3L, Set.of("12345678")));
		final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.create();
		given(rentalSpecRepository.findByPropertyNumbers(Set.of("12345678")))
			.willReturn(List.of(rentalSpec));

		// when, then
		assertThatThrownBy(() -> equipmentRentValidator.validate(createEquipmentRentalRequest))
			.isExactlyInstanceOf(DuplicateRentalException.class);
	}

	private CreateEquipmentRentalRequest createEquipmentRentalRequest() {
		final EquipmentRentalSpecsRequest equipmentRentalSpecsRequest = EquipmentRentalSpecsRequest.builder()
			.reservationSpecId(2L)
			.propertyNumbers(List.of("12345678")).build();
		return CreateEquipmentRentalRequest.builder()
			.reservationId(1L)
			.rentalSpecsRequests(List.of(equipmentRentalSpecsRequest))
			.build();
	}
}